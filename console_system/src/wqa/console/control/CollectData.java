/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import modebus.pro.NahonConvert;
import modebus.pro.NahonConvertException;

/**
 *
 * @author chejf
 */
public class CollectData implements IJXLSaver {

    public static final String timeFormat = "yyyy-MM-dd HH:mm:ss";
    public static String StateString[] = new String[]{"正常", "通信异常", "报警", "错误", "清扫"};

    public CollectData(int len) {
        this.datas = new Float[len];
        this.time = new Date();
    }

    public CollectData(String[] split) throws ParseException {
        this.datas = new Float[4];
        this.time = new SimpleDateFormat(timeFormat).parse(split[0]);
        this.state = Integer.valueOf(split[1]);
        for (int i = 0; i < datas.length; i++) {
            if (2 + i < split.length) {
                datas[i] = Float.valueOf(split[2 + i]);
            } else {
                datas[i] = 0f;
            }
        }
    }

    public Date time;
    public Float[] datas;
    public int state = 0;

    @Override
    public Object[] GetValue() {
        Object[] values = new Object[datas.length + 2];
        values[0] = new SimpleDateFormat(timeFormat).format(time);
        values[1] = StateString[this.state];
        for (int i = 0; i < datas.length; i++) {
            values[i + 2] = datas[i];
        }
        return values;
    }

    @Override
    public String toString() {
        String ret = new SimpleDateFormat(timeFormat).format(time) + "|" + this.state;
        for (int i = 0; i < this.datas.length; i++) {
            ret += "|" + this.datas[i];
        }
        return ret;
    }

    public byte[] toByteArray() throws NahonConvertException {
        byte[] ret = new byte[24];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int year = calendar.get(Calendar.YEAR);
        ret[0] = (byte) (year / 100);
        ret[1] = (byte) (year % 100);
        ret[2] = (byte) (calendar.get(Calendar.MONTH));
        ret[3] = (byte) (calendar.get(Calendar.DAY_OF_MONTH));
        ret[4] = (byte) (calendar.get(Calendar.HOUR_OF_DAY));
        ret[5] = (byte) (calendar.get(Calendar.MINUTE));
        ret[6] = (byte) (calendar.get(Calendar.SECOND));
        ret[7] = (byte) this.state;
        System.arraycopy(NahonConvert.FloatToByteArray(datas[0]), 0, ret, 8, 4);
        System.arraycopy(NahonConvert.FloatToByteArray(datas[1]), 0, ret, 12, 4);
        System.arraycopy(NahonConvert.FloatToByteArray(datas[2]), 0, ret, 16, 4);
        System.arraycopy(NahonConvert.FloatToByteArray(datas[3]), 0, ret, 20, 4);
        return ret;
    }
}
