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
        commandRaw.put("dest", destination);
        commandRaw.put("src", "1");
        commandRaw.put("data", "17 1");
        reply = new JSONObject();
    }
    @Test
    public void testPingWrongService() throws IOException, ParseException
    {
        boolean replyToRequest;
        boolean pass = true;
        for(int i = 0; i <256; i++)
        { 
            replyToRequest = false;
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
                           serviceName = "An invalid response!";  
                           output.append("command: ").append(String.valueOf(i)).append(" ([").append(commandRaw.get("dest").toString());
                           output.append(", ").append(String.valueOf(2)).append(", ").append(commandRaw.get("src").toString());
                           output.append(", ").append(String.valueOf(i)).append(", 1])\n");
                           output.append("response: ").append(reply.get("_raw_").toString()).append("\n");
                        }
            }
        }
        Assert.assertTrue("An invalid thing happened", pass);
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


