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
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import static org.junit.Assert.assertEquals;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.RunWith;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMStream;



/**
 *
 * @author LocalAdmin
 */
public class TestRunner {
    private static XTCEDatabase db;
            private static XTCETMStream stream;
    
    
   public static void main(String[]args)throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException {
    

          
            //PQ9DataClient client;
            int TIMEOUT = 300; // in ms
           
          
            
        
            
    //PQ9DataClient client = new PQ9DataClient("localhost", 10000); 
        
    //          JSONObject command = new JSONObject();
    //        command.put("_send_", "Ping");
    //        command.put("Destination", "EPS");
     //       client.setTimeout(TIMEOUT);
            //client.sendFrame(command);    
            //Frame reply;
           
            
            
        
          //  System.out.println(assertEquals(reply.get("Service").getValue(), "Reply")); 
        PQ9 pingResponse = new PQ9(1, 2, new byte[]{(byte)0x11, (byte)0x02});
        // reference of PQ9 below: 
        // public PQ9(int destination, int source, byte[] input) throws PQ9Exception
        XTCEContainerContentModel pingResponseDecoded;
        byte[] twili = pingResponse.getFrame();
        for(int item = 0; item<twili.length; item++)
                    {  
                      System.out.print(String.format("%02X ", twili[item] & 0xFF));
                    }System.out.println();
        //pingResponseDecoded = stream.processStream( pingResponse.getFrame() );
        //System.out.println(pingResponseDecoded);




       Result result = JUnitCore.runClasses(PingTestSuite.class);
      
      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
   }
}  	
