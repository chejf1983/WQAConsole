/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.consoleform;

import java.text.SimpleDateFormat;
import javax.swing.table.AbstractTableModel;
import wqa.console.control.CollectData;
import wqa.console.control.ConsoleControl;

/**
 *
 * @author chejf
 */
public class DataModel extends AbstractTableModel {

    ConsoleControl console;
    CollectData data;

    public DataModel(CollectData data, ConsoleControl console) {
        this.data = data;
        this.console = console;
    }
    
    @Override
    public boolean isCellEditable(int i, int column) {
        return column == 0;
    }

    @Override
    public String getColumnName(int i) {
        return i == 0 ? "时间" : new SimpleDateFormat("HH:mm:ss").format(data.time);
    }

    @Override
    public int getRowCount() {
        return data.datas.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return console.showName[row];
        } else {
            return data.datas[row];
        }
    }
    
    @Override
    public void setValueAt(Object o, int row, int column) {
        if(column == 0){
            console.showName[row] = o.toString();
        }
    }
}
