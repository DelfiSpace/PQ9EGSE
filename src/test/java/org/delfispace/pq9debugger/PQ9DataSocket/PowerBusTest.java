/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
/**
 *
 * @author LocalAdmin
 */
public class PowerBusTest {
    private final static int TIMEOUT = 495; // in ms
    private final PrintStream writer;
    
    JSONObject command = new JSONObject();
    JSONObject command0 = new JSONObject();
    JSONObject command1 = new JSONObject();
    JSONObject command2 = new JSONObject();
    PQ9DataClient client;
    
    public PowerBusTest(String filename) throws IOException{
        client = new PQ9DataClient("localhost", 10000);
        writer = new PrintStream(new FileOutputStream(filename, true));
        command0.put("dest", 2);
        command0.put("src", 1);
        client.setTimeout(TIMEOUT);
        command0.put("SendRaw", "0 2");
        client.sendFrame(command0);
        //String data = command0.getData();
        
        try{
            // does this command have a reply? 
            Frame reply = client.getFrame2();
        } catch (TimeoutException ex) 
                {
                    // nothing to do here
                    writer.println();
                    ex.printStackTrace();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }   
    }
    
    
   public static void main(String args[]) throws IOException
    {
        String filename = "PowerBusTest.txt";
        PowerBusTest TestBus = new PowerBusTest(filename);
    }
}
