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
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.PingTestCase1.caseClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
/**
 *
/**
 *
 * @author micha
 */
public class BusTestCase1 {
    private final static int TIMEOUT = 5000; // in ms
    JSONObject reply;
    JSONObject commandP;
    JSONObject commandR;
    static PQ9DataClient caseClient;
    private final String busIsOn = "{\"valid\":\"true\",\"value\":\"ON\"}";
    private final String busIsOff = "{\"valid\":\"true\",\"value\":\"OFF\"}";
    
    
    @BeforeClass 
    public static void BeforeBusTestClass() throws IOException 
    {
        System.out.println("Initializer of BusTestClass1 ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);    
    }    
    
    @Before
    public void setup() throws IOException
    {
        commandP = new JSONObject();
        commandR = new JSONObject();
        commandP.put("_send_", "EPSBusSW");
        commandP.put("state", "BUSSwOn");
        
        commandR.put("_send_", "GetTelemetry");
        commandR.put("Destination", "EPS");
    }
    
    @Test(timeout=1500)
    public void testBus4() throws IOException, ParseException, TimeoutException, InterruptedException
    {       
       commandP.put("EPSParam", "Bus4Sw"); 
       commandP.put("_send_", "EPSBusSW");
       commandP.put("state", "BUSSwOn");
       caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
      
       caseClient.sendFrame(commandR);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOn, reply.get("B4_state").toString()); 
    }
    
    
    @Test(timeout=1500)
    public void testBus3() throws IOException, ParseException, TimeoutException, InterruptedException
    {
       commandP.put("EPSParam", "Bus3Sw");  
       caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       //
       Thread.sleep(10);
       caseClient.sendFrame(commandR);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOn, reply.get("B3_state").toString()); 
    }
    
    @Test(timeout=5000)
    public void testBus2() throws IOException, ParseException, TimeoutException, InterruptedException
    {
       commandP.put("EPSParam", "Bus2Sw");  
       caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       //
       //Thread.sleep(10);
       caseClient.sendFrame(commandR);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
       Assert.assertEquals(busIsOn, reply.get("B2_state").toString()); 
    }
    
     @Test(timeout=1500)
    public void testBus4Inv() throws IOException, ParseException, TimeoutException, InterruptedException
    {       
       commandP.put("EPSParam", "Bus4Sw"); 
       commandP.put("state", "BUSSwOff");
       caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       //
       Thread.sleep(10);
       caseClient.sendFrame(commandR);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOff, reply.get("B4_state").toString()); 
    }
    
    @Test(timeout=1500)
    public void testBus3Inv() throws IOException, ParseException, TimeoutException, InterruptedException
    {
       commandP.put("EPSParam", "Bus3Sw");  
       commandP.put("state", "BUSSwOff");
       caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       //
       Thread.sleep(10);
       caseClient.sendFrame(commandR);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOff, reply.get("B3_state").toString()); 
    }
    
    @Test(timeout=1500)
    public void testBus2Inv() throws IOException, ParseException, TimeoutException, InterruptedException
    {
       commandP.put("EPSParam", "Bus2Sw");  
       commandP.put("state", "BUSSwOff");
       caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       //
       Thread.sleep(10);
       caseClient.sendFrame(commandR);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
       Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); 
    }
    
    @Test(timeout=10500)
    public void testBus2Dup() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        commandP.put("EPSParam", "Bus2Sw");  
        commandP.put("state", "BUSSwOn");
        caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        for(int i=0; i<10; i++){
            Thread.sleep(10);
            caseClient.sendFrame(commandP);  
        Thread.sleep(50);// MCU seems to need 50 ms to respond
        }
        //
        Thread.sleep(10);
        caseClient.sendFrame(commandR);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOn, reply.get("B2_state").toString()); 
    }
    
    @Test(timeout=15500)
    public void testBus3Dup() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        commandP.put("EPSParam", "Bus2Sw");  
        commandP.put("state", "BUSSwOn");
        caseClient.sendFrame(commandP);  
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        for(int i=0; i<10; i++){
            Thread.sleep(10);
            caseClient.sendFrame(commandP);  
        Thread.sleep(50);// MCU seems to need 50 ms to respond
        }
        commandP.put("state", "BUSSwOff");
         for(int i=0; i<10; i++){
            Thread.sleep(10);
            caseClient.sendFrame(commandP);  
        Thread.sleep(50);// MCU seems to need 50 ms to respond
        }
        //
        Thread.sleep(10);
        caseClient.sendFrame(commandR);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); 
    }
    
    
    @After
    public void tearDown() throws IOException
    {
        System.out.print("test complete");
    }
    
    @AfterClass
     public static void shutDown() throws IOException
    {
        caseClient.close();
    }
    
}

