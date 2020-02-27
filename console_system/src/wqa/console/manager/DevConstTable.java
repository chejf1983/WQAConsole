/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.manager;

import java.util.HashMap;

/**
 *
 * @author chejf
 */
public class DevConstTable {

    private DevConstTable() {
        this.initInfoTable();
    }

    private static DevConstTable table;

    public static DevConstTable GetTable() {
        if (table == null) {
            table = new DevConstTable();
        }
        return table;
    }

    // <editor-fold defaultstate="collapsed" desc="设备静态信息">
    public class DevInfo {

        public String dev_name;
        public String dev_name_ch;
        public DataInfo[] data_list;

        public DevInfo(
                String devtype,
                String devtype_string,
                DataInfo... datas) {
            this.dev_name = devtype;
            this.dev_name_ch = devtype_string;
            this.data_list = datas;
        }
    }

    public class DataInfo {

        public String data_name;
        public String data_unit;
        public String[] data_range;
        public int cal_num;

        public DataInfo(String data_name, String data_unit, int cal_num, String... range) {
            this.data_name = data_name;
            this.data_unit = data_unit;
            this.cal_num = cal_num;
            this.data_range = range;
        }
    }

    public HashMap<Integer, DevInfo> namemap = new HashMap();

    private void initInfoTable() {
        //ESA        
        this.namemap.put(0x0200, new DevInfo("ESA_pH", "PH",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0208, new DevInfo("ESA_ORP", "ORP",
                new DataInfo("ORP", "mV", 2, "(-2000-2000)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0203, new DevInfo("ESA_EC", "电导率",
                new DataInfo("电导率", "us/cm", 1, "(0-500)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        //OSA
        this.namemap.put(0x0201, new DevInfo("ESA_DO", "溶解氧",
                new DataInfo("溶解氧", "mg/L", 0x102, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0210, new DevInfo("OSA_FDO", "溶解氧FDO",
                new DataInfo("溶解氧", "mg/L", 0x102, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0110, new DevInfo("OSA_FDO", "溶解氧FDO",
                new DataInfo("溶解氧", "mg/L", 0x102, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0100, new DevInfo("OSA_Turb", "浊度",
                new DataInfo("浊度", "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0102, new DevInfo("OSA_TS", "悬浮物",
                new DataInfo("悬浮物", "mg/L", 3, "(0-10000)", "(0-20000)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0106, new DevInfo("OSA_ChlA", "叶绿素",
                new DataInfo("叶绿素", "ug/L", 3, "(0-500)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0108, new DevInfo("OSA_Cyano", "蓝绿藻",
                new DataInfo("蓝绿藻", "细胞/ul", 3, "(0-2000)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x010A, new DevInfo("OSA_Oil", "水中油",
                new DataInfo("水中油", "ppm", 3, "(0-500)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        //ISA
        this.namemap.put(0x0300, new DevInfo("ISA_AMMO_I", "氨氮I",
                new DataInfo("pH", "mg/L", 2, "(0-14)"),
                new DataInfo("NH4", "mg/L", 2, "(0-1000)"),
                new DataInfo("K", "mg/L", 2, "(0-1000)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
        this.namemap.put(0x0301, new DevInfo("ISA_AMMO_II", "氨氮II",
                new DataInfo("pH", "mg/L", 2, "(0-14)"),
                new DataInfo("NH4", "mg/L", 2, "(0-1000)"),
                new DataInfo("K", "mg/L", 2, "(0-1000)"),
                new DataInfo("温度", "℃", 1, "(0-60)")));
    }
    // </editor-fold> 
}
