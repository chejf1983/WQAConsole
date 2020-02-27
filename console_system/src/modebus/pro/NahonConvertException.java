/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.pro;

/**
 *
 * @author Administrator
 */
public class NahonConvertException extends Exception {

    public NahonConvertException(Exception ex) {
        super(ex);
    }

    public NahonConvertException(String ex) {
        super(ex);
    }

}
