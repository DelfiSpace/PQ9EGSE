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
 * @author LocalAdmin
 */
public class PingTestInvalidParameters 
{
    private static PQ9DataClient caseClient;
    protected final static long NANTOMIL = 1000*1000;
    private static StringBuilder output;     
    private JSONObject commandRaw;
    protected JSONObject reply;
    
    @BeforeClass 
    public static void BeforePingTestClass() throws IOException 
    {   
        caseClient = new PQ9DataClient("localhost", 10000);
        System.out.println("Initializer of Ping Test with Invalid Parameters ");
        caseClient.setTimeout(TestParameters.getTimeOut());     
    }
    
    @Before
    public void setup() throws IOException
    {
        output = new StringBuilder(""); 
        commandRaw = new JSONObject();
        commandRaw.put("_send_", "SendRaw");
        commandRaw.put("dest", "2");
        commandRaw.put("src", "1");
        commandRaw.put("data", "17 1");
        reply = new JSONObject();
    }
   
    @Test
    public void testPingRAW() throws IOException, ParseException, TimeoutException
    {
         
       caseClient.sendFrame(commandRaw);  
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
    
    @Test
    public void testPingLoopDestinations() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        String where = String.valueOf(2);
        boolean boardConnected = false;
        for(int i=0; i < 12; i++){
            where = String.valueOf(i);
            commandRaw.put("dest", where); 
            caseClient.sendFrame(commandRaw);  
            try{
            reply = caseClient.getFrame();
            boardConnected = true;
            }catch(TimeoutException Ex){
                output.append("No reply from board no. ").append(String.valueOf(i)).append("\n");
                Assert.assertEquals("No reply from ", String.valueOf(i),String.valueOf(i));
                boardConnected = false;
            } 
            if(boardConnected)
            {
                String boardName = reply.get("Source").toString();
                boardName = getSource(boardName);
                Assert.assertTrue("Unknown board", TestParameters.isKnown(boardName));
                Assert.assertEquals("PingService", reply.get("_received_").toString()); 
                Assert.assertEquals(getExpectedReply("Service"), reply.get("Service").toString());   
                Assert.assertEquals(getExpectedReply("Request"), reply.get("Request").toString()); 
                output.append("Succesfully Pinged board ").append(boardName).append("\n");;
            }
        }
    }

    @Test
    public void testPingDestinationOverflow() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        String where = String.valueOf(2);
        boolean boardConnected = false;
        for(int i=256; i < 256+12; i++){
            where = String.valueOf(i);
            commandRaw.put("dest", where); 
            caseClient.sendFrame(commandRaw);  
            try{
            reply = caseClient.getFrame();
            boardConnected = true;
            }catch(TimeoutException Ex){
                output.append("No reply from board no. ").append(String.valueOf(i)).append("\n");
                Assert.assertEquals("No reply from ", String.valueOf(i),String.valueOf(i));
                boardConnected = false;
            } 
            if(boardConnected)
            {
                String boardName = reply.get("Source").toString();
                boardName = getSource(boardName);
                Assert.assertTrue("Unknown board", TestParameters.isKnown(boardName));
                Assert.assertEquals("PingService", reply.get("_received_").toString()); 
                Assert.assertEquals(getExpectedReply("Service"), reply.get("Service").toString());   
                Assert.assertEquals(getExpectedReply("Request"), reply.get("Request").toString()); 
                output.append("Succesfully Pinged board ").append(boardName).append("\n");;
            }
        }
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
     
    protected JSONObject stringToJSON(String tobreak) throws ParseException
    {  
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
    protected String getSource(String source) throws ParseException
    {
        JSONObject tempJSON = stringToJSON(source);
        String value = tempJSON.get("value").toString();
        return value;
    }    
    protected String getService(String service) throws ParseException
    {
        JSONObject tempJSON = stringToJSON(service);
        String value = tempJSON.get("value").toString();
        return value;
    } 
    private String getExpectedReply(String service)
    {
        //return string example: "{\"valid\":\"true\",\"value\":\"COMMS\"}"
            StringBuilder exReply;
            exReply = new StringBuilder(40);
        
        if( service.equals("Request"))
        {
            exReply.append("{\"valid\":\"true\",\"value\":\"Reply\"}");
        }
        if( service.equals("Service")){
            exReply.append("{\"valid\":\"true\",\"value\":\"Ping\"}");
        }
        return exReply.toString();
    }
}
