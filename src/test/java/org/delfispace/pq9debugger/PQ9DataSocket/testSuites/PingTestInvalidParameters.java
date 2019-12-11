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
        commandRaw.put("dest", String.valueOf(TestParameters.getDestinationInt()));
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
    
    @Test
    public void testPingCommandOverflow() throws IOException, ParseException, TimeoutException, InterruptedException
    {//Testing what happens when calling pingservice with a improper command
        StringBuilder errorMessage = new StringBuilder("[1, 2, ");
        
        String where = String.valueOf(2);
        boolean[] boardConnected  = new boolean[12];
        for(int i=0; i < 12; i++){
            where = String.valueOf(i);
            commandRaw.put("dest", where); 
            caseClient.sendFrame(commandRaw);  
            try
            {
                reply = caseClient.getFrame();
                boardConnected[i] = true;
            }catch(TimeoutException Ex){
                boardConnected[i] = false;
            }
        }
        for(int b=0; b<12; b++)
        {
            errorMessage.append(String.valueOf(b)).append(", 0, 0,");
            if(boardConnected[b])
            {
                boolean boardReplied;
                for(int i=0; i < 256; i++)
                {
                    StringBuilder data = new StringBuilder("");
                    data.append("17 ").append(String.valueOf(i));
                    commandRaw.put("dest", String.valueOf(b)); 
                    
                    commandRaw.put("data", data.toString());
                    
                    caseClient.sendFrame(commandRaw);  
                    try{
                        reply = caseClient.getFrame();
                        boardReplied = true;
                    }catch(TimeoutException Ex)
                    {
                        output.append("No reply to service request. ").append(String.valueOf(i)).append("\n");         
                        boardReplied = false;
                    } 
                    if(boardReplied)
                    {
                        String boardName;
                        String serviceName;
                        boolean correctSource = false;
                        try
                        {
                            boardName = reply.get("Source").toString();
                            correctSource = true;
                            serviceName = reply.get("Service").toString();
                            boardName = getSource(boardName);
                            if(getExpectedReply("Request","Error").equals(reply.get("Request"))){}
                            else
                            {
                                output.append("contacting board: ").append(String.valueOf(b)).append(" ,");
                                output.append("Testing data: ").append(data.toString()).append("\n");
                                output.append(boardName).append(" replied to ").append(String.valueOf(i)).append(" with ");
                                output.append(serviceName);
                                output.append("and ").append(reply.get("Request").toString()).append("\n"); 
                            }
                        }catch(NullPointerException ex)
                        {
                            if(correctSource){boardName = reply.get("Source").toString();}
                            else{boardName = "Unknown Board!";}
                            StringBuilder rawReply = new StringBuilder(reply.get("_raw_").toString());
                            rawReply.delete(15, rawReply.length());
                            if(rawReply.toString().equals(errorMessage.toString()))
                                {
                                    output.append("recieved error message from command: ").append(String.valueOf(i)).append("\n");
                                }
                                else
                                {
                                    output.append("command: ").append(String.valueOf(i)).append(" ([").append(commandRaw.get("dest").toString());
                                    output.append(", ").append(String.valueOf(2)).append(", ").append(commandRaw.get("src").toString());
                                    output.append(", ").append(String.valueOf(i)).append(", 1])\n");
                                    output.append("response: ").append(reply.get("_raw_").toString()).append("\n");
                                }

                        }
                    }
                }
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
}
