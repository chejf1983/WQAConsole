/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.consoleform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.console.control.CollectData;
import wqa.console.control.ConsoleControl;
import wqa.console.manager.ProcessData;
import wqa.console.system.WQAPlatform;
import wqa.form.main.entrer.InitPaneHelper;
import wqa.form.main.entrer.MainForm;

/**
 *
 * @author chejf
 */
public class ConsolePane extends javax.swing.JPanel {

    /**
     * Creates new form Consel
     */
    private ConsoleControl console;
    public static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    public ConsolePane(ConsoleControl instance) {
        initComponents();
        this.console = instance;
        this.Label_CAddr.setText("控制器:" + this.console.GetAddr());

//        try {
//            Date time = new SimpleDateFormat(timeFormat).parse("2021-03-25 19:00:00");
//            if (new Date().after(time)) {
//                Button_Load.setVisible(false);
//                Button_BackUp.setVisible(false);
//            }
//        } catch (ParseException ex) {
//            Logger.getLogger(ConsolePane.class.getName()).log(Level.SEVERE, null, ex);
            Button_Load.setVisible(false);
            Button_BackUp.setVisible(false);
//        }


        InitTimeText();
        instance.CollectData.RegeditListener(new EventListener<CollectData>() {
            @Override
            public void recevieEvent(Event<CollectData> event) {
                UpdateData(event.GetEvent());
            }
        });
    }

    private void UpdateData(CollectData data) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Table_data.setModel(new DataModel(data, console));
                Label_Message.setText(console.GetMessage());
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Label_CAddr = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_data = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        Label_Message = new javax.swing.JLabel();
        Button_ReadHistory = new javax.swing.JButton();
        Button_Load = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        TextField_start_time = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        TextField_end_time = new javax.swing.JTextField();
        Button_BackUp = new javax.swing.JButton();
        Button_ShowHistoryNum = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        Label_CAddr.setBackground(new java.awt.Color(51, 51, 51));
        Label_CAddr.setForeground(new java.awt.Color(255, 255, 255));
        Label_CAddr.setText("   jLabel2");
        Label_CAddr.setOpaque(true);

        Table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(Table_data);

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setOpaque(true);

        Label_Message.setBackground(new java.awt.Color(51, 51, 51));
        Label_Message.setForeground(new java.awt.Color(255, 255, 255));
        Label_Message.setOpaque(true);

        Button_ReadHistory.setText("导出数据");
        Button_ReadHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ReadHistoryActionPerformed(evt);
            }
        });

        Button_Load.setText("数据恢复");
        Button_Load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_LoadActionPerformed(evt);
            }
        });

        jLabel4.setText("起始:");

        TextField_start_time.setText("jTextField1");

        jLabel5.setText("终止:");

        TextField_end_time.setText("jTextField1");

        Button_BackUp.setText("数据备份");
        Button_BackUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_BackUpActionPerformed(evt);
            }
        });

        Button_ShowHistoryNum.setText("记录数目");
        Button_ShowHistoryNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ShowHistoryNumActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Label_CAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Label_Message, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextField_start_time, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Button_Load, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Button_BackUp, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextField_end_time, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addGap(8, 8, 8))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_ShowHistoryNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_ReadHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Label_CAddr, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Label_Message, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(TextField_start_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Button_BackUp)
                            .addComponent(Button_Load)
                            .addComponent(Button_ShowHistoryNum)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(TextField_end_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_ReadHistory)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    ProcessData lastprocess = ProcessData.EmptyProcess();

    Date start_time = null;
    Date stop_time = null;

    private void InitTimeText() {
        Date now = new Date();
        now.setTime(new Date().getTime() - 24 * 3600 * 1000);
        this.TextField_start_time.setText(new SimpleDateFormat(timeFormat).format(now));
        this.TextField_end_time.setText(new SimpleDateFormat(timeFormat).format(new Date()));
    }

    private void EnableButton(boolean value) {
        this.Button_BackUp.setEnabled(value);
        this.Button_Load.setEnabled(value);
        Button_ReadHistory.setEnabled(value);
        this.Button_ShowHistoryNum.setEnabled(value);
    }

    private void ShowProcess() {
        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            while (!lastprocess.isfinished) {
                java.awt.EventQueue.invokeLater(() -> {
                    MainForm.instance.GetProcesBar().setMaximum(lastprocess.total_len);
                    MainForm.instance.GetProcesBar().setValue(lastprocess.current_len);
                    MainForm.instance.GetPecentLable().setText(lastprocess.current_len + "/" + lastprocess.total_len);
                });
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            java.awt.EventQueue.invokeLater(() -> {
                MainForm.instance.GetProcesBar().setValue(0);
                EnableButton(true);
                MainForm.instance.GetPecentLable().setText(lastprocess.current_len + "/" + lastprocess.total_len);
            });
//            LogCenter.Instance().ShowMessBox(Level.SEVERE, "");
        });
    }

    private boolean CheckCondition() {
        if (!this.lastprocess.isfinished) {
            return false;
        }

        EnableButton(false);
        return true;
    }

    private void Button_ReadHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ReadHistoryActionPerformed
        //获取起止时间
        try {
            start_time = new SimpleDateFormat(timeFormat).parse(this.TextField_start_time.getText());
            stop_time = new SimpleDateFormat(timeFormat).parse(this.TextField_end_time.getText());
//            this.TextField_start_time.setText(new SimpleDateFormat(timeFormat).format(start_time));
//            this.TextField_end_time.setText(new SimpleDateFormat(timeFormat).format(stop_time));
            if (!start_time.before(stop_time)) {
                LogCenter.Instance().ShowMessBox(Level.SEVERE, "截至时间必须大于起始时间");
                return;
            }
        } catch (ParseException ex) {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, ex.getMessage());
            InitTimeText();
            return;
        }

        if (CheckCondition()) {
            String path = InitPaneHelper.GetDirPath();
            if (path == null) {
                EnableButton(true);
                return;
            }

            lastprocess = new ProcessData();
            WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
                console.History.SaveToExcel(path, start_time, stop_time, lastprocess);

                lastprocess.isfinished = true;
            });
            ShowProcess();
        }

    }//GEN-LAST:event_Button_ReadHistoryActionPerformed

    private void Button_LoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_LoadActionPerformed
        if (CheckCondition()) {
            String path = InitPaneHelper.GetFilePath(".bak");
            if (path == null) {
                EnableButton(true);
                return;
            }

            lastprocess = new ProcessData();
            WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
                console.History.LoadBackUp(path, lastprocess);

                lastprocess.isfinished = true;
            });
            ShowProcess();
        }
    }//GEN-LAST:event_Button_LoadActionPerformed

    private void Button_BackUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_BackUpActionPerformed
        if (CheckCondition()) {
            String path = InitPaneHelper.GetDirPath();
            if (path == null) {
                EnableButton(true);
                return;
            }

            lastprocess = new ProcessData();
            WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
                console.History.BackUp(path, lastprocess);

                lastprocess.isfinished = true;
            });
            ShowProcess();
        }
    }//GEN-LAST:event_Button_BackUpActionPerformed

    private void Button_ShowHistoryNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ShowHistoryNumActionPerformed
        if (CheckCondition()) {
            int[] nums = this.console.ShowHistoryNum();
            if (nums.length > 0) {
                String info = "";
                for (int i = 0; i < nums.length; i++) {
                    info += "通道" + (i + 1) + ":" + nums[i] + " ";
                }
                JOptionPane.showMessageDialog(this, "当前控制器记录个数:" + info);
            }
            EnableButton(true);
        }
    }//GEN-LAST:event_Button_ShowHistoryNumActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_BackUp;
    private javax.swing.JButton Button_Load;
    private javax.swing.JButton Button_ReadHistory;
    private javax.swing.JButton Button_ShowHistoryNum;
    private javax.swing.JLabel Label_CAddr;
    private javax.swing.JLabel Label_Message;
    private javax.swing.JTable Table_data;
    private javax.swing.JTextField TextField_end_time;
    private javax.swing.JTextField TextField_start_time;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
