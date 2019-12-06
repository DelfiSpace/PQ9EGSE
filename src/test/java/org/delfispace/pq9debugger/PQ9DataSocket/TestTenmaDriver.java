/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.IOException;
import org.json.simple.parser.ParseException;

/**
 *
 * @author LocalAdmin
 */
public class TestTenmaDriver {
    protected static TenmaDriver powerSupply;
    static String comportTenma = "COM4";
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        powerSupply = new TenmaDriver(comportTenma);
        powerSupply.setVoltage(3);
        Thread.sleep(5000);
        powerSupply.setVoltage(3.2);
         Thread.sleep(5000);
        powerSupply.setVoltage(3.4);
         Thread.sleep(5000);
        powerSupply.setCurrent(1.9);
        powerSupply.setVoltage(2.99);
        powerSupply.closePort();
    }
    
}
