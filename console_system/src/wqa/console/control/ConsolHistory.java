/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modebus.pro.ModeBus_Base;
import modebus.pro.NahonConvert;
import modebus.pro.Register;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;
import nahon.comm.faultsystem.LogCenter;
import wqa.console.manager.DevConstTable;
import wqa.console.manager.ProcessData;

/**
 *
 * @author chejf
 */
public class ConsolHistory {

    private ConsoleControl control;
    private int log_num[];
    private int dev_type[];

    private Register DEVTYPE = new Register(0x20, 8);
    private Register LOGNUM = new Register(0x40, 8);
    private Register CID = new Register(0x48, 1);
    private Register READ = new Register(0x4A, 1);
    private Register LOG = new Register(0x4B, 12);
    private static int READCMD = 0x11;
    private static int WRITCMD = 0x22;
    private static int SETCMD = 0x33;
    private static int CLEANCMD = 0x44;

    public ConsolHistory(ConsoleControl control) {
        this.control = control;
    }

    // <editor-fold defaultstate="collapsed" desc="Excel导出">
    public int InitHistoryLen() throws Exception {
        byte[] data = this.control.instance.ReadMemory(DEVTYPE.reg_add, DEVTYPE.reg_num, ModeBus_Base.def_timeout);
        this.log_num = new int[4];
        this.dev_type = new int[4];

        for (int i = 0; i < this.dev_type.length; i++) {
            this.dev_type[i] = NahonConvert.ByteArrayToUShort(data, i * 2);
        }

        data = this.control.instance.ReadMemory(LOGNUM.reg_add, LOGNUM.reg_num, ModeBus_Base.def_timeout);
        int total_num = 0;
        for (int i = 0; i < this.log_num.length; i++) {
            this.log_num[i] = NahonConvert.ByteArrayToUShort(data, i * 2);
            total_num += this.log_num[i];
        }

        return total_num;
    }

    //channel 通道号， id log编号， d_num 读取数据个数, data 返回数据
    private boolean ReadData(int channel, int id, int d_num, CollectData data) {
        try {
            this.control.instance.WriterMemory(CID.reg_add, 3,
                    NahonConvert.Cat(NahonConvert.UShortToByteArray(channel),
                            NahonConvert.UShortToByteArray(id),
                            NahonConvert.UShortToByteArray(READCMD)),
                    ModeBus_Base.def_timeout);

            byte[] mem = this.control.instance.ReadMemory(LOG.reg_add, LOG.reg_num, ModeBus_Base.def_timeout);
            int year = mem[0] * 100 + mem[1];
            int month = mem[2];
            int day = mem[3];
            int hour = mem[4];
            int min = mem[5];
            int sec = mem[6];
            int state = mem[7]; // 状态

//            CollectData data = new CollectData(d_num);
            String dateString = String.format("%d-%d-%d %d:%d:%d", year, month, day, hour, min, sec);
            data.time = new SimpleDateFormat(CollectData.timeFormat).parse(dateString);
            data.state = state;
            for (int i = 0; i < d_num; i++) {
                data.datas[i] = NahonConvert.ByteArrayToFloat(mem, 8 + i * 4);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    //获取列表名称
    private String[] ReadDataName(int devtype) {
        DevConstTable.DevInfo info = DevConstTable.GetTable().namemap.get(devtype);
        if (info == null) {
            return null;
        }

        String[] ret = new String[info.data_list.length + 3];
        ret[0] = info.dev_name_ch;
        ret[1] = "(时间)";
        ret[2] = "状态";
        for (int i = 0; i < info.data_list.length; i++) {
            ret[i + 3] = info.data_list[i].data_name + info.data_list[i].data_unit;
        }
        return ret;
    }

    //查找指定时间对应的数据，折半查找
    private int GetNearlistIndex(int cal_id, int start, int end, Date time) throws Exception {
        int mid = (int) ((end + start) / 2);
        if (mid == start) {
            return mid;
        } else {
            CollectData read_data = new CollectData(0);
            if (!ReadData(cal_id, mid, 0, read_data)) {
                throw new Exception("查询时间失败");
            }
            if (time.compareTo(read_data.time) == 0) {
                return mid;
            } else if (time.compareTo(read_data.time) < 0) {
                return GetNearlistIndex(cal_id, start, mid, time);
            } else {
                return GetNearlistIndex(cal_id, mid, end, time);
            }
        }
    }

    private int prompt_num = 0;

    //提示异常
    private boolean Prompting() {
        int ret = JOptionPane.showConfirmDialog(null, "线路不稳,请检查线路后确认是否继续", "连接中断提示", JOptionPane.YES_NO_OPTION);

        if (ret == JOptionPane.YES_OPTION) {
            prompt_num++;
            if (prompt_num > 1) {
                prompt_num = 0;
                this.control.Collect();
            }
        }

        return ret == JOptionPane.YES_OPTION;
    }

    public void SaveToExcel(String path, Date start, Date end, ProcessData data) {
        this.control.instance.io_lock.lock();
        long start_time = System.currentTimeMillis();
        try (XlsSheetWriter save_file = XlsSheetWriter.CreateSheet(path + "/控制器" + control.GetAddr() + "历史数据.xls", "data")) {
            this.InitHistoryLen();

            for (int cl_id = 1; cl_id <= this.log_num.length; cl_id++) {
                data.total_len += this.log_num[cl_id - 1];
            }

            for (int cl_id = 1; cl_id <= this.log_num.length; cl_id++) {
                //当前数据类型
                int devtype = this.dev_type[cl_id - 1];
                //当前数据个数
                int num = this.log_num[cl_id - 1];
                //获取设备名称
                String[] names = ReadDataName(devtype);
                if (names == null) {
                    data.current_len += num;
                    continue;
                }

                //获取起点
                int s_point = this.GetNearlistIndex(cl_id, 0, num, start);
                //获取终点
                int e_point = this.GetNearlistIndex(cl_id, s_point, num, end);

                String[] pars = new String[names.length - 1];
                System.arraycopy(names, 1, pars, 0, pars.length);
                //写列名
                xlsTable_W table = save_file.CreateNewTable(names[0], e_point - s_point, XlsSheetWriter.DirecTion.Horizontal, pars);
//                save_file.WriterColumnName(names);

                //跳过起始点之前的数据
                data.current_len += s_point;
                for (int log_id = s_point; log_id < e_point; log_id++) {
                    //发送命令前休息10ms
//                    TimeUnit.MILLISECONDS.sleep(10);
                    //去掉时间和状态，剩下得是数据个数
                    CollectData read_data = new CollectData(names.length - 3);
                    if (!ReadData(cl_id, log_id, names.length - 3, read_data)) {
                        //读取失败，提示用户
                        if (this.Prompting()) {
                            //回滚一条数据
                            log_id = log_id - 1;
                            continue;
                        } else {
                            break;
                        }
                    }
                    table.WriterLine(read_data.GetValue());
                    data.current_len++;
                }
                //补上终点之后的数据个数
                data.current_len += num - e_point;
                table.Finish();
            }

            start_time = System.currentTimeMillis() - start_time;
            start_time = start_time / 1000;
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "导出完毕" + start_time / 60 + "分钟" + start_time % 60 + "秒");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "保存数据失败" + ex);
        } finally {
            this.control.instance.io_lock.unlock();
        }
    }
//
//    public void SaveToExcel(String path, ProcessData data) {
//        this.control.instance.io_lock.lock();
//        long start_time = System.currentTimeMillis();
//        try (XlsSheetWriter save_file = XlsSheetWriter.CreateSheet(path + "/控制器" + control.GetAddr() + "历史数据.xls", "data")) {
//            this.InitHistoryLen();
//            for (int cl_id = 1; cl_id <= this.log_num.length; cl_id++) {
//                data.total_len += this.log_num[cl_id - 1];
//            }
//            for (int cl_id = 1; cl_id <= this.log_num.length; cl_id++) {
//                int devtype = this.dev_type[cl_id - 1];
//                //获取设备名称
//                String[] names = ReadDataName(devtype);
//                if (names == null) {
//                    continue;
//                }
//                int num = this.log_num[cl_id - 1];
//
//                String[] pars = new String[names.length - 1];
//                System.arraycopy(names, 1, pars, 0, pars.length);
//                xlsTable_W table = save_file.CreateNewTable(names[0], num, XlsSheetWriter.DirecTion.Horizontal, pars);
//                for (int log_id = 0; log_id < num; log_id++) {
////                    TimeUnit.MILLISECONDS.sleep(10);
//                    //去掉时间和状态，剩下得是数据个数
//                    CollectData read_data = new CollectData(names.length - 3);
//                    if (!ReadData(cl_id, log_id, names.length - 3, read_data)) {
//                        //读取失败，提示用户
//                        if (this.Prompting()) {
//                            //回滚一条数据
//                            log_id = log_id - 1;
//                            continue;
//                        } else {
//                            break;
//                        }
//                    }
//                    table.WriterLine(read_data.GetValue());
////                    table.WriterLine(ReadData(cl_id, log_id, names.length - 3).GetValue());
//                    data.current_len++;
//                }
//                table.Finish();
//            }
//            start_time = System.currentTimeMillis() - start_time;
//            start_time = start_time / 1000;
//            LogCenter.Instance().ShowMessBox(Level.SEVERE, "导出完毕" + start_time / 60 + "分钟" + start_time % 60 + "秒");
//        } catch (Exception ex) {
//            LogCenter.Instance().SendFaultReport(Level.SEVERE, "保存数据失败" + ex);
//        } finally {
//            this.control.instance.io_lock.unlock();
//        }
//    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="数据备份">
    private static int HEAD = 0x55AA7B7B;
    private static int END = 0x55AA7D7D;

    public void BackUp(String path, ProcessData data) {
        File bakfile = new File(path + "/控制器" + control.GetAddr() + "数据备份.bak");
        this.control.instance.io_lock.lock();
        try (FileOutputStream outStream = new FileOutputStream(bakfile)) {
            outStream.flush();
            //写文件头信息
            outStream.write(NahonConvert.IntegerToByteArray(HEAD));

            this.InitHistoryLen();
            //写通道个数
            outStream.write(NahonConvert.IntegerToByteArray(this.log_num.length));
            for (int cl_id = 1; cl_id <= this.log_num.length; cl_id++) {
                data.total_len += this.log_num[cl_id - 1];
                //保存条数
                outStream.write(NahonConvert.IntegerToByteArray(this.log_num[cl_id - 1]));
            }

            for (int cl_id = 1; cl_id <= this.log_num.length; cl_id++) {
                int num = this.log_num[cl_id - 1];

                for (int log_id = 0; log_id < num; log_id++) {
                    this.control.instance.WriterMemory(CID.reg_add, 3,
                            NahonConvert.Cat(NahonConvert.UShortToByteArray(cl_id),
                                    NahonConvert.UShortToByteArray(log_id),
                                    NahonConvert.UShortToByteArray(READCMD)),
                            ModeBus_Base.def_timeout);
                    byte[] ReadMemory = this.control.instance.ReadMemory(LOG.reg_add, LOG.reg_num, ModeBus_Base.def_timeout);
                    //写数据（24Bytes）
                    outStream.write(ReadMemory);
                    data.current_len++;
                }

                //保存条数
                outStream.write(NahonConvert.IntegerToByteArray(END));
            }
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "备份成功");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "备份数据失败" + ex);
        } finally {
            this.control.instance.io_lock.unlock();
        }
    }

    private void WriteLine(int cl_id, int log_id, byte[] data) throws Exception {
        //写入记录
        this.control.instance.WriterMemory(LOG.reg_add, LOG.reg_num, data, ModeBus_Base.def_timeout);
        //启动功能
        this.control.instance.WriterMemory(CID.reg_add, 3,
                NahonConvert.Cat(NahonConvert.UShortToByteArray(cl_id),
                        NahonConvert.UShortToByteArray(log_id),
                        NahonConvert.UShortToByteArray(WRITCMD)),
                ModeBus_Base.def_timeout);
    }

    private void UpdateClNum(int cl_id, int log_num) throws Exception {
        //更新记录条数
        this.control.instance.WriterMemory(LOGNUM.reg_add + cl_id - 1, 1,
                NahonConvert.UShortToByteArray(log_num),
                ModeBus_Base.def_timeout);
        //使能更新记录条数
        this.control.instance.WriterMemory(CID.reg_add, 3,
                NahonConvert.Cat(NahonConvert.UShortToByteArray(cl_id),
                        NahonConvert.UShortToByteArray(0),
                        NahonConvert.UShortToByteArray(SETCMD)),
                ModeBus_Base.def_timeout);
    }

    private void CleanCl(int cl_id) throws Exception {
        //清除通道数据
        this.control.instance.WriterMemory(CID.reg_add, 3,
                NahonConvert.Cat(NahonConvert.UShortToByteArray(cl_id),
                        NahonConvert.UShortToByteArray(0),
                        NahonConvert.UShortToByteArray(CLEANCMD)),
                ModeBus_Base.def_timeout);

        this.UpdateClNum(cl_id, 0);
    }

    public void LoadBackUp(String path, ProcessData data) {
        File file = new File(path);
        this.control.instance.io_lock.lock();
        try (FileInputStream inStream = new FileInputStream(file)) {

            byte[] Line = new byte[4];
            //读取文件头
            inStream.read(Line);
            int head = NahonConvert.ByteArrayToInteger(Line, 0);
            if (head != HEAD) {
                throw new Exception("非法文件");
            }

            //读取通道数
            inStream.read(Line);
            int cl_num = NahonConvert.ByteArrayToInteger(Line, 0);
            this.log_num = new int[cl_num];
            for (int cl_id = 1; cl_id <= cl_num; cl_id++) {
                Line = new byte[4];
                //读取记录条数
                inStream.read(Line);
                log_num[cl_id - 1] = NahonConvert.ByteArrayToInteger(Line, 0);
                data.total_len += log_num[cl_id - 1];
            }

            for (int cl_id = 1; cl_id <= this.log_num.length; cl_id++) {
                //清除控制器通道历史数据
                CleanCl(cl_id);
                //写入数据
                for (int log_id = 0; log_id < log_num[cl_id - 1]; log_id++) {
                    //读取记录
                    Line = new byte[24];
                    inStream.read(Line);
                    WriteLine(cl_id, log_id, Line);
                }
                //更新记录
                UpdateClNum(cl_id, log_num[cl_id - 1]);

                Line = new byte[4];
                inStream.read(Line);
                int end = NahonConvert.ByteArrayToInteger(Line, 0);
                if (end != END) {
                    throw new Exception("非法文件");
                }
            }
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "导入成功");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "导入数据失败" + ex);
        } finally {
            this.control.instance.io_lock.unlock();
        }
    }
    // </editor-fold> 

}