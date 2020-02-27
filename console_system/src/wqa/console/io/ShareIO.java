/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.io;

import java.util.concurrent.locks.ReentrantLock;
import nahon.comm.event.EventCenter;
import wqa.console.io.SDataPacket.IOEvent;
import wqa.bill.io.IAbstractIO;

/**
 *
 * @author chejf
 */
public class ShareIO {

    private IAbstractIO io;
    private final ReentrantLock share_lock = new ReentrantLock(true);

    public EventCenter<SDataPacket> SendReceive = new EventCenter();

    public ShareIO(IAbstractIO io) {
        this.io = io;
    }

    // <editor-fold defaultstate="collapsed" desc="IO控制">       
    public boolean IsClosed() {
        return this.io.IsClosed();
    }

    public void Lock() throws Exception {
        if (!this.isavailable) {
            throw new Exception("串口被关闭");
        }
        share_lock.lock();
    }

    public void UnLock() {
        if (share_lock.isLocked()) {
            share_lock.unlock();
        }
    }

    public void SendData(byte[] data) throws Exception {
        if (!this.io.IsClosed()) {
            byte[] tmp = new byte[data.length];
            System.arraycopy(data, 0, tmp, 0, data.length);
            this.SendReceive.CreateEventAsync(new SDataPacket(this.GetConnectInfo(), IOEvent.Send, tmp));
            this.io.SendData(data);
        }
    }

    public int ReceiveData(byte[] data, int timeout) throws Exception {
        if (!this.io.IsClosed()) {
            int reclen = this.io.ReceiveData(data, timeout);
            if (reclen > 0) {
                byte[] tmp = new byte[reclen];
                System.arraycopy(data, 0, tmp, 0, reclen);
                this.SendReceive.CreateEventAsync(new SDataPacket(this.GetConnectInfo(), IOEvent.Receive, tmp));
            }
            return reclen;
        } else {
            return 0;
        }
    }

    public SIOInfo GetConnectInfo() {
        return this.io.GetConnectInfo();
    }

    public int MaxBuffersize() {
        return this.io.MaxBuffersize();
    }// </editor-fold>  

    public void Available() throws Exception {
        this.io.Open();
        this.isavailable = true;
    }

    public void UnAvailable() {
        this.io.Close();
        this.isavailable = false;
    }

    private boolean isavailable = false;

    public boolean IsAvailable() {
        return this.isavailable;
    }
}
