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
import org.delfispace.pq9debugger.PQ9DataSocket.Bk8500CException;

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

    public BK8500Driver(String port) throws IOException
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
        if(comPort.isOpen()){
            System.out.println(comPort.getDescriptivePortName()+" isOpen: " + comPort.isOpen() + " ");
        }
        else{
            //exception is thrown if the port did not open. 
            throw new IOException("Port is shut");
        }
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        // use flow control disabled even though the datasheet suggests otherwise
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        // Set timeout settings 
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);
        
        //create and check in and output streams
        is = comPort.getInputStream();
        os = comPort.getOutputStream();
        if(is == null || os == null){
            throw new IOException("In or outputstream fail");
        }
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
    // start remote operation
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
    // set voltage limit
    public void setVoltageLim() throws IOException{
        byte[] cmd = new byte[26]; // cmd byte
        // set all command bytes to 0
        for(int item : cmd){
            cmd[item] = 0x00;
        }
        System.out.println("printing empty command");
        System.out.println(Arrays.toString(cmd));
        cmd[0]=(byte) 0xAA;
        cmd[2]=(byte) 0x22;
        cmd[3]=(byte) 0x66;
        cmd[4]=(byte) 0x3f;
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
    public void setCurrentLim() throws IOException{
        byte[] cmd = new byte[26]; // cmd byte
        // set all command bytes to 0
        for(int item : cmd){
            cmd[item] = 0x00;
        }
        System.out.println("printing empty command");
        System.out.println(Arrays.toString(cmd));
        cmd[0]=(byte)0xAA;
        cmd[2]=(byte)0x24;
        cmd[3]=(byte)0xE0;
        cmd[4]=(byte)0x79;
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
     public void setPowerLim(double powerlimit) throws IOException{
        byte[] cmd = new byte[26]; // cmd byte
        // set all command bytes to 0
        for(int item : cmd){
            cmd[item] = 0x00;
        }
        System.out.println("printing empty command");
        System.out.println(Arrays.toString(cmd));
        cmd[0]=(byte)0xAA;
        cmd[2]=(byte)0x26;
        // finih
        cmd[3]=(byte)powerlimit; // does this work? 
        cmd[4]=(byte)0x00; // should be checked
        cmd[5]=(byte)0x00;
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
    public void getModel() throws IOException{
        byte[] cmd = new byte[26]; // cmd byte
        // set all command bytes to 0
        for(int item : cmd){
            cmd[item] = 0x00;
        }
        System.out.println("printing empty command");
        System.out.println(Arrays.toString(cmd));
        cmd[0]=(byte)0xAA;
        cmd[2]=(byte)0x6A;
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
       public double getValues() throws IOException{
        byte[] cmd = new byte[26]; // cmd byte
        // set all command bytes to 0
        for(int item : cmd){
            cmd[item] = 0x00;
        }
        System.out.println("printing empty command");
        System.out.println(Arrays.toString(cmd));
        cmd[0]=(byte)0xAA;
        cmd[2]=(byte)0x5F; // datasheet
    
        cmd[25]=(byte)checkSum(cmd);
        // now we are ready to send the command
        System.out.println("Print Command Stream");
        for(int item = 0; item<cmd.length; item++)
        {  
            System.out.print(String.format("%02X ", cmd[item] & 0xFF));
        }
        System.out.println();
        sendCommand(cmd);
        return 0.0001;
    }
    public void setMode() throws IOException{
        
    }
    
    public void endRemoteOperation(){
        
    }
    
    public void setMinVoltageBatteryTest(float minV){
        
    }
    public void selectBatteryTest(){
        
    }
    public String getTestType(){
        return "still to do";
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
    
    private byte[] getAnyResponse() throws IOException, Bk8500CException
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
        if(val[3]==0x80){}
        else 
            {
                if(val[0]==0xAA){
                switch ((((int)val[3]) & 0xFF)){
                    case 0x90:
                        throw new Bk8500CException("CheckSum Incorrect");
                    case 0xA0:
                        throw new Bk8500CException("Parameter Incorrect");
                    case 0xB0:
                        throw new Bk8500CException("Unrecognized Command"); 
                    case 0xC0:
                         throw new Bk8500CException("Invalid Command"); 
                    default:
                          throw new Bk8500CException("Unknown problem"); 
                }
                }
                byte[] val2 = val;
                val2[26] = 0x00;
                int checksumR = checkSum(val2);
                if(val[26]!=checksumR){
                    throw new Bk8500CException("Response from device has been scrambled"); 
                }         
            }
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
