/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import com.fazecast.jSerialComm.SerialPort;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

/**
 *
 * @author micha
 */
public class BK8500TestDriver {
       protected final static double stopVoltage = 2.999;
       protected final static double testCurrent = 0.500;
    
    
    
     public static void main(String args[]) throws IOException, Bk8500CException, InterruptedException, TimeoutException 
    {
      
        Date start = new Date();
        long starttime = start.getTime();
        PrintStream writer;
        // find serial port. 
         SerialPort[] seenPorts = SerialPort.getCommPorts();
        // show serial ports // note that COM10 comes before COM2 in the array. 
        for (SerialPort item : seenPorts){System.out.println(item);}
        String portName;
        portName = "COM10"; //note this is device specific. 
        // start new driver
        String fileString; 
        int testCur = (int)(testCurrent*1000);
        fileString = "BKDATA_TEST_" + String.valueOf(testCur)+".txt";
        writer = new PrintStream(new FileOutputStream(fileString, true));
        BK8500Driver TestDriver = new BK8500Driver(portName);
        System.out.println("Test simpleCheck");
         byte[] testArray = new byte[26];
        for(int i = 0; i<testArray.length; i++)
        {
            testArray[i] = (byte)0x00;
        }
        testArray[3] = (byte)0x50;
         for(int item = 0; item<testArray.length; item++)
            {  
                System.out.print(String.format("%02X ", testArray[item] & 0xFF));
            }
            System.out.println();
        try
        {
            boolean right = TestDriver.simpleCheck(testArray);
            System.out.print("right = "); System.out.println(right);
        }catch(Bk8500CException Ex) 
        {
            System.out.println(Ex.toString());
        }
       
             
        
        
        
        
        System.out.println("Start Remote Operation");
        TestDriver.startRemoteOperation();
        System.out.println("get Response (remote operation)");
        byte[] response =  TestDriver.getAnyResponse(false);
        // getAnyResponse throws IOException, Bk8500CException
        // Bk8500CExceptions are not thrown if packetCheck is false. 
            for(int item = 0; item<response.length; item++)
            {  
                System.out.print(String.format("%02X ", response[item] & 0xFF));
            }
            System.out.println();
     
            
        TestDriver.startBatteryTest(stopVoltage, testCurrent);
        boolean go = true;
        boolean go2 = false;
        do{
        StringBuilder sb = new StringBuilder();
                Date now = new Date(); //gets local computer time
                double[] reply = TestDriver.getValues();
                sb.append((now.getTime()-starttime) + ", " + reply[0]+ ", " + reply[1]);
                writer.println(sb.toString());
                writer.flush();
                System.out.println(sb.toString());
                if(reply[1]>testCurrent+0.1 ){
                    byte[] bugCheck;
                    bugCheck = TestDriver.getAnyResponse(false);
                    System.out.println("Something has gone wrong");
                    for(int item = 0; item<bugCheck.length; item++)
                    {  
                      System.out.print(String.format("%02X ", bugCheck[item] & 0xFF));
                    }System.out.println();
                }
                
                if(reply[0]<2.9 && go2){go = false;}
                if(reply[1]<0.001 ){go2 = true;}
                if(reply[1]>0.01){go2 = false;}
                Thread.sleep(750);
        }while(go);
        
        TestDriver.endRemoteOperation();
        TestDriver.closePort();
            /*
        System.out.println("Set current limit");    
        TestDriver.setCurrentLim(1.0);
            System.out.println("get Response(Current Limit)");
            response =  TestDriver.getAnyResponse();// throws IOException, Bk8500CException;
            for(int item = 0; item<response.length; item++)
            {  
                System.out.print(String.format("%02X ", response[item] & 0xFF));
            }
            System.out.println();
         //JOPtion pane stack
         int res = JOptionPane.showConfirmDialog(null,
			      "Current limit is set, Would you like to continue?",
			      "end",
			      JOptionPane.YES_NO_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
		if (res == JOptionPane.NO_OPTION){
                    TestDriver.endRemoteOperation();
	    	 // System.exit(0);
	          }
	    else {}
         System.out.println("Set CC");        
         TestDriver.setMode("CC");
         System.out.println("get Response (CC mode)");
            response =  TestDriver.getAnyResponse();// throws IOException, Bk8500CException;
            for(int item = 0; item<response.length; item++)
            {  
                System.out.print(String.format("%02X ", response[item] & 0xFF));
            }
            System.out.println();
         //JOPtion pane stack
         res = JOptionPane.showConfirmDialog(null,
			      "mode is CC, Would you like to continue?",
			      "end",
			      JOptionPane.YES_NO_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
		if (res == JOptionPane.NO_OPTION){
                     TestDriver.endRemoteOperation();
	    	 //System.exit(0);
	          }
	    else {}
                
         System.out.println("Test Current");        
         TestDriver.setCurrentCC(0.3);
         System.out.println("get Response (current set)");
            response =  TestDriver.getAnyResponse();// throws IOException, Bk8500CException;
            for(int item = 0; item<response.length; item++)
            {  
                System.out.print(String.format("%02X ", response[item] & 0xFF));
            }
            System.out.println();
     
         System.out.println("Set Battery");        
         TestDriver.setFunction("BATTERY");
         System.out.println("get Response (set battery function)");
            response =  TestDriver.getAnyResponse();// throws IOException, Bk8500CException;
            for(int item = 0; item<response.length; item++)
            {  
                System.out.print(String.format("%02X ", response[item] & 0xFF));
            }
            System.out.println();
         //JOPtion pane stack
         res = JOptionPane.showConfirmDialog(null,
			      "Battery function set, Would you like to continue?",
			      "end",
			      JOptionPane.YES_NO_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
		if (res == JOptionPane.NO_OPTION){
                    TestDriver.endRemoteOperation();
                   // System.exit(0);
	        } else {}
         System.out.println("Set Battery minimum voltage"); 
          TestDriver.setMinVoltageBatteryTest((float)2.9);
         System.out.println("get Response (set battery end voltage)");
            response =  TestDriver.getAnyResponse();// throws IOException, Bk8500CException;
            for(int item = 0; item<response.length; item++)
            {  
                System.out.print(String.format("%02X ", response[item] & 0xFF));
            }
            System.out.println();
            //JOPtion pane stack
         res = JOptionPane.showConfirmDialog(null,
			      "Battery end point set, Would you like to continue?",
			      "end",
			      JOptionPane.YES_NO_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
		if (res == JOptionPane.NO_OPTION){
                    TestDriver.endRemoteOperation();
                  //System.exit(0);
	        }else{}
                
            
         res = JOptionPane.showConfirmDialog(null,
			      "Would you like to turn on the system?",
			      "end",
			      JOptionPane.YES_NO_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
		if (res == JOptionPane.NO_OPTION){
                    TestDriver.endRemoteOperation();
                    System.exit(0);
                }else {}
        TestDriver.turnLoadON();
        System.out.println("get Response (Turn On)");
            response =  TestDriver.getAnyResponse();// throws IOException, Bk8500CException;
            for(int item = 0; item<response.length; item++)
            {  
                System.out.print(String.format("%02X ", response[item] & 0xFF));
            }
            System.out.println();
          //JOPtion pane stack
        boolean go;
        go = false;
          do{
         res = JOptionPane.showConfirmDialog(null,
			      "Would you like to turn off the system?",
			      "end",
			      JOptionPane.YES_NO_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
		if(res == JOptionPane.YES_OPTION){
                    TestDriver.turnLoadOFF();
                    go = false;
                }
         
         if (res == JOptionPane.NO_OPTION){
             go = true;
	          }
	    else {}
         Thread.sleep(2000);
        }while(go);
        
         
         //Thread.sleep(20000);
         TestDriver.endRemoteOperation();
        System.exit(0);
         
         */
         
    }   
}
