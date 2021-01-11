/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.control;

import java.util.logging.Level;
import modebus.pro.ModeBus_Base;
import modebus.pro.NahonConvert;
import modebus.pro.Register;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;

/**
 *
 * @author chejf
 */
public class ConsoleControl {

    public EventCenter<CollectData> CollectData = new EventCenter();
    public String[] showName = new String[8];
    final ModeBus_Base instance;
    public ConsoleDB LocalDB = new ConsoleDB(this);
    public ConsolHistory History = new ConsolHistory(this);

    public ConsoleControl(ModeBus_Base instance) {
        this.instance = instance;
        for (int i = 0; i < showName.length; i++) {
            this.showName[i] = "Data" + i;
        }
    }

    public int GetAddr() {
        return this.instance.GetCurrentAddr();
    }

    public void Collect() {
        this.instance.io_lock.lock();
        try {
            byte[] buffer = this.instance.ReadMemory(0x00, showName.length * 2, 200);
            CollectData data = new CollectData(showName.length);
            for (int i = 0; i < showName.length; i++) {
                data.datas[i] = NahonConvert.ByteArrayToFloat(buffer, i * 4);
                data.datas[i] = NahonConvert.TimData(data.datas[i], 2);
            }
            CollectData.CreateEvent(data);
            LocalDB.SaveData(data);
        } catch (Exception ex) {
            CollectData.CreateEvent(new CollectData(8));
            this.SetMessage("采集失败");
        } finally {
            this.instance.io_lock.unlock();
        }
    }

    public int[] ShowHistoryNum() {
        this.instance.io_lock.lock();
        try {
            Register LOGNUM = new Register(0x40, 8);
            byte[] data = this.instance.ReadMemory(LOGNUM.reg_add, LOGNUM.reg_num, ModeBus_Base.def_timeout);
            int[] log_num = new int[4];
            for (int i = 0; i < log_num.length; i++) {
                log_num[i] = NahonConvert.ByteArrayToUShort(data, i * 2);
            }
            return log_num;
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "采集失败");
            return new int[0];
        } finally {
            this.instance.io_lock.unlock();
        }
    }

    private String message;

    void SetMessage(String info) {
        message = info;
    }

    public String GetMessage() {
        String tmp = message;
        message = "";
        return tmp;
    }
}
