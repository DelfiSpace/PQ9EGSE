/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael van den Bos
 */

//This class send data packets to COMMS 
//It also attempts to retrieve data packets from COMMS. 
//If RX and TX are set to the same frequency these should be the same. 

//to-do: Define a way in the EPS.xml to transmit and receive data without the 
//use of RAW
public class RadioTransmissionTest 
{
    private static PQ9DataClient caseClient;
    protected final static long NANTOMIL = 1000*1000;
    private static StringBuilder output;     
    private JSONObject commandRaw;
    private JSONObject commandPing;
    private JSONObject commandGetFrame;
    protected JSONObject reply;
    
    @BeforeClass 
    public static void RadioTransmissionTest() throws IOException 
    {   
        caseClient = new PQ9DataClient("localhost", 10000);
        System.out.println("Initializer Radio transmission test");
        caseClient.setTimeout(TestParameters.getTimeOut());     
    }
    
    @Before
    public void setup() throws IOException
    {
        output = new StringBuilder(""); 
        commandRaw = new JSONObject();
        commandRaw.put("_send_", "SendRaw");
        commandRaw.put("dest", String.valueOf(TestParameters.getDestinationInt()));
        commandRaw.put("src", "1");
        commandRaw.put("data", "20 3"); // 20 3 = transmit
        reply = new JSONObject();
        commandPing = new JSONObject();
        commandPing.put("_send_", "Ping");
        commandPing.put("Destination", TestParameters.getDestination());
        commandGetFrame = new JSONObject();
        commandGetFrame.put("_send_","GETServiceRadio");
        commandGetFrame.put("RadioCommand","radioGDF");

    }
   
    @Test
    public void testTransmission() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        String transmX = "0x01 0x02 0x04 0x08 0x10 0x20 0x40 0x80 0xA 0x0B 0x0C 0x0D 0x0E 0x0F";
        caseClient.sendFrame(commandPing);
        reply = caseClient.getFrame();
        PQ9JSONObjectChecker Chk = new PQ9JSONObjectChecker(reply);
        Assert.assertTrue(Chk.checkPingReply());
        transmX = "20 3"+" "+transmX;
        commandRaw.put("data", transmX); // 20 3 = transmit
        caseClient.sendFrame(commandRaw);
        caseClient.getFrame();
        Thread.sleep(1000);
        caseClient.sendFrame(commandGetFrame);
        PQ9JSONObjectInterpreter intep = new PQ9JSONObjectInterpreter(caseClient.getFrame());
        String received = intep.getStringFromKey("_raw_");
        System.out.println(received);
    }
    /**/
    
    @After
    public void tearDown() throws IOException
    {
        System.out.println("test complete");
        System.out.println(output);
    }
    
     @AfterClass
     public static void shutDown() throws IOException
    {
        caseClient.close();
    }
    
}
