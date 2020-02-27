/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.pro;


/**
 *
 * @author chejf
 */
public class ModbusPacket {
    public static byte ReadCMD = 0x03;
    public static byte WriterCMD = 0x10;
    
    public static byte[] ReadPacket(byte devaddr, int memaddr, int mem_num) throws NahonConvertException {
        byte[] tmp = new byte[1 + 1 + 2 + 2 + 2];
        tmp[0] = devaddr;
        tmp[1] = ReadCMD; // 读寄存器命令字
        System.arraycopy(NahonConvert.UShortToByteArray(memaddr), 0, tmp, 2, 2);
        System.arraycopy(NahonConvert.UShortToByteArray(mem_num), 0, tmp, 4, 2);
        CRC16 crc16 = new CRC16();
        int crc = crc16.getCrc(tmp, tmp.length - 2);
        System.arraycopy(NahonConvert.UShortToByteArray(crc), 0, tmp, 6, 2);
        return tmp;
    }
    
    public static byte[] WriterPacket(byte devaddr, int memaddr, int mem_num, byte[] par) throws NahonConvertException{
        byte[] tmp = new byte[1 + 1 + 2 + 2 + 1 + par.length + 2];
        tmp[0] = devaddr;
        tmp[1] = WriterCMD; // 写寄存器命令字
        System.arraycopy(NahonConvert.UShortToByteArray(memaddr), 0, tmp, 2, 2);
        System.arraycopy(NahonConvert.UShortToByteArray(mem_num), 0, tmp, 4, 2);
        tmp[6] = (byte)(par.length);
        System.arraycopy(par, 0, tmp, 7, par.length);
        CRC16 crc16 = new CRC16();
        int crc = crc16.getCrc(tmp, tmp.length - 2);
        System.arraycopy(NahonConvert.UShortToByteArray(crc), 0, tmp, tmp.length - 2, 2);
        return tmp;
    }
    
    public static boolean CheckCRC(byte[] buffer, int buffer_len) throws NahonConvertException{
        if(buffer_len < 2){
            return false;
        }
        
        CRC16 crc16 = new CRC16();
        int crc = crc16.getCrc(buffer, buffer_len - 2);
        byte[] a_crc = NahonConvert.UShortToByteArray(crc);
        return a_crc[0] == buffer[buffer_len - 2] && a_crc[1] == buffer[buffer_len - 1];
    }
    
    public static void main(String... args) throws NahonConvertException{
        byte[] data = ModbusPacket.ReadPacket((byte)1,0x4B, 12);
        for(int i = 0; i < data.length; i++){
            System.out.print(String.format(" %02X", data[i]));
        }
    }
}
