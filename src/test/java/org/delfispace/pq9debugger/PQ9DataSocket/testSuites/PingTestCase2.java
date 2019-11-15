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
public class PingTestCase2 extends TestVarsMethods
{
      @BeforeClass 
    public static void BeforePingTestClass() throws IOException 
    {
        System.out.println("Initializer of PingTestClass1 ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);     
          output = new StringBuilder("");
    }
    
    @Before
    public void setup() throws IOException
    {
        
        commandRaw = new JSONObject();
        commandRaw.put("_send_", "SendRaw");
        commandRaw.put("dest", "2");
        commandRaw.put("src", "1");
        commandRaw.put("data", "17 1");
    }
    
    @Test
    public void atestPingSSS() throws IOException, ParseException, TimeoutException
    {
       caseClient.sendFrame(commandRaw);  
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
      @Test
    public void btestPingSSS() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        String where = String.valueOf(2);
        for(int i=0; i < 12; i++){
            where = String.valueOf(i);
            commandRaw.put("dest", where); 
            caseClient.sendFrame(commandRaw);  
            try{
            reply = caseClient.getFrame();
            }catch(TimeoutException Ex){
                String successString = "No reply from board no. " + String.valueOf(i)+"/n" ;
                output.append(successString);
                Assert.assertEquals("No reply from ", String.valueOf(i),String.valueOf(i));
            } 
            if(i == NumEPS){ 
                Assert.assertEquals("PingService", reply.get("_received_").toString()); 
                Assert.assertEquals(PingService, reply.get("Service").toString());   
                Assert.assertEquals(PingRequest, reply.get("Request").toString()); 
                Assert.assertEquals(EPSSource, reply.get("Source").toString()); 
            }
             if(i == NumCOMMS){ 
                Assert.assertEquals("PingService", reply.get("_received_").toString()); 
                Assert.assertEquals(PingService, reply.get("Service").toString());   
                Assert.assertEquals(PingRequest, reply.get("Request").toString()); 
                Assert.assertEquals(COMMSSource, reply.get("Source").toString()); 
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
