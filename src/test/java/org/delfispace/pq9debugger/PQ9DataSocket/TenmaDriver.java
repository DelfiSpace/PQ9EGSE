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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final static String RECALL = "RCL";
    private final static String SAVESETTING = "SAV";
    private final static String ENABLEOUTPUT = "OUT1";
    private final static String DISABLEOUTPUT = "OUT0";
    private final static String SETVOLTAGE = "VSET1:";
    private final static String SETCURRENT = "ISET1:";
    private final static double MAXVOLTAGE = 40;
    //private final static byte
    // Memory functions
    
    
    // Over current
    private final static String OVERCURRENT = "OCP1 OCP OPEN";
    
    private String port_loc;
    
    public TenmaDriver(String port) throws IOException
    {        
        port_loc = port;
        comPort = SerialPort.getCommPort(port);               
        comPort.openPort();
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);
        is = comPort.getInputStream();
        os = comPort.getOutputStream();
        if(is == null || os == null){
        throw new IOException("In or outputstream fail");}
    }
    
    public void closePort(){
    // closes 
        comPort.closePort();
       // return "Port Closed";
    }
    
    public boolean ping() throws IOException 
    {
        sendCommand(PING);
        return getResponse(VERSION.length()).endsWith(VERSION);
    }
    
    public void sunUP() throws IOException{
        sendCommand(ENABLEOUTPUT);
    }
    
    public void sunDown() throws IOException{
        sendCommand(DISABLEOUTPUT);
    }
    
    public void setVoltage(double voltage) throws IOException{
        if(voltage <= MAXVOLTAGE){
        String cmd;
        //force to use 2 decimals, more will not be accepted by Tenma
        cmd =  SETVOLTAGE+String.format(Locale.US,"%.2f", voltage);;
        //System.out.println(cmd);
        sendCommand(cmd);
        }
        else{System.out.println("Trying to set dangarous voltage, voltage is not changed");}
    }
    public void setCurrent(double current) throws IOException{
        
        String cmd;
        cmd =  SETCURRENT+String.format(Locale.US,"%.2f", current);; 
        System.out.println(cmd);
        sendCommand(cmd);
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
        try{
        String tmp = getResponse(5);
        return Double.valueOf(tmp);
        }catch(IOException Ex)
        {
            System.out.println("Tenma Issue, attempting to restore");
            sendCommand(GETVOLTAGEACT);
            String tmp = getResponse(5);
            return Double.valueOf(tmp);
        }
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
    private void recallMemory(int setting) throws IOException{
        String cmd;
        cmd =  RECALL+Integer.toString(setting); 
        System.out.println(cmd);
        sendCommand(cmd);
    }
    private void savelMemory(int setting) throws IOException{
        String cmd;
        cmd =  SAVESETTING+Integer.toString(setting); 
        System.out.println(cmd);
        sendCommand(cmd);
    }
    private void overCurrent(int setting){
        //this command is malfunctioning. 
    }
    
    private void sendCommand(String cmd) throws IOException
    {
        os.write(cmd.getBytes());
        try 
        {
            Thread.sleep(100);
        } catch (InterruptedException ex) 
        {
            // ignoring the error
        }
    }
                
    private String getResponse(int length) throws IOException
    {      
        //try{
            byte[] val = is.readNBytes(length);
            return new String(val);
       // }catch(IOException ex){
       //     ex.printStackTrace();
       //     byte[] val0 = null;
       //    for(int i=0; i<length; i++){
        //    val0[i] = 0;
        //    }
        //    return new String(val0);
        //}
    }
    private byte getResponseByte() throws IOException
    {
        byte[] val = is.readNBytes(1);
        return val[0];
      
    }
    
    @Override
    protected void finalize(){
        closePort();
    }
    
}
