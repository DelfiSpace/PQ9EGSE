/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import java.io.IOException;
import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.delfispace.pq9debugger.*;
import org.delfispace.pq9debugger.PQ9DataSocket.Frame;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
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
            Frame reply;
            JSONObject command;
            PQ9DataClient client;
            private final static String INPUT_FILE = "EPS.xml";
            private static XTCEDatabase db;
            private static XTCETMStream stream;
            
            
            @SuppressWarnings("unchecked")
    public PingTestCase1() throws IOException, ParseException, TimeoutException {
        //System.out.print("inside PingTestCase1");
        client = new PQ9DataClient("localhost", 10000);
        client.setTimeout(TIMEOUT);
        
            //String replyService = ;
        
    }
    @Test
    public void testPing() throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException{
        command = new JSONObject();
            command.put("_send_", "Ping");
            command.put("Destination", "EPS");
        client.sendFrame(command);    
        reply = client.getFrame2();
        PQ9 pingResponse = new PQ9(1, 1, new byte[]{(byte)0x11, (byte)0x02});
        XTCEContainerContentModel pingResponseDecoded = 
                stream.processStream( pingResponse.getFrame() );
        Assert.assertEquals("PingService", pingResponseDecoded.getName()); 
    }
}
