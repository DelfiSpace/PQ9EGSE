/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author stefanosperett
 */
public class TenmaDriver 
{
    // SerialPort Object
    private final SerialPort comPort;
    // in and output streams
    private final InputStream is;
    private final OutputStream os;
    // Power Supply Functions. 
    private final static String VERSION = "TENMA 72-2540 V2.1";
    private final static String PING = "*IDN?";
    private final static String GETVOLTAGE = "VSET1?";
    private final static String GETCURRENT = "ISET1?";
    private final static String GETVOLTAGEACT = "VOUT1?";
    private final static String GETCURRENTACT = "IOUT1?";
    private final static String GETSTATUS = "STATUS?";
    // Memory functions
    
    
    // Over current
    private final static String OVERCURRENT = "OCP1 OCP OPEN";
    
    public TenmaDriver(String port)
    {        
        comPort = SerialPort.getCommPort(port);               
        comPort.openPort();
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);
        is = comPort.getInputStream();
        os = comPort.getOutputStream();
        
    }
    
    public boolean ping() throws IOException 
    {
        sendCommand(PING);
        return getResponse(VERSION.length()).endsWith(VERSION);
    }
        
    public double getVoltageSet() throws IOException 
    {
        sendCommand(GETVOLTAGE);
        String tmp = getResponse(5);
        return Double.valueOf(tmp);
    }
    
     public double getVoltageAct() throws IOException 
    {//Get the actual output voltage
        sendCommand(GETVOLTAGEACT);
        String tmp = getResponse(5);
        return Double.valueOf(tmp);
    }
     
    public double getCurrentSet() throws IOException 
    {
        sendCommand(GETCURRENT);
        String tmp = getResponse(5);
        return Double.valueOf(tmp);
    }
      
    public double getCurrentAct() throws IOException 
    {
        sendCommand(GETCURRENTACT);
        String tmp = getResponse(5);
        return Double.valueOf(tmp);
    }
       
    public byte getSTATUS() throws IOException 
    {
        sendCommand(GETSTATUS);
        byte tmp = getResponseByte();
        return tmp;
    }
    
    private void sendCommand(String cmd) throws IOException
    {
        os.write(cmd.getBytes());
    }
                
    private String getResponse(int length) throws IOException
    {
        byte[] val = is.readNBytes(length);
        return new String(val);
    }
    private byte getResponseByte() throws IOException
    {
        byte[] val = is.readNBytes(1);
        return val[0];
    }
}
