/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.IIOFactory;
import wqa.console.manager.ConsoleManager;
import wqa.console.io.IOManager;

/**
 *
 * @author chejf
 */
public class WQAPlatform {

    private static WQAPlatform instance;

    private WQAPlatform() {
    }

    public static WQAPlatform GetInstance() {
        if (instance == null) {
            instance = new WQAPlatform();
        }
        return instance;
    }

    // <editor-fold defaultstate="collapsed" desc="IO驱动和设备驱动"> 
    public static void LoadIOFactory(IIOFactory iodrv){
        IOManager.SetIOFactory(iodrv);
    }
    // </editor-fold>  
    
    public void InitSystem() throws Exception{
        LogCenter.Instance().SetLogPath("./log");
        
        this.InitConfig();
        
    }
    
    // <editor-fold defaultstate="collapsed" desc="系统模块">      
    
    private ConsoleManager manager;
    public ConsoleManager GetManager(){
        if(this.manager == null){
            manager = new ConsoleManager();
        }
        
        return manager;
    }
    //线程池
    ExecutorService threadpools;

    public ExecutorService GetThreadPool() {
        if (threadpools == null) {
            threadpools = Executors.newFixedThreadPool(200);
        }
        return threadpools;
    }
    
    public boolean is_internal = false;
    
    private Properties Config = new Properties();
    public Properties GetConfig(){
        return this.Config;
    }
    
    private void InitConfig(){
        File file = new File("./dev_config");
        if (file.exists()) {
            try {
                Config.loadFromXML(new FileInputStream(file));
                this.is_internal = Config.getProperty("IPS", "").contains("Naqing");
            } catch (IOException ex) {
                LogCenter.Instance().PrintLog(Level.SEVERE, "没有找到配置文件", ex);
            }
        }
    }
    
    public void SaveConfig() {
        File file = new File("./dev_config");
        try {
            Config.storeToXML(new FileOutputStream(file), "");
        } catch (IOException ex) {
            LogCenter.Instance().PrintLog(Level.SEVERE, "保存配置失败！", ex);
        }

    }
    // </editor-fold>  
}
