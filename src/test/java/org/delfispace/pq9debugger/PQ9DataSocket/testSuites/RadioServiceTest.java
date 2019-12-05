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
 * @author LocalAdmin
 */
public class RadioServiceTest implements TestClassInterface
{
    private static PQ9DataClient caseClient;
    protected final static long NANTOMIL = 1000*1000;
    private static StringBuilder output;     
    private JSONObject commandRadio;
    protected JSONObject reply;
    static private boolean connected; 
    
    @BeforeClass 
    public static void BeforeRadioServiceTest() throws IOException 
    {   
        caseClient = new PQ9DataClient("localhost", 10000);
        System.out.println("Initializer of RadioService Test");
        caseClient.setTimeout(TestParameters.getTimeOut());     
        connected = false;
    }
    
    @Before
    public void setup() throws IOException
    {
        output = new StringBuilder(""); 
        commandRadio = new JSONObject();
        commandRadio.put("_send_", "Radio");
        commandRadio.put("Destination", "COMMS");
        
        reply = new JSONObject();
    }
   
    @Test
    public void testPingRAW() throws IOException, ParseException, TimeoutException
    { 
       caseClient.sendFrame(commandRadio);  
       reply = caseClient.getFrame();
       Assert.assertEquals("RadioService", reply.get("_received_").toString()); 
       connected = true;
    }
    @Test
    public void getRXfreq() throws IOException, ParseException
    {
       checkCOMMS(); //should fail if COMMS not functioning, fails quickly, doesnt waste time
       commandRadio.put("Command","getRXFrequency");
       caseClient.sendFrame(commandRadio);
    }
    @Test
    public void getTXfreq() throws IOException, ParseException
    {
       checkCOMMS(); //should fail if COMMS not functioning, fails quickly, doesnt waste time
       commandRadio.put("Command","getTXFrequency");
       caseClient.sendFrame(commandRadio);
    }
    @Test
    public void getRXBitRate() throws IOException, ParseException
    {
       checkCOMMS(); //should fail if COMMS not functioning, fails quickly, doesnt waste time
       commandRadio.put("Command","getTXBitRate");
       caseClient.sendFrame(commandRadio);
    }
    @Test
    public void getTXBitRate() throws IOException, ParseException
    {
       checkCOMMS(); //should fail if COMMS not functioning, fails quickly, doesnt waste time
       commandRadio.put("Command","getTXBitRate");
       caseClient.sendFrame(commandRadio);
    }
    
    @After
    public void tearDown() throws IOException
    {
        System.out.println("test complete");
        System.out.println(output);
        System.out.println(connected);
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
    private String getExpectedReply(String service, String error)
    {
        //return string example: "{\"valid\":\"true\",\"value\":\"COMMS\"}"
            StringBuilder exReply;
            exReply = new StringBuilder(40);
        
        if( service.equals("Request"))
        {
            if(error.equals("Error"))
            {
                exReply.append("{\"valid\":\"true\",\"value\":\"Error\"}");
            }
            else
            {
                exReply.append("{\"valid\":\"true\",\"value\":\"Reply\"}");
            }
            
        }
        if( service.equals("Service")){
            exReply.append("{\"valid\":\"true\",\"value\":\"Ping\"}");
        }
        
        return exReply.toString();
    }
    
    protected JSONObject pingCOMMS() throws IOException, ParseException, TimeoutException{
        JSONObject replyInt = new JSONObject();
        JSONObject commandPing = new JSONObject();
            commandPing.put("Destination", "COMMS");
            commandPing.put("_send_", "Ping");
        caseClient.sendFrame(commandPing);  
        replyInt = caseClient.getFrame();
        return replyInt;   
    }  
    protected void checkCOMMS() throws IOException, ParseException
    {
        if(!connected)
        {
            try
            {
                pingCOMMS();
            }catch(TimeoutException Ex)
            {
                Assert.assertTrue("not connected", false);
                Ex.printStackTrace();
            }
        }
    }
}
