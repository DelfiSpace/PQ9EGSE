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
 * @author MFvandenBos
 */
public class TestRunner 
{
   public static void main(String[]args)throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException 
   {
        Result result = JUnitCore.runClasses(PingTestSuite.class);
        result.getFailures().forEach((failure) -> 
        {
            System.out.println(failure.toString());
        });		     
   }
}  	
