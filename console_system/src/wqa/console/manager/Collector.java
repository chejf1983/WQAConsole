/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.console.manager;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import wqa.console.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class Collector {

    private boolean isstart = false;
    private Date lasttime = new Date();

    public void Start() {
        if (!this.isstart) {
            this.isstart = true;
            WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
//                        int ttime = intergeral;
                        while (isstart) {
                            if (new Date().getTime() - lasttime.getTime() > intergeral * 1000) {
                                Collect();
                            }
                            TimeUnit.SECONDS.sleep(1);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Collector.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        isstart = false;
                    }
                }
            });
        }
    }

    public void Collect() {
        lasttime = new Date();
        WQAPlatform.GetInstance().GetManager().CollectAll();
    }

    public void Stop() {
        if (this.isstart) {
            this.isstart = false;
        }
    }

    public int intergeral = 10;

    public void ChangeInteval(int time) {
        this.intergeral = time;
    }
}
