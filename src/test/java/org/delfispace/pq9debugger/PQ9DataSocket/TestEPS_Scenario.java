/*
 * Copyright (C) 2019 Michael van den Bos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;


/**
 *
 * @author Michael van den Bos
 * mfvandenbos@gmail.com
 */
public class TestEPS_Scenario {
    
    private final static int TIMEOUT = 300; // in ms
    private final static StatisticsGenerator[] STATS = new StatisticsGenerator[22];
    
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        //Timer scheduler = new Timer(); 
        
       // TimerTask nextTask1 = new TaskListItem();
        
        SerialPort[] seenPorts = SerialPort.getCommPorts();
        
        for (SerialPort item : seenPorts) {
            System.out.println(item);
        }
        String portName;
        portName = seenPorts[1].getSystemPortName(); //note this is device specific. 
        System.out.print("Portname = ");
         System.out.println(portName);
        String commandString1;
        commandString1 = "VSET1:4.1"; // this commands sets a new 
        
        SendToTenma(commandString1, portName);
        
        SerialPort comPort = SerialPort.getCommPort(portName);
        /*comPort.openPort();
        try 
        {
            while (true)
            {
                while (comPort.bytesAvailable() == 0)
                {
                Thread.sleep(20);
                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
                }
            }
        } catch (InterruptedException e){ e.printStackTrace(); }
        comPort.closePort();*/
        
        comPort.openPort();
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        commandString1 = "*IDN?"; //request ID from Tenma
         SendToTenma(commandString1, portName); // sends command to tenma
        comPort.addDataListener(new SerialPortDataListener() { // should trigger event when data is recieved. 
        @Override
         public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
        @Override
        public void serialEvent(SerialPortEvent event)
        {
            byte[] newData = event.getReceivedData();
            System.out.println("Received data of size: " + newData.length);
        for (int i = 0; i < newData.length; ++i)
         System.out.print((char)newData[i]);
        System.out.println("\n");
        }});
        SendToTenma(commandString1, portName);
    }
    private static void SendToTenma(String commandString1, String portName){
        SerialPort tenmaPort = SerialPort.getCommPort(portName);
        //open the port
        tenmaPort.openPort(); 
        //configure the seriql port parameters
        tenmaPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        //write string to port.
        byte[] writeBuffer = commandString1.getBytes();
        System.out.println(commandString1);
        tenmaPort.writeBytes(writeBuffer, writeBuffer.length);
        // if a respons is expected you need to call a reader withing 50 miliseconds
    }
}

 
