/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import com.fazecast.jSerialComm.SerialPort;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.delfispace.pq9debugger.PQ9DataSocket.TaskList;

/**
 *
 * @author LocalAdmin
 */
public class BatteryChargeTest {
    
     public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException {
        Grapher example = new Grapher("Line Chart Example");
        SwingUtilities.invokeLater(() -> {
        example.setAlwaysOnTop(true);
        example.pack();
        example.setSize(600, 400);
        example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        example.setVisible(true);
        });
          SerialPort[] seenPorts = SerialPort.getCommPorts();
        
        for (SerialPort item : seenPorts) {
            System.out.println(item);
        }
        String portName;
        portName = "COM4"; //seenPorts[2].getSystemPortName(); //note this is device specific. 
       String chargeTest;
       double currentLimit = 0.4;
       chargeTest = "ChargeTest_" + String.valueOf(currentLimit*1000)+".txt";
       BatteryRun TaskList = new BatteryRun(1, portName, chargeTest, currentLimit, example);
        
    }
}
