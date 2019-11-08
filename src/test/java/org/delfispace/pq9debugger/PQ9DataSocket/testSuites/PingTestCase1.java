/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author LocalAdmin
 */
public class PingTestCase1
{
    private final static int TIMEOUT = 300; // in ms
    JSONObject reply;
    JSONObject commandP;
    static PQ9DataClient caseClient;
    private final static ZoneId LOCAL = ZoneId.of("Europe/Berlin");
    private final static long NANTOMIL = 1000*1000;
                
    @BeforeClass 
    public static void BeforePingTestClass() 
    {
        System.out.println("Initializer of PingTestClass1 ");
         
   
    }
    
    @Before
    public void setup() throws IOException
    {
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);    
        commandP = new JSONObject();
        commandP.put("_send_", "Ping");
        commandP.put("Destination", "COMMS");
    }
    
    @Test(timeout=1000)
    public void testPingOne() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
    @Test(timeout=1000)
    public void testPingTwo() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
      Assert.assertEquals("Reply", reply.get("Service").toString());   
    }
    @Test(timeout=1000)
    public void testPingthree() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Assert.assertEquals("Reply", reply.get("Request").toString()); 
    }
    
    
    
    @Test
    public void testPingMany() throws IOException, ParseException, TimeoutException
    {
       Date before = new Date();
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Date after = new Date();
       long timeSpent = after.getTime() - before.getTime();
       
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
    
    @After
    public void tearDown() throws IOException
    {
        caseClient.close();
    }
}
