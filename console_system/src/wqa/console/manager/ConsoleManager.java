/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.manager;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import modebus.pro.ModeBus_Base;
import nahon.comm.faultsystem.LogCenter;
import wqa.console.control.ConsoleControl;
import wqa.console.io.IOManager;
import wqa.console.io.ShareIO;
import wqa.console.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class ConsoleManager {

    public ArrayList<ConsoleControl> control_list = new ArrayList();
    private final ReentrantLock list_lock = new ReentrantLock(true);
    public Collector CollectorInstance = new Collector();
    private ProcessData lastprocess = ProcessData.EmptyProcess();

//    private int max_addr = 0x10;

    public ProcessData Search() {
        if (!lastprocess.isfinished) {
            return lastprocess;
        }

        lastprocess = new ProcessData();
        WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                list_lock.lock();
                try {
                    control_list.clear();
                    ShareIO[] iolist = IOManager.GetInstance().GetAllIO();
//                    lastprocess.total_len = iolist.length * max_addr;
                    lastprocess.total_len = iolist.length;
                    for (ShareIO io : iolist) {
                        search(io, lastprocess);
                    }
                } finally {
                    lastprocess.isfinished = true;
                    list_lock.unlock();
                    CollectorInstance.Start();
                }
            }
        });
        return lastprocess;
    }

    private void search(ShareIO io, ProcessData lastprocess) {
//        for (int i = 1; i < max_addr; i++) {
            try {
                ModeBus_Base modeBus_Base = new ModeBus_Base(io, (byte) 0);
                if (modeBus_Base.SearchDev(200)) {
                    control_list.add(new ConsoleControl(modeBus_Base));
                }
//                this.CollectorInstance.Collect();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            lastprocess.current_len++;
//        }
    }

    public void CollectAll() {
        list_lock.lock();
        try {
            for (int i = 0; i < this.control_list.size(); i++) {
                this.control_list.get(i).Collect();
            }
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        } finally {
            list_lock.unlock();
        }
    }

    public void SaveTo(String path) {
        list_lock.lock();
        try {
            for (int i = 0; i < this.control_list.size(); i++) {
                this.control_list.get(i).LocalDB.SaveToExcel(path);
            }
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        } finally {
            list_lock.unlock();
        }
    }
}
