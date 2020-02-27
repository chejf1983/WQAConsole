/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.pro;

/**
 *
 * @author chejf
 */
public class Register {
    public Register(int reg_add, int reg_num){
        this.reg_add = reg_add;
        this.reg_num = reg_num;
    }
    public int reg_add;
    public int reg_num;
}
