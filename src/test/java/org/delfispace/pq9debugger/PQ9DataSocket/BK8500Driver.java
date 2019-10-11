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
// Initialize port 
    {        
        comPort = SerialPort.getCommPort(port);               
        comPort.openPort();
        comPort.setComPortParameters(38400, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);
        is = comPort.getInputStream();
        os = comPort.getOutputStream();
    }
//remember to close the port only one object can use the same port.     
    public void closePort(){
    // closes 
        comPort.closePort();
       // return "Port Closed";
    }
       
    public int checkSum(int[] cmd){
        // This method calculates the checksum. 
        // The check sum 
        int sum =0;
        
        for(int i = 0; i<cmd.length;i++)
        {  
            System.out.println(cmd[i]);
            sum = (sum + cmd[i]);
            System.out.println("csum;  " + sum );
        }
        System.out.println("final csum "+ sum);
        System.out.println("final 0FF&csum  "+ (0xFF&sum) );
        int extra = 0xFF; // 
        return (extra&sum);
    }
    
    public static String byteToString(byte b) {
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
        int[] cmdp = new int[26];
        // set all command bytes to 0
        for(int item : cmd){
            cmd[item] = 0x00;
            cmdp[item] = 0;
            System.out.println(byteToString(cmd[item]));
        }
        System.out.println("printing empty command");
        System.out.println(Arrays.toString(cmd));
        cmd[0]=(byte) 0xAA;
        cmdp[0] = 0xAA;
        cmd[2]=(byte) 0x20;
        cmdp[2] = 0x20;
        cmd[3]=(byte) 1;
        cmdp[3] = 1;
        cmd[25]=(byte)checkSum(cmdp);
        // now we are ready to send the command
        System.out.println("Print Command Stream");
        for(int item = 0; item<cmd.length; item++){
            
            System.out.println(byteToString(cmd[item]));
        }
        sendCommand(cmd);
    }
    
    public void endRemoteOperation(){
        
    }
    
    private void sendCommand(byte[] cmd) throws IOException
    {
        //for(int item = 0; item<cmd.length; item++){
            System.out.println("I am sending");
           os.write(cmd);
        //}
        try 
        {
            Thread.sleep(200);
        } catch (InterruptedException ex) 
        {
            // ignoring the error
        }
    }

    private byte[] getResponseByte() throws IOException
    {
       // byte[] val = is.readNBytes(26); // a packet is always 26 bytes. 
        byte[] val = is.readNBytes(2); 
// a packet is always 26 bytes.
               
        return val;
      
    }
    
     public static void main(String args[]) throws IOException {
        // gain serial port. 
         SerialPort[] seenPorts = SerialPort.getCommPorts();
        
        for (SerialPort item : seenPorts) {
            System.out.println(item);
        }
        String portName;
        portName = seenPorts[0].getSystemPortName(); //note this is device specific. 
        System.out.println("port 1 = " + seenPorts[0].getDescriptivePortName() );
        System.out.println("port 2 = " + seenPorts[1].getDescriptivePortName() );
        System.out.println("port 3 = " + seenPorts[2].getDescriptivePortName() );
        BK8500Driver TestDriver = new BK8500Driver(portName);
        System.out.println("Start Remote Operation");
        TestDriver.startRemoteOperation();
        System.out.println("get Response");
        byte[] response =  TestDriver.getResponseByte();
                for(int item : response){
            System.out.println(byteToString(response[item]));
        }
    }   
}
