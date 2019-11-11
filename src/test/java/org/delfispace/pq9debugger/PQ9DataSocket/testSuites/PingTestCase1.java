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
    private final static int TIMEOUT = 100; // in ms
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
        commandP.put("Destination", "EPS");
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
      // Assert.assertEquals("PingService", reply.get("_received_").toString());
      // String pow = reply.get("Ping").toString();
       Assert.assertEquals("{\"valid\":\"true\",\"value\":\"Ping\"}", reply.get("Service").toString());   
    }
    @Test(timeout=1000)
    public void testPingthree() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Assert.assertEquals("{\"valid\":\"true\",\"value\":\"Reply\"}", reply.get("Request").toString()); 
    }
     @Test(timeout=1000)
    public void testPingFour() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Assert.assertEquals("{\"valid\":\"true\",\"value\":\"EPS\"}", reply.get("Source").toString()); 
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
                caseClient.sendFrame(commandP);  
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
                    if("{\"valid\":\"true\",\"value\":\"Ping\"}".equals(reply.get("Service").toString())){
                        if("{\"valid\":\"true\",\"value\":\"Reply\"}".equals(reply.get("Request").toString())){
                            if("{\"valid\":\"true\",\"value\":\"EPS\"}".equals(reply.get("Source").toString())){
                            results[i]=elapsed;
                            }
                            else{
                                Assert.assertEquals("Service should have been: EPS, but Service = ",reply.get("Source").toString());
                            }
                        }
                        else{
                            Assert.assertEquals("Service should have been: Ping, but Service = ",reply.get("Request").toString()); 
                        }
                    } 
                    else{
                        Assert.assertEquals("Service should have been: Ping but Service = ",reply.get("Service").toString()); 
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
        caseClient.close();
    }
}
