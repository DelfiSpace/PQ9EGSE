/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.delfispace.pq9debugger.*;
import org.delfispace.pq9debugger.PQ9DataSocket.Frame;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.json.simple.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMStream;
/**
 *
 * @author LocalAdmin
 */
public class PingTestCase1{
            private final static int TIMEOUT = 300; // in ms
            JSONObject reply;
            JSONObject command;
            PQ9DataClient client;
            private final static String INPUT_FILE = "EPS.xml";
            private static XTCEDatabase db;
            private static XTCETMStream stream;
            private JSONObject whatisit;
            
            
    @SuppressWarnings("unchecked") //@BeforeClass 
    public PingTestCase1() throws IOException, ParseException, TimeoutException {
        //System.out.print("inside PingTestCase1");
        client = new PQ9DataClient("localhost", 10000);
        client.setTimeout(TIMEOUT);
        System.out.println("Initializer of PingTest1 ");
        
            //String replyService = ;
        
    }
    @Test
    public void testPing() throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException{
        
      
        command = new JSONObject();
            command.put("_send_", "Ping");
            command.put("Destination", "EPS");
        client.sendFrame(command);    
       reply = client.getFrame();
       Assert.assertEquals(reply.get("_recieved_").toString(), "PingService");
       // Assert.assertEquals(reply.get("Destination").toString(), "OBC");
       // Assert.assertEquals(reply.get("Request").toString(), "Reply");
        whatisit = reply;
    }
    
    @After
    public void tearDown(){
        System.out.println("Single Ping EPS Test Run");
       // String jSondata = whatisit.toString();
       // System.out.println(jSondata);
        System.out.println(whatisit.get("_recieved_").toString());
        System.out.println("there");
    };
}
