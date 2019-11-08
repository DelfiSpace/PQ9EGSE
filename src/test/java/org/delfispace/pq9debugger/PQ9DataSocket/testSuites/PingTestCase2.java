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
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class PingTestCase2 {
     private final static int TIMEOUT = 300; // in ms
            Frame reply;
            
            
            
            @SuppressWarnings("unchecked")
    public PingTestCase2() throws IOException, ParseException, TimeoutException {
        PQ9DataClient client = new PQ9DataClient("localhost", 10000);
        client.setTimeout(TIMEOUT);
        JSONObject command = new JSONObject();
            command.put("_send_", "Ping");
            command.put("Destination", "EPS");
        client.sendFrame(command);    
        reply = client.getFrame2();
            //String replyService = ;
        
    }
    @Test
    public void testPing(){
        Assert.assertEquals("Reply", reply.get("Service").getValue());  
    }
}
