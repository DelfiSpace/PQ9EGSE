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
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestVarsMethods.output;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMStream;

/**
 *
 * @author LocalAdmin
 */
public class PowerTestCase extends TestVarsMethods {

            JSONObject command;
            PQ9DataClient client;
            private final static String INPUT_FILE = "EPS.xml";
            private static XTCEDatabase db;
            private static XTCETMStream stream;
    
    
 @BeforeClass 
    public static void BeforeTestClass() 
    {
        System.out.println("Initializer of PowerTestClass");
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
    @Test
    @SuppressWarnings("unchecked")
    public void testPingJJJ() throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException{
        command = new JSONObject();
            command.put("_send_", "Ping");
            command.put("Destination", "EPS");
        client.sendFrame(command);    
        reply = client.getFrame();
    }
}


