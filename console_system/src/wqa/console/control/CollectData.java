/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.control;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author chejf
 */
public class CollectData implements IJXLSaver {

    public static final String timeFormat = "yyyy-MM-dd HH:mm:ss";
    public static String StateString[] = new String[]{"正常","通信异常","报警","错误","清扫"};

    public CollectData(int len) {
        this.datas = new Float[len];
        this.time = new Date();
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
}
