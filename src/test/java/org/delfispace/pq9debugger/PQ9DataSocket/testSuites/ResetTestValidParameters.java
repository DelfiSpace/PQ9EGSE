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
public class ResetTestValidParameters extends TestVarsMethods
{   
    private String dest;
    
      @BeforeClass 
    public static void BeforeTestClass() throws IOException 
    {
        System.out.println("Initializer of ResetTestCase ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);     
        System.out.println("This next test will take about 10 minutes");
          
    }
    
    @Before
    public void setup() throws IOException
    {
        dest = TestParameters.getDestination();
        output = new StringBuilder("");   // can collect output from the test
        commandReset = new JSONObject();  // This is required for the test   
        commandReset.put("_send_", "Reset");    //This should remain during the test
        commandReset.put("Destination", dest); //This is overwritten by the tests
        commandReset.put("Type", "Soft");       //This is overwritten by the tests
        
        commandGetTelemetry = new JSONObject(); // This is required for the test  
        commandGetTelemetry.put("_send_", "GetTelemetry");
        commandGetTelemetry.put("Destination", "EPS");
        reply = new JSONObject();
    }
    
    @Test
    public void atestSoftResetEPS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        int uptime;
        do{
            reply = getTelemetry(dest); // check uptime
            uptime = getUptime(reply.get("Uptime").toString());
            Thread.sleep(WAITREFRESH);
        }while(uptime < 3);
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(EPSSource, reply.get("Source").toString());
        output.append(reply.get("Size").toString());
        output.append("\n");
        output.append(ResetReplySize);
        output.append("\n");
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetSoft, reply.get("Reset").toString());
        Assert.assertEquals(ResetReplyDest, reply.get("Destination").toString());
        //Thread.sleep(WAITREFRESH);
        reply = getTelemetry("EPS"); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }      
    @Test
    public void btestHardResetEPS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        int uptime;
        do{
            reply = getTelemetry("EPS"); // check uptime
            uptime = getUptime(reply.get("Uptime").toString());
            Thread.sleep(WAITREFRESH);
        }while(uptime < 3);
        commandReset.put("Type", "Hard");// alter Reset Type
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(EPSSource, reply.get("Source").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetHard, reply.get("Reset").toString());
        Assert.assertEquals(ResetReplyDest, reply.get("Destination").toString());
        reply = getTelemetry("EPS"); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }
    
     @Test
    public void cTestPowerCycleEPS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        int uptime;
        do{
            reply = getTelemetry("COMMS"); // check uptime
            uptime = getUptime(reply.get("Uptime").toString());
            Thread.sleep(WAITREFRESH);
        }while(uptime < 3);
        commandReset.put("Type", "PowerCycle");// alter Reset Type
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(commandReset.get("Destination").toString(), reply.get("Source").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetPC, reply.get("Reset").toString());
        Assert.assertEquals(commandReset.get("Source").toString(), reply.get("Destination").toString());
        //caseClient.sendFrame(commandReset); // send reset command
        reply = getTelemetry("EPS"); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }
    @Test
    public void etestSoftResetCOMMS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        int uptime;
        do{
            reply = getTelemetry("COMMS"); // check uptime
            uptime = getUptime(reply.get("Uptime").toString());
            Thread.sleep(WAITREFRESH);
        }while(uptime < 3);
        output.append("This is a reset test of COMMS, soft "); 
        output.append("\n");
        reply = resetSubSystem("COMMS", "Soft");
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals("COMMS", commandReset.get("Destination").toString());
        Assert.assertEquals(COMMSSource, reply.get("Source").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetSoft, reply.get("Reset").toString());
        Assert.assertEquals(ResetReplyDest, reply.get("Destination").toString());
        reply = getTelemetry("COMMS"); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        output.append(uptime);output.append("\n");
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }        
    
    @Test
    public void ftestHardResetCOMMS() throws IOException, ParseException, TimeoutException, InterruptedException
    {   int uptime;
        do{
            reply = getTelemetry("COMMS"); // check uptime
            uptime = getUptime(reply.get("Uptime").toString());
            Thread.sleep(WAITREFRESH);
        }while(uptime < 3);
        reply = resetSubSystem("COMMS", "Hard");
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(COMMSSource, reply.get("Source").toString());
        Assert.assertEquals("COMMS", commandReset.get("Destination").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetHard, reply.get("Reset").toString());
        Assert.assertEquals(ResetReplyDest, reply.get("Destination").toString());
        reply = getTelemetry("COMMS"); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }
     @Test
    public void gTestPowerCycleCOMMS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        output.append("This is a reset test of COMMS, PowerCycle "); 
        output.append("/n");
        reply = resetSubSystem("COMMS", "PowerCycle");
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(commandReset.get("Destination").toString(), reply.get("Source").toString());
        Assert.assertEquals("COMMS", commandReset.get("Destination").toString());
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetHard, reply.get("Reset").toString());
        Assert.assertEquals(commandReset.get("Source").toString(), reply.get("Destination").toString());
        reply = getTelemetry("COMMS"); // check uptime
        int uptime;
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
        // this last test may pass even if board has not reset but it should be very rare 
        // If needed the uptime can be checked before the reset as well to ensure. 
    }
    /*
    @Test
    public void ctestInternalResetEPS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        output.append("This is a internal reset test (EPS). \n"); 
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Thread.sleep(180000);
        //GET HOUSEKEEPING
        Thread.sleep(WAITREFRESH);// housekeeping data is refreshed every 1000 miliseconds. 
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
     @Test
    public void ctestInternalResetCOMMS() throws IOException, ParseException, TimeoutException, InterruptedException
    {   int uptime;
        output.append("This is a internal reset test (COMMS). \n"); 
        reply = resetSubSystem("COMMS", "Soft"); // soft reset 
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        //GET HOUSEKEEPING to check uptime
        reply = getTelemetry("COMMS");
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
        //uptime is less than 3, now wait 3 minutes
        for(int i = 0; i<3; i++){
            reply = pingSubSystem("EPS");
            Thread.sleep(60000); // pin EPS once every minute so it does not reset
        }// if EPS resets, so does COMMS.
        //GET HOUSEKEEPING
        reply = getTelemetry("COMMS");
        Assert.assertEquals("COMMSHousekeepingReply", reply.get("_received_").toString());
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("uptime should be less than 5", uptime < 5);
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