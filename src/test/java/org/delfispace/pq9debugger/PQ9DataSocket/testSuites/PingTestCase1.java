/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.BusTestCase1.caseClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author LocalAdmin
 */
public class PingTestCase1 extends TestVarsMethods
{ 
    private String sDestination;
                
    @BeforeClass 
    public static void BeforePingTestClass() 
    {
        System.out.println("Initializer of PingTestClass1 ");
        output = new StringBuilder("");   
    }
    
    @Before
    public void setup() throws IOException
    {
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);    
        commandPing = new JSONObject();
        commandPing.put("_send_", "Ping");
        commandPing.put("Destination", "EPS");
        String sDestination = (String)commandPing.get("Destination");
    }
    
    @Test(timeout=1000)
    public void testPingOne() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandPing);  
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
    
    @Test(timeout=1000)
    public void testPingTwo() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandPing);  
       reply = caseClient.getFrame();
       Assert.assertEquals(PingService, reply.get("Service").toString());   
    }
    @Test(timeout=1000)
    public void testPingthree() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandPing);  
       reply = caseClient.getFrame();
       Assert.assertEquals(PingRequest, reply.get("Request").toString()); 
    }
     @Test(timeout=1000)
    public void testPingFour() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandPing);  
       reply = caseClient.getFrame();
       Assert.assertEquals(EPSSource, reply.get("Source").toString()); 
    }    
    @Test
    public void testPingMany() throws IOException, ParseException, TimeoutException
    {
            int testruns = 1200;
            long[] results = new long[testruns];
            long pt;
            long et;
            long elapsed;
            for(int i = 0; i<testruns; i++){
                Instant before = Instant.now();
                pt = System.nanoTime();
                caseClient.sendFrame(commandPing);  
                try{
                    reply = caseClient.getFrame();
                }catch (TimeoutException ex){
                    results[i]=-1;
                } 
                et = System.nanoTime()-pt;
                elapsed = et/NANTOMIL;
                if(results[i]==-1){}
                else{
                if("PingService".equals(reply.get("_received_").toString())) {
                    if(PingService.equals(reply.get("Service").toString())){
                        if(PingRequest.equals(reply.get("Request").toString())){
                            if(EPSSource.equals(reply.get("Source").toString())){
                            results[i]=elapsed;
                            }
                            else{
                                Assert.assertEquals("Source should have been: " + sDestination + " but Source = ",reply.get("Source").toString());
                            }
                        }
                        else{
                            Assert.assertEquals("Service should have been: Ping, but Service = ",reply.get("Request").toString()); 
                        }
                    } 
                    else{
                        Assert.assertEquals("Request should have been: Reply but Request = ",reply.get("Service").toString()); 
                    }
                }
                else {
                    Assert.assertEquals("_received_ should have been: PingService but _recieved_ = ",reply.get("_received_").toString()); 
                }
                }
            }
            int timeoutrate=0;
            for(int i=0; i<testruns; i++){
                if (results[i]==-1){timeoutrate=timeoutrate++;} 
            }
            double timeoutrateR;
            timeoutrateR = ((double)timeoutrate/(double)testruns)*100;
            String eRmessage = "The failure rate is above 0.001//% ";
            eRmessage = eRmessage + String.valueOf(timeoutrateR);
            Assert.assertTrue(eRmessage,timeoutrate<1);  
    }
    
    @After
    public void tearDown() throws IOException
    {
        
    }
    
    @AfterClass
     public static void shutDown() throws IOException
    {
        caseClient.close();
    }
}
