/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import java.time.ZoneId;
import org.delfispace.pq9debugger.PQ9DataSocket.Frame;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.BusTestCase1.caseClient;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.PingTestCase1.caseClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class PingTestCase2 
{
    private final static int TIMEOUT = 500; // in ms
    JSONObject reply;
    JSONObject commandP;
    static PQ9DataClient caseClient;
    private final static ZoneId LOCAL = ZoneId.of("Europe/Berlin");
    private final static long NANTOMIL = 1000*1000;
    private final String EPSPingService = "{\"valid\":\"true\",\"value\":\"Ping\"}";
    private final String EPSPingRequest = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    private final String EPSPingSource  = "{\"valid\":\"true\",\"value\":\"EPS\"}";  
    private final int testtimepar = 43; //wait time in miliseconds
     private static StringBuilder output = new StringBuilder("");
    
      @BeforeClass 
    public static void BeforePingTestClass() throws IOException 
    {
        System.out.println("Initializer of PingTestClass1 ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);     
          
    }
    
    @Before
    public void setup() throws IOException
    {
        
        commandP = new JSONObject();
        commandP.put("_send_", "SendRaw");
        commandP.put("dest", "2");
        commandP.put("src", "1");
        commandP.put("data", "17 1");
    }
    
    @Test
    public void atestPingSSS() throws IOException, ParseException, TimeoutException
    {
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
      @Test
    public void btestPingSSS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        String where = String.valueOf(2);
        for(int i=0; i < 12; i++){
            where = String.valueOf(i);
            commandP.put("dest", where); 
            caseClient.sendFrame(commandP);  
            try{
            reply = caseClient.getFrame();
            }catch(TimeoutException Ex){
                String successString = "No reply from board no. " + String.valueOf(i)+"/n" ;
                output.append(successString);
                Assert.assertEquals("No reply from ", String.valueOf(i),String.valueOf(i));
            } 
            if(i == 2){ 
                Assert.assertEquals("PingService", reply.get("_received_").toString()); 
                Assert.assertEquals(EPSPingService, reply.get("Service").toString());   
                Assert.assertEquals(EPSPingRequest, reply.get("Request").toString()); 
                Assert.assertEquals(EPSPingSource, reply.get("Source").toString()); 
            }
            Thread.sleep(testtimepar);
        }
    }
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
