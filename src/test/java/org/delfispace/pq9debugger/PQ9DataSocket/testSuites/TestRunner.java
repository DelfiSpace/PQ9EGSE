/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import java.io.IOException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.xtce.toolkit.XTCEDatabaseException;



/**
 *
 * @author LocalAdmin
 */
public class TestRunner {
   @SuppressWarnings("unchecked")
   public static void main(String[]args)throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException {
            int TIMEOUT = 300; // in ms            
            PQ9DataClient TestClient = new PQ9DataClient("localhost", 10000); 
            JSONObject commandT = new JSONObject();
            commandT.put("_send_", "Ping");
            commandT.put("Destination", "EPS");
            TestClient.setTimeout(TIMEOUT);
            TestClient.sendFrame(commandT);    
            JSONObject reply;
            reply = TestClient.getFrame();
            System.out.println(reply.get("_received_").toString());
        try{
       Result result = JUnitCore.runClasses(PingTestSuite.class);
        for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
        }catch(Exception ex){
        System.out.println(ex.getMessage());}
      
   }
}  	
