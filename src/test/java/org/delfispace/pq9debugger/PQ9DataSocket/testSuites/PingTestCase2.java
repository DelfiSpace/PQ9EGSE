/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import org.delfispace.pq9debugger.PQ9DataSocket.Frame;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class PingTestCase2 
{
     private final static int TIMEOUT = 300; // in ms
            Frame reply;
            
      @BeforeClass 
    public static void BeforePingTestClass() throws IOException, ParseException, TimeoutException {
        System.out.println("Initializer of PingTestClass2 ");
    }              
    
    @Test
    public void testPingSSS()
    {
        System.out.println("test of PingTestClass2 ");
        Assert.assertEquals("Reply", reply.get("Service").getValue());  
    }
}
