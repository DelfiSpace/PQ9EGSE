/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestVarsMethods.caseClient;
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
public class TestInvalidService {
    
    private static StringBuilder output;     
    private JSONObject commandRaw;
    protected JSONObject reply;
    private final int destination = TestParameters.getDestinationInt();
    private static PQ9DataClient caseClient; 
    
    @BeforeClass 
    public static void BeforeTestClass() throws IOException 
    {
        //TestParameters.setDestination("COMMS"); // REMOVE THIS LINE
        caseClient = new PQ9DataClient("localhost", 10000);
        System.out.println("Initializer of Service Test with random Parameters ");
        caseClient.setTimeout(TestParameters.getTimeOut());     
    }
    
    @Before
    public void setup() throws IOException
    {
        output = new StringBuilder(""); 
        commandRaw = new JSONObject();
        commandRaw.put("_send_", "SendRaw");
        commandRaw.put("dest", String.valueOf(destination));
        commandRaw.put("src", "1");
        commandRaw.put("data", "17 1");
        reply = new JSONObject();
    }
    /**/
    @Test
    public void testWrongService() throws IOException, ParseException
    {
        boolean replyToRequest;
        boolean pass = true;
        boolean responseToAll = true;
        System.out.println("getting started");
        StringBuilder failMessage = new StringBuilder("");
        StringBuilder errorMessage = new StringBuilder("[1, 2, ");
        errorMessage.append(String.valueOf(destination)).append(", 0, 0,");
        for(int i = 0; i <256; i++)
        { 
            StringBuilder data = new StringBuilder("");
            data.append(String.valueOf(i)).append(" 1");
            commandRaw.put("data", data.toString());
            caseClient.sendFrame(commandRaw);  
            try
            {
                reply = caseClient.getFrame();
                replyToRequest = true; 
            }catch(TimeoutException Ex)
            {
                output.append("No reply to service request. ").append(String.valueOf(i)).append("\n");         
                replyToRequest = false;
            } 
            if(replyToRequest)
            {
                String boardName;
                String serviceName;
                boolean correctSource = false;
                try{
                    boardName = reply.get("Source").toString();
                    correctSource = true;
                    serviceName = reply.get("Service").toString();
                    boardName = getSource(boardName);
                    output.append(boardName).append(" replied to ").append(String.valueOf(i)).append(" with ");
                    output.append(serviceName).append("\n");
                }catch(NullPointerException ex)
                        {
                           pass = false;
                           if(correctSource){boardName = reply.get("Source").toString();}
                           else{boardName = "Unknown Board!";}
                           StringBuilder rawReply = new StringBuilder(reply.get("_raw_").toString());
                           rawReply.delete(15, rawReply.length());
                           if(rawReply.toString().equals(errorMessage.toString()))
                           {
                               pass = true;
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
            else
            {failMessage.append("No response from board ").append(destination).append(" on commmand: ").append(String.valueOf(i)).append("\n");
            responseToAll = false;
            }
        }
        
        Assert.assertTrue(failMessage.toString(),responseToAll);
        Assert.assertTrue("An invalid thing happened", pass);
    }    
    /**/

    @After
    public void tearDown() throws IOException
    {
        System.out.println("test complete");
        System.out.println(output);
    }
    
     @AfterClass
     public static void shutDown() throws IOException, InterruptedException
    {
        caseClient.close();
        Thread.sleep(500);
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
  
}


