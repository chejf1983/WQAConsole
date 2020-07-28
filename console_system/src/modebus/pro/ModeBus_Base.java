/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.pro;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import wqa.console.io.ShareIO;

/**
 *
 * @author chejf
 */
public class ModeBus_Base {

    protected ShareIO io;
    protected byte addr = 0x00;
    public static final int def_timeout = 300;
    public final int max_pack_len = 100;
    public final ReentrantLock io_lock = new ReentrantLock(true);

    public ModeBus_Base(ShareIO io, byte addr) {
        this.io = io;
        this.addr = addr;
    }

    public ShareIO GetIO() {
        return this.io;
    }

    public byte GetCurrentAddr() {
        return this.addr;
    }

    public boolean SearchDev(int timeout) throws Exception {
        //尝试读取一个数据
        return this.readmemory(0x00, 0x02, timeout).length > 0;
    }

    private byte[] readmemory(int memaddr, int mem_num, int timeout) throws Exception {
        //发送读取命令
        this.io.SendData(ModbusPacket.ReadPacket(this.addr, memaddr, mem_num));
//        TimeUnit.MILLISECONDS.sleep(100);
        //等待返回结果
        byte[] rcbuffer = new byte[20480];
        int ret_len = 0;
        while (!ModbusPacket.CheckCRC(rcbuffer, ret_len)) {
            byte[] tmpbuffer = new byte[1000];
            int tmp_len = this.io.ReceiveData(tmpbuffer, timeout);
            //如果一次超时时间内收不到任何数据，跳出循环
            if (tmp_len <= 0) {
                break;
            }
            System.arraycopy(tmpbuffer, 0, rcbuffer, ret_len, tmp_len);
            ret_len += tmp_len;
            if (ret_len > max_pack_len) {
                break;
            }
        }

        //检查是否是完整包
        if (!ModbusPacket.CheckCRC(rcbuffer, ret_len)) {
            return new byte[0];
        }

        if (rcbuffer[0] != this.addr) {
            return new byte[0];
        }

        //检查返回命令
        if (rcbuffer[1] == ModbusPacket.ReadCMD) {
            //获取返回内容长度
            int len = rcbuffer[2];
            byte[] ret = new byte[len];

            //将内容复制出来
            System.arraycopy(rcbuffer, 3, ret, 0, len);
            return ret;
        } else {
            //String packet = "";
            //for (int i = 0; i < ret_len; i++) {
            //   packet += String.format("%02X ", tmpbuffer[i]);
            //}
            return new byte[0];
        }
    }

    public byte[] ReadMemory(int memaddr, int mem_num, int timeout) throws Exception {
        return ReadMemory(memaddr, mem_num, 3, timeout);
    }

    public byte[] ReadMemory(int memaddr, int mem_num, int retry_num, int timeout) throws Exception {
        for (int retry = 0; retry < retry_num; retry++) {
            byte[] ret = this.readmemory(memaddr, mem_num + retry, timeout);
            if (ret.length > 0) {
                return ret;
            }
        }
        throw new Exception("超时");
    }

    private boolean writermemory(int memaddr, int mem_num, byte[] memorys, int timeout) throws Exception {
        TimeUnit.MILLISECONDS.sleep(20);
        //发送读取命令
        this.io.SendData(ModbusPacket.WriterPacket(this.addr, memaddr, mem_num, memorys));
        //等待返回结果
        byte[] tmpbuffer = new byte[1000];
        int ret_len = this.io.ReceiveData(tmpbuffer, timeout + 100);
        //检查返回值
        if (ret_len <= 0) {
            return false;
        }

        if (tmpbuffer[0] != this.addr) {
            return false;
        }

        //检查返回命令
        if (tmpbuffer[1] == ModbusPacket.WriterCMD) {
            //长度应该是总长度 - 1个命令字-1长度字-2个CRC
            int retry = 0;
            while ((ret_len < 5) && retry++ < 6) {
                byte[] recbuffer = new byte[100];
                int tmplen = this.io.ReceiveData(recbuffer, timeout);
                System.arraycopy(recbuffer, 0, tmpbuffer, ret_len, tmplen);
                ret_len += tmplen;
            }
            return ModbusPacket.CheckCRC(tmpbuffer, ret_len);
        } else {
            // String packet = "";
            // for (int i = 0; i < ret_len; i++) {
            //     packet += String.format("%02X ", tmpbuffer[i]);
            // }
            return false;
        }
    }

    public void WriterMemory(int memaddr, int mem_num, byte[] memorys, int timeout) throws Exception {
        
        WriterMemory(memaddr, mem_num, memorys, 3, timeout);
    }

    public void WriterMemory(int memaddr, int mem_num, byte[] memorys, int retry_num, int timeout) throws Exception {
        int retry = 0;
        while (retry++ < retry_num) {
            if (this.writermemory(memaddr, mem_num, memorys, timeout)) {
                return;
            }
        }
        throw new Exception("超时");
    }
}
