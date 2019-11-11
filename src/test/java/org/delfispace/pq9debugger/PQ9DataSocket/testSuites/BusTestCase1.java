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
    private final static int TIMEOUT = 300; // in ms
    JSONObject reply;
    JSONObject commandP;
    static PQ9DataClient caseClient;
    
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
    public void testPing() throws IOException, ParseException, TimeoutException
    {       
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
    
    @Test(timeout=1000)
    public void testPing2() throws IOException, ParseException, TimeoutException
    {
       caseClient.sendFrame(commandP);  
       reply = caseClient.getFrame();
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
    
    @After
    public void tearDown() throws IOException
    {
        caseClient.close();
    }
}
}
