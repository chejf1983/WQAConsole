/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.io;

/**
 *
 * @author chejf
 */
public interface IIOFactory {    
    public IAbstractIO CreateIO(String type, String[] pars);
    
    public String[] ListAllCom();
}
