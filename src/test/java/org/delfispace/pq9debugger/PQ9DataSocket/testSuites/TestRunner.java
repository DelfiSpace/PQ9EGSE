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
import static org.junit.Assert.assertEquals;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.RunWith;



/**
 *
 * @author LocalAdmin
 */
public class TestRunner {
    
    
    
   public static void main(String[]args)throws IOException, ParseException, TimeoutException {
    

          
            //PQ9DataClient client;
            int TIMEOUT = 300; // in ms
           
          
            
        
            
    PQ9DataClient client = new PQ9DataClient("localhost", 10000); 
        
              JSONObject command = new JSONObject();
            command.put("_send_", "Ping");
            command.put("Destination", "EPS");
            client.setTimeout(TIMEOUT);
            client.sendFrame(command);    
            Frame reply;
           
            
            
        
          //  System.out.println(assertEquals(reply.get("Service").getValue(), "Reply")); 
        




       Result result = JUnitCore.runClasses(PingTestSuite.class);
      
      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
   }
}  	
