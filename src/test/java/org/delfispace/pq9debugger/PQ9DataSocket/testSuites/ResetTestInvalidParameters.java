/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestClassInterface.WAITREFRESH;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestClassInterface.genericPingAttempt;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
public class ResetTestInvalidParameters implements TestClassInterface
{   
    private String dest;
    protected static PQ9DataClient caseClient;
    private JSONObject commandReset;
    protected JSONObject reply;
    protected StringBuilder output;
    protected JSONObject commandGetTelemetry;
    protected JSONObject commandPing;
    protected JSONObject commandRaw;
    
    
      @BeforeClass 
    public static void BeforeTestClass() throws IOException 
    {
        System.out.println("Initializer of ResetTestCase ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TestParameters.getTimeOut());     
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
        commandGetTelemetry.put("Destination", dest);
        commandRaw = new JSONObject();
        commandRaw.put("_send_", "SendRaw");
        commandRaw.put("dest", String.valueOf(TestParameters.getDestinationInt()));
        commandRaw.put("src", "1");
        commandRaw.put("data", "19 1");
        reply = new JSONObject();
    }
    
    @Test
    public void testSoftReset() throws IOException, ParseException, TimeoutException, InterruptedException
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
        Assert.assertEquals(dest, getSource(reply.get("Source").toString()));
        output.append(reply.get("Size").toString());
        output.append("\n");
        output.append(ResetReplySize);
        output.append("\n");
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetSoft, reply.get("Reset").toString());
        Assert.assertEquals(ResetReplyDest, reply.get("Destination").toString());
        Thread.sleep(WAITREFRESH);// required because EPS cannot respond when it is resetting.
        // the reset response is sent before the reset. 
        reply = getTelemetry(dest); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }      
    @Test
    public void testHardReset() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        int uptime;
        do{
            reply = getTelemetry(dest); // check uptime
            uptime = getUptime(reply.get("Uptime").toString());
            Thread.sleep(WAITREFRESH);
        }while(uptime < 3);
        commandReset.put("Type", "Hard");// alter Reset Type
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        Assert.assertEquals(ResetReply, reply.get("Request").toString());
        Assert.assertEquals(dest, getSource(reply.get("Source").toString()));
        Assert.assertEquals(ResetReplySize, reply.get("Size").toString());
        Assert.assertEquals(ResetHard, reply.get("Reset").toString());
        Assert.assertEquals(ResetReplyDest, reply.get("Destination").toString());
        Thread.sleep(WAITREFRESH);  // required because EPS cannot respond when it is resetting. 
        reply = getTelemetry(dest); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }
    
     @Test
    public void testPowerCycle() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        int uptime;
        do{
            reply = getTelemetry(dest); // check uptime
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
        reply = getTelemetry(dest); // check uptime
        uptime = getUptime(reply.get("Uptime").toString());
        Assert.assertTrue("Uptime should be less than 3", uptime < 3);
    }
    
   
    @Test
    public void testInternalReset() throws IOException, ParseException, TimeoutException, InterruptedException, PQ9PingTestException
    {
        output.append("This is a internal reset test.").append(dest); 
        caseClient.sendFrame(commandReset); // send reset command
        reply = caseClient.getFrame();
        Assert.assertEquals("ResetService", reply.get("_received_").toString());
        for(int i = 0; i<3; i++)
        {
            Thread.sleep(60000);
            if(!"EPS".equals(dest))
            {
                boolean ping = genericPingAttempt("EPS", caseClient); 
            }
        }
        
        //GET HOUSEKEEPING
        Thread.sleep(WAITREFRESH);// housekeeping data is refreshed every 1000 miliseconds. 
        reply = getTelemetry(dest);
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
     
    protected JSONObject getTelemetry(String subSystem) throws IOException, ParseException, TimeoutException
    {  
        JSONObject replyInt = new JSONObject();
        if(isKnown(subSystem, SUBSYSTEMS))
        {
            commandGetTelemetry.put("_send_", "GetTelemetry");
            commandGetTelemetry.put("Destination", subSystem);
            //commandGetTelemetry should be initialized by the @Before method of the test
            caseClient.sendFrame(commandGetTelemetry); //request housekeeping data
            replyInt = caseClient.getFrame(); //receive housekeeping data
            return replyInt;
        }
        else
        {
            replyInt.put("_recieved_", "Error Unknown Subsystem");
            return replyInt; // return here, there is something wrong thererfore test will fail. 
        }          
    }
    
    protected JSONObject resetSubSystem(String subSystem, String type)throws IOException, ParseException, TimeoutException{
        JSONObject replyInt = new JSONObject();
        if("Soft".equals(type) || "Hard".equals(type) || "PowerCycle".equals(type))
        {
            commandReset.put("Type", type);
        }
        else
        {
            replyInt.put("_received_", "Error Unknown Reset Type");
            return replyInt; // return here, there is something wrong thererfore test will fail.
        }
        if(isKnown(subSystem, SUBSYSTEMS))
        {
            commandReset.put("Destination", subSystem);
        }
        else
        {
            replyInt.put("_received_", "Error Unknown Subsystem");
            return replyInt; // return here, there is something wrong thererfore test will fail.
        }
        //commandReset should be initialized by the @Before method of the test
        caseClient.sendFrame(commandReset);  
        replyInt = caseClient.getFrame();
        return replyInt;   
    } 
    
    
    protected int timeStampGetMillis(String timestamp){
        String[] delta = breakTimestamp(timestamp);
            int loc = delta.length;
            int millis;
            millis = Integer.valueOf(delta[loc-1]);
        return millis;
    }
    protected String[] breakTimestamp(String timestamp){
        // Rememeber this only works if the timestamp formatting is not changed!
        String[] internalStringArray;
        String[] temp = timestamp.split(" ");// splits off the date
        String[] temp2 = temp[1].split(":");// splits up the time
        String[] temp3 = temp2[temp2.length-1].split("\\.");
        int size1 = temp2.length +2;
        internalStringArray = new String[size1];
        internalStringArray[0] = temp[0];
        int size2 = internalStringArray.length-2;
        System.arraycopy(temp2, 0, internalStringArray, 1, size1-2);
        System.arraycopy(temp3, 0, internalStringArray, size2, 2);
        return internalStringArray;
    }
    public String[] testBreakTimeStamp(String timestamp){
        String[] delta = breakTimestamp(timestamp);
        return delta;
    }
     public int testgetMillis(String timestamp){
        int delta = timeStampGetMillis(timestamp);
        return delta;
    }
    protected boolean isKnown(String x, String[] ax){
        for(String s: ax){
            if(x.equals(s))
                return true;
        }
        return false;
    } 
     public boolean testisKnown(String str, String[] Arr){
        boolean delta = isKnown(str,Arr); 
        return delta;
    }
     protected JSONObject stringToJSON(String tobreak) throws ParseException{
        
        JSONParser parser = new JSONParser();
        JSONObject tempJSON = (JSONObject) parser.parse(tobreak);
        return tempJSON;
     }
     protected int getUptime(String uptime) throws ParseException
     {
             JSONObject tempJSON = stringToJSON(uptime);
             int value = Integer.valueOf(tempJSON.get("value").toString());
             return value;
     }  
     protected String getSource(String SourceString) throws ParseException
     {
        JSONObject tempJSON = stringToJSON(SourceString);
             String value = tempJSON.get("value").toString();
             return value;
     }
}
