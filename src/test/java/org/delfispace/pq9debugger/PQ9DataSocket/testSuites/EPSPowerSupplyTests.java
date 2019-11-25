/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.delfispace.pq9debugger.PQ9DataSocket.Frame;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TenmaDriver;
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
public class EPSPowerSupplyTests 
{
            StringBuilder output;
            JSONObject command;
            PQ9DataClient client;
            private final static String INPUT_FILE = "EPS.xml";
            private static XTCEDatabase db;
            private static XTCETMStream stream;
            private TenmaDriver powerSupply; 
            private PQ9DataClient caseClient;
            private JSONObject commandRaw;
            protected JSONObject reply;
            protected JSONObject commandPing;

/* Intended to test EPS functions, with regards to power and current handling.  
 * cut-off power, 
 * cut-off current,
 * Intended to be run after EPSBusHandlingTest         
*/    
    
 @BeforeClass 
    public void BeforeTestClass() throws IOException 
    {
        System.out.println("Initializer of PowerTestClass");
        output = new StringBuilder("");   
        StringBuilder mesgLoader = new StringBuilder("");
        mesgLoader.append("Power Supply is not responding. Is it connected to ");
        String Comport = "COM4";
        mesgLoader.append(Comport);
        mesgLoader.append("\n is this correctly connected?");
        
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TestParameters.getTimeOut());
        
        boolean failed = false;
        do
        {
            try
            {
                powerSupply = new TenmaDriver(Comport);
            }catch(IOException ex)
            {
                //JOPtion pane stack
                int res = JOptionPane.showConfirmDialog(null,
                mesgLoader.toString(),
                "There is a Problem",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.NO_OPTION){
                    System.exit(0);
                      }
                else {}
            }
        }while(failed);
    }
    
    @Before
    public void setup() throws IOException
    {
        powerSupply.setVoltage(4.1); 
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


