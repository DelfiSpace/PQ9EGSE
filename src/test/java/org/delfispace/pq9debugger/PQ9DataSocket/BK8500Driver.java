/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;


import com.fazecast.jSerialComm.SerialPort;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
    private boolean RemoteOperation; 
      
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
        RemoteOperation = false;
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
    /*
        List of functions in class
        checkSum // calculates checksum
        closePort // closes Port
        byteToString // call this method to show cmd byte including all leading zeros. 
        startRemoteOperation // allows for remote operation 
        endRemoteOperation // ends remote operation 
        getModel // asks BK8500 to return model and make
        setMode // sets operational Mode CC = current control CV = voltage control CW  is power control CR is resistance control
        setFunction // sets functeional charachter 
        
    
        private
        commandHandler(byte, byte)  // function to construct command bytes, also starts comm
        commandHandler(byte) // returns byte array to fill
    */
    
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
        byte[]cmd;
        cmd = commandHandler((byte)0x20, (byte)0x01);
        sendCommand(cmd);
    }
    
     public void endRemoteOperation() throws IOException{
        //disable remote mode
        byte[]cmd;
        cmd = commandHandler((byte)0x20, (byte)0x00);
        sendCommand(cmd);
    }
    
    // get model number from device
    public void getModel() throws IOException{
        // add a return or print to this method. For sake of completeness
        byte[]cmd;
        cmd = commandHandler((byte)0x6A, (byte)0x00);
        sendCommand(cmd);
    }
    
    // set either CC CV CR or CW mode
    public void setMode(String mode) throws IOException, Bk8500CException{
        byte[]cmd;
        switch (mode) {
            case "CC":
                cmd = commandHandler((byte)0x28, (byte)0x00);
                break;
            case "CV":
                cmd = commandHandler((byte)0x28, (byte)0x01);
                break;
            case "CW":
                cmd = commandHandler((byte)0x28, (byte)0x02);
                break;
            case "CR":
                cmd = commandHandler((byte)0x28, (byte)0x03);
                break;
            default:
                throw new Bk8500CException("Mode selector unknown");              
        }
        sendCommand(cmd);
    }
    
    // set function, either FIXED, SHORT, TRANSIENT, LIST, BATTERY 
    public void setFunction(String sFunction) throws IOException, Bk8500CException{
        byte[]cmd;
        switch (sFunction) {
            case "FIXED":
                cmd = commandHandler((byte)0x5D, (byte)0x00);
                break;
            case "SHORT":
                cmd = commandHandler((byte)0x5D, (byte)0x01);
                break;
            case "TRANSIENT":
                cmd = commandHandler((byte)0x5D, (byte)0x02);
                break;
            case "LIST":
                cmd = commandHandler((byte)0x5D, (byte)0x03);
                break;
             case "BATTERY":
                cmd = commandHandler((byte)0x5D, (byte)0x04);
                break;    
            default:
                throw new Bk8500CException("Function selector unknown");              
        }
        sendCommand(cmd);
    }
    
    public void turnLoadON() throws IOException{
        //turn the load on
        byte[]cmd;
        cmd = commandHandler((byte)0x21, (byte)0x01);
        sendCommand(cmd);
    }
    
    public void turnLoadOFF() throws IOException{
        //turn the load off
        byte[]cmd;
        cmd = commandHandler((byte)0x21, (byte)0x00);
        sendCommand(cmd);
    }      
   
    public String getOperationMode() throws IOException{
        byte[]cmd;
        cmd = commandHandler((byte)0x29, (byte)0x00);
        sendCommand(cmd);
        return "still to do";
    }
    
    public String returnStatus() throws IOException{
        byte[]cmd;
        cmd = commandHandler((byte)0x12, (byte)0x00);
        sendCommand(cmd);
        return "still to do";
    }
    
    // set voltage limit for all operations
    public void setVoltageLim(double voltageLimV) throws IOException{
        byte[] cmd; // new byte[]
        cmd = commandHandler((byte)0x22); //basic array fill
        long lendian; // new long
        lendian = (long)(voltageLimV*1000); // voltage to mV integer;
        byte[] lendiantobyte = longToByte(lendian); // long gets transformed into Byte array. 
        cmd[3]=lendiantobyte[0]; // it is little endian
        cmd[4]=lendiantobyte[1]; // second byte    
        cmd[5]=lendiantobyte[2]; // it may be required to set these bytes as well. 
        cmd[6]=lendiantobyte[3];
        cmd[25]=(byte)checkSum(cmd); // calculate checksum
        // now we are ready to send the command
        System.out.println("Print Command Stream");
        for(int item = 0; item<cmd.length; item++)
        {  
            System.out.print(String.format("%02X ", cmd[item] & 0xFF));
        }
        System.out.println();
        //sendCommand(cmd);
    }
    
    // set current limit for all operations
    public void setCurrentLim(double currentLimA) throws IOException{
        byte[] cmd; // new byte[]
        cmd = commandHandler((byte)0x24); //basic array fill
        long lendian; // new long
        lendian = (long)(currentLimA*10000); // now calculate the bytes setting the current
        byte[] lendiantobyte = longToByte(lendian);// long gets transformed into Byte array. 
        cmd[3]=lendiantobyte[0]; // it is little endian
        cmd[4]=lendiantobyte[1];
        cmd[5]=lendiantobyte[2]; // it may be required to set these bytes as well. 
        cmd[6]=lendiantobyte[3];
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
    
    // set power limit for all operations
     public void setPowerLim(double powerLimWatt) throws IOException{
        byte[] cmd; // new byte[]
        cmd = commandHandler((byte)0x26); //basic array fill
        long lendian; // new long
        lendian = (long)(powerLimWatt*1000); // now calculate the bytes setting the current
        byte[] lendiantobyte = longToByte(lendian);// long gets transformed into Byte array. 
        cmd[3]=lendiantobyte[0]; // it is little endian
        cmd[4]=lendiantobyte[1];
        cmd[5]=lendiantobyte[2]; // it may be required to set these bytes as well. 
        cmd[6]=lendiantobyte[3];
       
        cmd[25]=(byte)checkSum(cmd);
        // now we are ready to send the command
        //System.out.println("Print Command Stream");
        for(int item = 0; item<cmd.length; item++)
        {  
           // System.out.print(String.format("%02X ", cmd[item] & 0xFF));
        }
        //System.out.println();
        sendCommand(cmd);
    }
     
    // set current for CC operation
    public void setCurrentCC(double currentCCA) throws IOException{
        byte[] cmd; // new byte[]
        cmd = commandHandler((byte)0x2A); //basic array fill
        long lendian; // new long
        lendian = (long)(currentCCA*10000); // now calculate the bytes setting the current
        byte[] lendiantobyte = longToByte(lendian);// long gets transformed into Byte array. 
        cmd[3]=lendiantobyte[0]; // it is little endian
        cmd[4]=lendiantobyte[1];
        cmd[5]=lendiantobyte[2]; // it may be required to set these bytes as well. 
        cmd[6]=lendiantobyte[3];
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
    
     public void setMinVoltageBatteryTest(double minV) throws IOException{
        byte[] cmd; // new byte[]
        cmd = commandHandler((byte)0x4E); //basic array fill 
        long minVolt = (long)(minV*1000); 
        byte[] cmdbyte = longToByte(minVolt);
        cmd[3]=(byte) cmdbyte[0];//least important bits go first. 
        cmd[4]=cmdbyte[1];
        cmd[5]=cmdbyte[2];
        cmd[6]=cmdbyte[3];
        cmd[25]=(byte)checkSum(cmd);
        // now we are ready to send the command
        System.out.println("Print Command Stream setMinVoltageBatteryTest");
        for(int item = 0; item<cmd.length; item++)
        {  
            System.out.print(String.format("%02X ", cmd[item] & 0xFF));
        }
        System.out.println();
        sendCommand(cmd);
    }
    
    // get display values 
    public double[] getValues() throws IOException, Bk8500CException{
        byte[] cmd;
        cmd = commandHandler((byte)0x5F, (byte)0x00); // sends command to get values. 
        sendCommand(cmd);
        byte[] response;
        response = getAnyResponse(false);// throws IOException, Bk8500CException;
        byte[] VoltageResponse = {0x00, 0x00, 0x00, 0x00}; 
        byte[] CurrentResponse = {0x00, 0x00, 0x00, 0x00}; 
        byte[] PowerResponse = {0x00, 0x00, 0x00, 0x00};
        byte[] DemandStateResponse = {0x00, 0x00, 0x00, 0x00};
        System.arraycopy(response, 3, VoltageResponse, 0, 4);
        System.arraycopy(response, 7, CurrentResponse, 0, 4);
        System.arraycopy(response, 11, PowerResponse, 0, 4);
        System.arraycopy(response, 16, DemandStateResponse, 0, 2);
        double[] results = {0.0, 0.0, 0.0, 0.0};
        results[0] = (double)byteToLong(VoltageResponse)/1000;
        results[1] = (double)byteToLong(CurrentResponse)/10000;
        results[2] = (double)byteToLong(PowerResponse)/1000;
        results[3] = (double)response[15];
//        results[4] = (double)byteToLong(DemandStateResponse);
        return results;
    }
    
    // battery test 
    public void startBatteryTest(double endVoltage, double testCurrent) throws IOException, Bk8500CException{
        //Excecute a batter test in CurrentControlled mode
        //check if remote connection is established. 
        setCurrentLim(1.0);    
        setMode("CC");   
        setCurrentCC(testCurrent);    
        setFunction("BATTERY");   
        // min voltage seems to have an overshoot
        setMinVoltageBatteryTest(endVoltage);   
        try {
            Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(BK8500Driver.class.getName()).log(Level.SEVERE, null, ex);
            }
        // packetCheck needs improving. 
    }

    // this function translates commands into byte[]
    // useful for the mode functions
    private byte[] commandHandler(byte b2Cmd, byte b3Cmd) throws IOException{
        byte[] cmd = new byte[26]; // instantiate new cmd array
        for(int item : cmd){cmd[item] = 0x00;}// set all command bytes to 0
        cmd[0]=(byte) 0xAA; // first byte is always 0xAA
        cmd[2]=(byte) b2Cmd; // command byte
        cmd[3]=(byte) b3Cmd; // command value
        cmd[25]=(byte)checkSum(cmd);
        return(cmd);
    }
    // overloaded function translates commands into byte[]
    // useful for the set functions. 
     private byte[] commandHandler(byte b2Cmd){
        byte[] cmd = new byte[26]; // instantiate new cmd array
        for(int item : cmd){cmd[item] = 0x00;}// set all command bytes to 0
        cmd[0]=(byte) 0xAA; // first byte is always 0xAA
        cmd[2]=(byte) b2Cmd; // command byte 
        // Checksum is not called because the packet is not yet finished. 
        return cmd;
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
        //System.out.println("I am sending " + os);
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
        /*System.out.println("Returned array ");
         for(int item = 0; item<val.length; item++)
        {  
            System.out.print(String.format("%02X ", val[item] & 0xFF));
        }System.out.println();
        */
        //byte[] val = is.readAllBytes(); 
        // a packet is always 26 bytes.  
        // run a packet check
        if(packetCheck(val)){};//everthing is going well if true
        return val;
    }
      public byte[] getAnyResponse(boolean pcheck) throws IOException, Bk8500CException
    {
        try 
        {
            Thread.sleep(10);
        } catch (InterruptedException ex) 
        {
            // ignoring the error
        }
        byte[] val = is.readNBytes(26); // a packet is always 26 bytes. 
        //System.out.println("Returned array ");
         for(int item = 0; item<val.length; item++)
        {  
          //  System.out.print(String.format("%02X ", val[item] & 0xFF));
        }//System.out.println();
       
        //byte[] val = is.readAllBytes(); 
        // a packet is always 26 bytes.  
        // run a packet check
        if(pcheck){
        if(packetCheck(val)){};//everthing is going well if true
        }
        
        return val;
    }
    
    private boolean packetCheck(byte[]val) throws Bk8500CException
    {
        byte testval1 = (byte)0x80;
        if(val[3] == testval1){}
        else 
            {
                System.out.println("I am here");
                if(0xAA==val[0]){
                    System.out.println("i am here too");
                    System.out.print(String.format("%02X ", val[3] & 0xFF));
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
                byte[] val2 = Arrays.copyOf(val, 26);
                val2[25] = 0x00;
                     System.out.println("val2 ");
                 for(int item = 0; item<val.length; item++)
                {     
                System.out.print(String.format("%02X ", val[item] & 0xFF));
                }System.out.println();
                int checksumR = checkSum(val2);
                if(val[25]!=checksumR){
                    throw new Bk8500CException("Response from device has been scrambled"); 
                }         
            }
        return true;
    }
    private byte[] longToByte(long lendian){
        byte[] lendiantobyte = new byte[8];
        lendiantobyte[0] = (byte)lendian;
        lendiantobyte[1] = (byte) (lendian >> 8);
        lendiantobyte[2] = (byte) (lendian >> 16);
        lendiantobyte[3] = (byte) (lendian >> 24);
        lendiantobyte[4] = (byte) (lendian >> 32);
        lendiantobyte[5] = (byte) (lendian >> 40);
        lendiantobyte[6] = (byte) (lendian >> 48);
        lendiantobyte[7] = (byte) (lendian >> 56);
        return lendiantobyte;
    }
    
    private long byteToLong(byte[] b){
        if (b.length!=4){
            System.out.println("give me 4 bytes");
        }
        long l = ((long) b[3] & 0xff) << 24 | ((long) b[2] & 0xff) << 16 | ((long) b[1] & 0xff) << 8 | ((long) b[0] & 0xff);
        return l;
    }
    
    
}
