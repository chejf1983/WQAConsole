/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.manager;

/**
 *
 * @author chejf
 */
public class ProcessData {
    public int total_len = 0;
    public int current_len = 0;    
    public boolean isfinished = false;
    
    public static ProcessData EmptyProcess(){
        ProcessData ret = new ProcessData();
        ret.isfinished = true;
        return ret;
    }
}
