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
    private final SerialPort comPort;
    private final InputStream is;
    private final OutputStream os;
    private final static String VERSION = "TENMA 72-2540 V2.1";
    private final static String PING = "*IDN?";
    private final static String GETVOLTAGE = "VOUT1?";
    private final static String GETCURRENT = "IOUT1?";
    
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
        
    public double getVoltage() throws IOException 
    {
        sendCommand(GETVOLTAGE);
        String tmp = getResponse(5);
        return Double.valueOf(tmp);
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
}
