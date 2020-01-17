/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.protocols.pq9;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.util.Arrays;
import org.json.simple.parser.ParseException;

/**
 *
 * @author stefanosperett
 */
public class TestHPI implements PQ9Receiver
{
    PCInterface pcInterface;
    SerialPort comPort;
    static boolean received;
            
    TestHPI(String port) throws PQ9Exception
    {
        // first time we connectot  a serial port            
        comPort = SerialPort.getCommPort(port);

        // open con port
        comPort.openPort();

        // configure the seriql port parameters
        comPort.setComPortParameters(230400, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

        // set the serial port in blocking mode
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        // crete the serial port reader
        pcInterface = new RS485PCInterface(comPort.getInputStream(), comPort.getOutputStream());
        
        pcInterface.setReceiverCallback(this);
        
        
    
    }
    
    public void send (PQ9 frame) throws IOException
    {
        pcInterface.send(frame);
    }
            
    public void sendRaw (int b) throws IOException
    {
        comPort.getOutputStream().write((byte)(b & 0xFF));
    }
    
    public static void main(String[] args) throws PQ9Exception, IOException
    {
        String port = "COM12";
        TestHPI t = new TestHPI(port);
        System.out.println("working");
        
        received = false;
        PQ9 frame = new PQ9(100, 1, new byte[]{17, 1});
        t.send(frame);
        System.out.println("PQ9FrameSending");
        
        while (!received){
            System.out.print("running");
        }
        System.out.println();
        t.sendRaw(20);
        
        received = false;
        System.out.println("setting received false");
        t.send(frame);
        while (!received){
            System.out.print("running");
        }
        System.out.println();
        System.out.println("Success!");
        System.exit(0);
    }

    @Override
    public void received(PQ9 msg) 
    {
        received = true;
        System.out.println("I got something");
    }
}
