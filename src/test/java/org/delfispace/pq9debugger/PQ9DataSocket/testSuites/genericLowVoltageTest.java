/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import org.delfispace.pq9debugger.PQ9DataSocket.BK8500Driver;
import org.delfispace.pq9debugger.PQ9DataSocket.Bk8500CException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TenmaDriver;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestClassInterface.askQuestionYESNO;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestClassInterface.genericPingAttempt;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class genericLowVoltageTest implements TestClassInterface 
{
    
    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    protected static StringBuilder output;
    protected static PQ9DataClient caseClient;
    protected static TenmaDriver powerSupply;
    protected static BK8500Driver programResistance;
    protected JSONObject commandSetBus;
    protected JSONObject commandPing;
    protected JSONObject reply; 
    protected JSONObject commandGetTelemetry;
    String destination;
    
/* Intended to test EPS functions, with regards to power and current handling.  
 * cut-off power, 
 * cut-off current,
 * Intended to be run after EPSBusHandlingTest         
*/    
    
 @BeforeClass 
    public static void BeforeTestClass() throws IOException, InterruptedException 
    {
        //TestParameters.setDestination("COMMS");
        output = new StringBuilder("");   
        StringBuilder mesgLoader = new StringBuilder("");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TestParameters.getTimeOut());
        boolean failed;
        do{
        try
        {
            powerSupply = new TenmaDriver(comportTenma);
            failed = false;
        }catch(IOException ex)
            {
                mesgLoader.append("Power Supply is not responding. Is it connected to ");
                mesgLoader.append(comportTenma).append("\n is this correctly connected?");
                // asks question: is Tenma connected correctly
                failed = askQuestionYESNO(mesgLoader.toString());
                // yes: try again, no: end program
                if(!failed){System.exit(0);}
            }
        }while(failed);
    }

    @Before
    public void setup() throws IOException, Bk8500CException, TimeoutException, InterruptedException
    {
        destination =  TestParameters.getDestination();
        commandPing = new JSONObject();
        commandPing.put("_send_", "Ping");
        commandPing.put("Destination", destination);
        reply = new JSONObject(); 
        commandGetTelemetry = new JSONObject();
        commandGetTelemetry.put("_send_", "GetTelemetry");
        commandGetTelemetry.put("Destination", destination);
        powerSupply.setVoltage(4.15); 
        powerSupply.setCurrent(0.5);
        powerSupply.sunUP();
        Thread.sleep(2500);
        System.out.println("start test: ");
    }
  
   
    @Test
      public void voltageSweepTest() throws IOException, ParseException, TimeoutException, InterruptedException, PQ9PingTestException
    {
        do{
        }while(askQuestionYESNO("Is the battery disconnected?")==false);
        Thread.sleep(100); 
        boolean working = true;
        working = genericPingAttempt(destination, caseClient);
        double d = 4.2;
        double volt;
        do{
            powerSupply.setVoltage(d);
            Thread.sleep(50);
            d = d-0.01;
            Thread.sleep(10);
            working = genericPingAttempt(destination, caseClient);
            volt = powerSupply.getVoltageAct();
            System.out.print("Voltage ");
            System.out.print(powerSupply.getVoltageAct());
            if(volt > 3.00)
            {
                Assert.assertTrue(working);
                System.out.println(" ON");
            }
            else
            {
                if(working)
                {
                    System.out.println(" ON");
                }
                else
                {
                    System.out.println(" OFF");
                }
            }
        }while(working);
        
        
        do{
            d = d+0.01;
            powerSupply.setVoltage(d);
            Thread.sleep(50);
            Thread.sleep(10);
            working = genericPingAttempt(destination, caseClient);
            volt = powerSupply.getVoltageAct();
            System.out.print("Voltage ");
            System.out.print(powerSupply.getVoltageAct());
            if(volt > 3.00)
            {
                Assert.assertTrue(working);
                System.out.println(" ON");
            }
            else
            {
                if(working)
                {
                    System.out.println(" ON");
                }
                else
                {
                    System.out.println(" OFF");
                }
            }
        }while(volt<3.3);
    }
    /**/
    @After
    public void tearDown() throws IOException, InterruptedException
    {
        System.out.println("test complete");
        System.out.println(output);
    }
    
    @AfterClass
     public static void shutDown() throws IOException
    {
        powerSupply.closePort();
        caseClient.close();
    }
    
    protected JSONObject stringToJSON(String tobreak) throws ParseException
    {  
        JSONParser parser = new JSONParser();
        JSONObject tempJSON = (JSONObject) parser.parse(tobreak);
        return tempJSON;
    }
    
}
