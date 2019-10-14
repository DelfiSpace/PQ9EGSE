/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;


import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author micha
 */
public class BK8500Driver {
     // SerialPort Object
    private final SerialPort comPort;
    // in and output streams
    private final InputStream is;
    private final OutputStream os;
    // Power Supply Functions. 

    public BK8500Driver(String port)
            /* this is a java implementation of the python code by  
            Copyright {2017} {B&K Precision Corporation}
            original: https://github.com/BKPrecisionCorp/BK-8500-Electronic-Load
            */
            
            /*
            The command interface of the BK8500 series uses a 26 Byte packet 
            interface. A command is a single packet and each command either 
            returns a command status response packet or a command specific packet.
            
            The last byte of the packet is a checksum. The checksum number is
            the arithmetic sum of each of the bytes modulo 256.
            */
    // Initialize port and object
    {        
        comPort = SerialPort.getCommPort(port);               
        comPort.openPort();
        System.out.println("isOpen: " + comPort.isOpen() + " ");
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        int fCs;
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        //fCs = comPort.getFlowControlSettings();
        //System.out.println("Flow control Settings: "+ fCs);
        
        //comPort.setFlowControl(SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_DTR_ENABLED);
        //comPort.setDTR();
        
        //omPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN)
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);

        is = comPort.getInputStream();
        os = comPort.getOutputStream();
        
        System.out.println("is " + is);
        System.out.println("os " + os);
    }
    //remember to close the port only one object can use the same port.     
    public void closePort()
    {
    // closes 
        comPort.closePort();
    }
       
    public int checkSum(byte[] cmd)
    {
        // This method calculates the checksum. 
        int sum =0;
        for(int i = 0; i<cmd.length;i++)
        {  
            sum = (sum + (((int)cmd[i]) & 0xFF));
        }
        return sum & 0xFF;
    }
    
    public static String byteToString(byte b) 
    {
        // call this method to show cmd byte including all leading zeros. 
        // this is useful for checking the cmd when adding new command function.
        byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((b & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }
    
    public void startRemoteOperation() throws IOException{
        //enable remote mode
        byte[] cmd = new byte[26];

        // set all command bytes to 0
        for(int item : cmd){
            cmd[item] = 0x00;
        }
        System.out.println("printing empty command");
        System.out.println(Arrays.toString(cmd));
        cmd[0]=(byte) 0xAA;
        cmd[2]=(byte) 0x20;
        cmd[3]=(byte) 1;
        cmd[25]=(byte)checkSum(cmd);
        // now we are ready to send the command
        System.out.println("Print Command Stream");
        for(int item = 0; item<cmd.length; item++)
        {  
            System.out.print(String.format("%02X ", cmd[item] & 0xFF));
        }
        System.out.println();
        sendCommand(cmd);
    }
    
    public void endRemoteOperation(){
        
    }
    
    private void sendCommand(byte[] cmd) throws IOException
    {
         try 
        {
            Thread.sleep(10);
        } catch (InterruptedException ex) 
        {
            // ignoring the error
        }
        System.out.println("I am sending " + os);
        os.write(cmd);
        os.flush();
        try 
        {
            Thread.sleep(150);
        } catch (InterruptedException ex) 
        {
            // ignoring the error
            ex.printStackTrace();
        }
        
    }

    private byte[] getResponseByte() throws IOException
    {
        try 
        {
            Thread.sleep(10);
        } catch (InterruptedException ex) 
        {
            // ignoring the error
        }
       // byte[] val = is.readNBytes(26); // a packet is always 26 bytes. 
        byte[] val = is.readNBytes(26); 
        // a packet is always 26 bytes.
 
        return val;       
    }
    
    private byte[] getAnyResponse() throws IOException
    {
        try 
        {
            Thread.sleep(10);
        } catch (InterruptedException ex) 
        {
            // ignoring the error
        }
        byte[] val = is.readNBytes(26); // a packet is always 26 bytes. 
        //byte[] val = is.readAllBytes(); 
        // a packet is always 26 bytes.         
        return val;
    }
    
     public static void main(String args[]) throws IOException 
    {
        // find serial port. 
         SerialPort[] seenPorts = SerialPort.getCommPorts();
        // show serial ports 
        for (SerialPort item : seenPorts){System.out.println(item);}
        String portName;
        // show descriptive port names. 
        portName = "COM10";//seenPorts[0].getSystemPortName(); //note this is device specific. 
        //System.out.println("port 1 = " + seenPorts[0].getDescriptivePortName() );
        //System.out.println("port 2 = " + seenPorts[1].getDescriptivePortName() );
        //System.out.println("port 3 = " + seenPorts[2].getDescriptivePortName() );
        // note that COM10 comes before COM2 in the array. 
        // start new driver
        BK8500Driver TestDriver = new BK8500Driver(portName);
        System.out.println("Start Remote Operation");
        TestDriver.startRemoteOperation();
        System.out.println("get Response");
        byte[] response =  TestDriver.getAnyResponse();
        for(int item = 0; item<response.length; item++)
        {  
            System.out.print(String.format("%02X ", response[item] & 0xFF));
        }
        System.out.println();
    }   
}
