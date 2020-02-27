/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.control;

import java.util.ArrayList;
import java.util.Arrays;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;

/**
 *
 * @author chejf
 */
public class ConsoleDB {

    private ConsoleControl control;

    public ConsoleDB(ConsoleControl control) {
        this.control = control;
    }

    private final ArrayList<CollectData> db = new ArrayList();

    public void SaveData(CollectData data) {
        if (db.size() > 6000) {
            this.db.remove(0);
        }
        this.db.add(data);
    }

    public void SaveToExcel(String path) {
        try (XlsSheetWriter save_file = XlsSheetWriter.CreateSheet(path + "/控制器" + control.GetAddr() + ".xls", "data")) {
            ArrayList<String> names = new ArrayList(Arrays.asList(this.control.showName));
            names.add(0, "状态");
            names.add(0, "时间");

            xlsTable_W table = save_file.CreateNewTable("控制器" + control.GetAddr(), db.size(), names.toArray(new String[0]));
//            save_file.WriterColumnName(names.toArray(new String[0]));
            for (int i = 0; i < db.size(); i++) {
                table.WriterLine(db.get(i).GetValue());
            }
            table.Finish();
            this.db.clear();
        } catch (Exception ex) {
            control.SetMessage("保存数据失败");
        }
    }
}
