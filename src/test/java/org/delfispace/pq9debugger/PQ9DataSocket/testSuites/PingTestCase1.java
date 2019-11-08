/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
import org.xtce.toolkit.XTCEDatabaseException;
import org.delfispace.protocols.pq9.PQ9Exception;
/**
 *
 * @author LocalAdmin
 */
public class PingTestCase1{
            private final static int TIMEOUT = 300; // in ms
            JSONObject reply;
            JSONObject commandP;
            static PQ9DataClient caseClient;
                
    @BeforeClass 
    public static void BeforePingTestClass() throws IOException, ParseException, TimeoutException {
        System.out.println("Initializer of PingTestClass ");
    }
    @Before
    public void setup(){
        // do nothing
    }
    
    @Test(timeout=1000)
            @SuppressWarnings("unchecked")
    public void testPing() throws Exception, IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException{
       caseClient = new PQ9DataClient("localhost", 10000);
       caseClient.setTimeout(TIMEOUT);    
       commandP = new JSONObject();
       commandP.put("_send_", "Ping");
       commandP.put("Destination", "EPS");
       caseClient.sendFrame(commandP);  
       try{
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
       } catch (TimeoutException ex) 
                {
                   // Assert.assertEquals(true, false); 
                }     
      
    }
    
    @Test(timeout=1000)
            @SuppressWarnings("unchecked")
    public void testPing2() throws Exception, IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException{
       caseClient = new PQ9DataClient("localhost", 10000);
       caseClient.setTimeout(TIMEOUT);    
       commandP = new JSONObject();
       commandP.put("_send_", "Ping");
       commandP.put("Destination", "EPS");
       caseClient.sendFrame(commandP);  
       try{
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
       } catch (TimeoutException ex) 
                {
                  Assert.assertEquals(true, false); 
                }     
      
    }
    
    
    
    @After
    public void tearDown(){
        System.out.println("Single Ping EPS Test Run");
    }
}
