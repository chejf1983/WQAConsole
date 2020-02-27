/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.io;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import wqa.bill.io.IIOFactory;
import wqa.bill.io.IAbstractIO;
import java.util.logging.Level;
import java.util.logging.Logger;
import nahon.comm.event.Event;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.console.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class IOManager implements EventListener {

    private static IOManager instance;

    private IOManager() {
        InitLogWatchDog();
    }

    public static IOManager GetInstance() {
        if (instance == null) {
            instance = new IOManager();
        }
        return instance;
    }

    // <editor-fold defaultstate="collapsed" desc="IO驱动"> 
    private static IIOFactory iofactory;

    public static void SetIOFactory(IIOFactory instance) {
        iofactory = instance;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="IO管理"> 
    private ArrayList<ShareIO> localio = new ArrayList();

    public ShareIO GetSIO(String name) {
        for (ShareIO io : localio) {
            if (io.GetConnectInfo().par[0].contentEquals(name)) {
                return io;
            }
        }
        return null;
    }

    public void OpenIO(String name, String bandrate) throws Exception {
        if (this.GetSIO(name) == null) {
            IAbstractIO io = iofactory.CreateIO("COM", new String[]{name, bandrate});
            if (io != null) {
                ShareIO sio = new ShareIO(io);
                sio.SendReceive.RegeditListener(new nahon.comm.event.EventListener<SDataPacket>() {
                    @Override
                    public void recevieEvent(Event<SDataPacket> event) {
                        WriterData(event.GetEvent());
                    }
                });
                localio.add(sio);
            }
        }

        this.GetSIO(name).Available();
    }

    public void CloseIO(String name) {
        if (this.GetSIO(name) != null) {
            this.GetSIO(name).UnAvailable();
        }
    }

    public String[] ListAllIO() {
        if (iofactory == null) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "没有IO驱动");
            return new String[0];
        }

        return iofactory.ListAllCom();
    }

    public ShareIO[] GetAllIO() {
        return localio.toArray(new ShareIO[0]);
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="IOlog处理"> 
    private final ReentrantLock log_lock = new ReentrantLock();

    public EventCenter<String> SendReceive = new EventCenter();

    private ArrayList<String> buffer_A = new ArrayList();
    private ArrayList<String> buffer_B = new ArrayList();
    private ArrayList<String> buffer_in = buffer_A;
    private ArrayList<String> buffer_out = buffer_B;

    private void WriterData(SDataPacket data) {
        log_lock.lock();
        try {
            String sdata = "";
            for (byte bdata : data.data) {
                sdata += String.format("%02X ", bdata);
            }

            buffer_in.add(
                    data.info.par[0] + ": " //串口号
                    + new SimpleDateFormat("HH:mm:ss SSS:  ").format(data.time) //时间
                    + data.type.toString() + ": " //发送还是接收
                    + sdata); //数据
        } finally {
            log_lock.unlock();
        }
    }

    private void InitLogWatchDog() {
        WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //检查是否有输入log
                    if (buffer_in.size() > 0) {
                        //交换输入输出buffer
                        log_lock.lock();
                        try {
                            ArrayList<String> tmp = buffer_in;
                            buffer_in = buffer_out;
                            buffer_out = tmp;
                        } finally {
                            log_lock.unlock();
                        }

                        try {
                            for (String log : buffer_out) {
                                //打印log
                                SendReceive.CreateEvent(log);
                            }
                        } catch (Exception ex) {
                            LogCenter.Instance().SendFaultReport(Level.SEVERE, "刷新错误", ex);
                        }

                        buffer_out.clear();
                    }

                    //等待400ms
                    try {
                        TimeUnit.MILLISECONDS.sleep(400);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(IOManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
    }
    // </editor-fold>   
}
