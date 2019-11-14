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
import org.json.simple.parser.JSONParser;
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
 * @author Michael van den Bos
 */
public class ResetTestCase
{
    private final static int TIMEOUT = 500; // in ms
    JSONObject reply;
    JSONObject commandReset;
    JSONObject commandGetTelemetry;
    static PQ9DataClient caseClient;
    private final static ZoneId LOCAL = ZoneId.of("Europe/Berlin");
    private final static long NANTOMIL = 1000*1000;
    private final String EPSPingService = "{\"valid\":\"true\",\"value\":\"Ping\"}";
    private final String EPSPingRequest = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    private final String EPSSource  = "{\"valid\":\"true\",\"value\":\"EPS\"}";  
    
    private final String ResetReply  = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    private final String ResetReplyDest = "{\"valid\":\"true\",\"value\":\"OBC\"}";
    private final String ResetService = "{\"valid\":\"true\",\"value\":\"Reset\"}";
    private final String ResetReplySize  = "{\"valid\":\"true\",\"value\":3\"}";
    private final String ResetSoft  ="{\"valid\":\"true\",\"value\":\"Soft\"}";
    private final String ResetHard  ="{\"valid\":\"true\",\"value\":\"Hard\"}";
    private final String ResetPC  ="{\"valid\":\"true\",\"value\":\"PowerCycle\"}";
    private final int testtimepar = 43; //wait time in miliseconds
     private static StringBuilder output = new StringBuilder("");
    
      @BeforeClass 
    public static void BeforeTestClass() throws IOException 
    {
        System.out.println("Initializer of PingTestClass 3 ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);     
        System.out.println("This next test will take about 10 minutes");
          
    }
    
    @Before
    public void setup() throws IOException
    {
        
        commandReset = new JSONObject();
        commandReset.put("_send_", "Reset");
        commandReset.put("Destination", "EPS");
        commandReset.put("Type", "Soft");
        
        commandGetTelemetry = new JSONObject();
        commandGetTelemetry.put("_send_", "GetTelemetry");
        commandGetTelemetry.put("Destination", "EPS");
    }
    
    @Test
    public void atestSoftReset() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(commandReset.get("Destination").toString(), reply.get("Source").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetSoft, reply.get("Reset").toString());
        Assert.assertEquals(commandReset.get("Source").toString(), reply.get("Destination").toString());
        
    }        
    @Test
    public void btestHardReset() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        commandReset.put("Type", "Hard");// alter Reset Type
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(commandReset.get("Destination").toString(), reply.get("Source").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetHard, reply.get("Reset").toString());
        Assert.assertEquals(commandReset.get("Source").toString(), reply.get("Destination").toString());
    }
     @Test
    public void cTestPowerCycle() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        commandReset.put("Type", "PowerCycle");// alter Reset Type
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(commandReset.get("Destination").toString(), reply.get("Source").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetPC, reply.get("Reset").toString());
        Assert.assertEquals(commandReset.get("Source").toString(), reply.get("Destination").toString());
    }
    
    @Test
    public void ctestInternalResetSSS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        
        output.append("This is a reset test. "); 
        output.append("/n");
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Thread.sleep(180000);
        //GET HOUSEKEEPING
        Thread.sleep(1500);// housekeeping data is refreshed every 1000 miliseconds. 
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        output.append(reply.get("Uptime").toString());
        String uptimeS = reply.get("Uptime").toString();
        output.append(uptimeS); 
        output.append("/n");
        JSONParser parser = new JSONParser();
        JSONObject tempJSON = (JSONObject) parser.parse(uptimeS);
        int uptime = Integer.valueOf(tempJSON.get("value").toString());
        Assert.assertTrue("uptime should be less than 5", uptime < 5);
    }
    /*
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
*/
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
