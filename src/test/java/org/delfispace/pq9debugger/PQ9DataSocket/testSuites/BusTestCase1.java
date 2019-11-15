/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.PingTestCase1.caseClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
import junit.runner.Version;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
/**
 *
/**
 *
 * @author michael van den Bos
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BusTestCase1 extends TestVarsMethods{
 
    @BeforeClass 
    public static void BeforeBusTestClass() throws IOException 
    {
        System.out.println("Initializer of BusTestClass1 ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TIMEOUT);   
        output = new StringBuilder("");
    }    
    
    @Before
    public void setup() throws IOException
    {
        commandSetBus = new JSONObject();
        commandGetTelemetry = new JSONObject();
        commandSetBus.put("_send_", "PowerBusControl");
        
        commandGetTelemetry.put("_send_", "GetTelemetry");
        commandGetTelemetry.put("Destination", "EPS");
    }
    
    @Test(timeout=2500)
   // @order(1)
    public void atestBus4() throws IOException, ParseException, TimeoutException, InterruptedException
    {       
       output.append("a"); // order of the test, these tests cannnot be run in parallel but should be run in alphabetical order 
       commandSetBus.put("PowerBusParam", "Bus4"); 
       commandSetBus.put("PowerBusState", "BusOn");
       caseClient.sendFrame(commandSetBus); //send command to turn on bus 4  
       reply = caseClient.getFrame();
       Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
       Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
       Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
       Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       caseClient.sendFrame(commandGetTelemetry); //request housekeeping data
       reply = caseClient.getFrame(); //receive housekeeping data
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOn, reply.get("B4_state").toString()); // assert that B4 is ON
    }
    
    @Test(timeout=1500)
    public void btestBus3() throws IOException, ParseException, TimeoutException, InterruptedException
    {
       output.append("b"); // order of the test
       commandSetBus.put("PowerBusParam", "Bus3"); // put can be used as replace
       commandSetBus.put("PowerBusState", "BusOn");
       caseClient.sendFrame(commandSetBus);//send command to turn on bus 3  
       reply = caseClient.getFrame();//get response
       Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
       Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
       Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
       Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       caseClient.sendFrame(commandGetTelemetry);//request housekeeping data
       reply = caseClient.getFrame();//receive housekeeping data
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOn, reply.get("B3_state").toString()); // assert that B3 is ON
    }
    
    @Test(timeout=5000)
    public void ctestBus2() throws IOException, ParseException, TimeoutException, InterruptedException
    {
       output.append("c"); // order of the test
       commandSetBus.put("PowerBusParam", "Bus2"); // put can be used as replace
       commandSetBus.put("PowerBusState", "BusOn");
       caseClient.sendFrame(commandSetBus);//send command to turn on bus 3  
       reply = caseClient.getFrame();//get response
       Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
       Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
       Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
       Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       caseClient.sendFrame(commandGetTelemetry);//request housekeeping data
       reply = caseClient.getFrame();//receive housekeeping data
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOn, reply.get("B2_state").toString()); // assert that B2 is ON
    }
    
     @Test(timeout=1500)
    public void dtestBus4Inv() throws IOException, ParseException, TimeoutException, InterruptedException
    {       
       output.append("d"); // order of the test
       commandSetBus.put("PowerBusParam", "Bus4"); // put can be used as replace
       commandSetBus.put("PowerBusState", "BusOff");
       caseClient.sendFrame(commandSetBus); 
       reply = caseClient.getFrame();//get response
       Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
       Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
       Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
       Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       caseClient.sendFrame(commandGetTelemetry);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOff, reply.get("B4_state").toString()); // assert that B4 is OFF
    }
    
    @Test(timeout=1500)
    public void etestBus3Inv() throws IOException, ParseException, TimeoutException, InterruptedException
    {
         output.append("e");
       commandSetBus.put("PowerBusParam", "Bus3"); // put can be used as replace
       commandSetBus.put("PowerBusState", "BusOff");
       caseClient.sendFrame(commandSetBus); 
       reply = caseClient.getFrame();//get response
       Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
       Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
       Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
       Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       caseClient.sendFrame(commandGetTelemetry);
       reply = caseClient.getFrame();
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOff, reply.get("B3_state").toString()); // assert that B3 is OFF
    }
    
    @Test(timeout=1500)
    public void ftestBus2Inv() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        output.append("f");
        commandSetBus.put("PowerBusParam", "Bus2"); // put can be used as replace
        commandSetBus.put("PowerBusState", "BusOff");
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame();//get response
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); // assert that B2 is OFF
    }
   
    @Test(timeout=25500)
    public void gtestBus2Dup() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        output.append("g");
    // STEP 1 turn on BUS 2
        commandSetBus.put("PowerBusParam", "Bus2"); // put can be used as replace
        commandSetBus.put("PowerBusState", "BusOn");
        caseClient.sendFrame(commandSetBus);  
        reply = caseClient.getFrame();//get response
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
    // STEP 2 assert that BUS 2 is ON
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        caseClient.sendFrame(commandGetTelemetry); // request housekeeping data
        reply = caseClient.getFrame(); // receive housekeeping data
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOn, reply.get("B2_state").toString()); 
    // STEP 3 turn off BUS 2
        commandSetBus.put("PowerBusState", "BusOff");
        caseClient.sendFrame(commandSetBus);  
        reply = caseClient.getFrame();//get response
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
    // STEP 4 assert that BUS 2 is OFF
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        caseClient.sendFrame(commandGetTelemetry); // request housekeeping data
        reply = caseClient.getFrame(); // receive housekeeping data
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); 
    //STEP 5 send same command multiple times
        commandSetBus.put("PowerBusState", "BusOn");
        for(int i=0; i<100; i++){
            if(i==50){commandSetBus.put("PowerBusState", "BusOff");
                caseClient.sendFrame(commandSetBus); commandSetBus.put("PowerBusState", "BusOn");
            }else{caseClient.sendFrame(commandSetBus); }
            reply = caseClient.getFrame();//get response
            Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        }
    //STEP 6 Assert that last command is recieved correct, and bus is ON. 
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOn, reply.get("B2_state").toString()); 
    }
     
    @Test(timeout=15500)
    public void htestBus3Dup() throws IOException, ParseException, TimeoutException, InterruptedException
    {
         output.append("h");
        commandSetBus.put("PowerBusParam", "Bus3"); // put can be used as replace
        commandSetBus.put("PowerBusState", "BusOn");
        caseClient.sendFrame(commandSetBus);//send command to turn on bus 3  
        reply = caseClient.getFrame();//get response
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        caseClient.sendFrame(commandGetTelemetry);//request housekeeping data
        reply = caseClient.getFrame();//receive housekeeping data
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
        Assert.assertEquals(busIsOn, reply.get("B3_state").toString()); // assert that B3 is ON
    
        commandSetBus.put("PowerBusState", "BusOff");
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame();//get response
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B3_state").toString()); // assert that B3 is OFF
    }
        @Test(timeout=15500)
    public void htestBus4Dup() throws IOException, ParseException, TimeoutException, InterruptedException
    {
         output.append("h");
       commandSetBus.put("PowerBusParam", "Bus4"); // put can be used as replace
       commandSetBus.put("PowerBusState", "BusOn");
       caseClient.sendFrame(commandSetBus); //send command to turn on bus 4  
       reply = caseClient.getFrame();
       Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
       Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
       Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
       Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
       caseClient.sendFrame(commandGetTelemetry); //request housekeeping data
       reply = caseClient.getFrame(); //receive housekeeping data
       Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
       Assert.assertEquals(busIsOn, reply.get("B4_state").toString()); // assert that B4 is ON
    
        commandSetBus.put("PowerBusState", "BusOff");
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame();//get response
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds.   
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B4_state").toString()); // assert that B4 is OFF
    }
     
    
       @Test(timeout=4500)
    public void itestBusAllTime() throws IOException, ParseException, TimeoutException, InterruptedException
    {   output.append("i"); //confirms test order
        // BUS2
        commandSetBus.put("PowerBusParam", "Bus2"); // put can be used as replace
        commandSetBus.put("PowerBusState", "BusOff");
        caseClient.sendFrame(commandSetBus);  
        reply = caseClient.getFrame();
        //BUS3
        commandSetBus.put("PowerBusParam", "Bus3"); // put can be used as replace
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame(); 
        //BUS4
        commandSetBus.put("PowerBusParam", "Bus4"); // put can be used as replace
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame();
        //GET HOUSEKEEPING
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds. 
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B3_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B4_state").toString()); 
        // BUS2
        commandSetBus.put("PowerBusParam", "Bus2"); // put can be used as replace
        commandSetBus.put("PowerBusState", "BusOn");
        caseClient.sendFrame(commandSetBus);  
        reply = caseClient.getFrame();
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyPB, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        Assert.assertEquals(replySH, reply.get("State").toString());// validate response
        //BUS3
        commandSetBus.put("PowerBusParam", "Bus3"); // put can be used as replace  
        caseClient.sendFrame(commandSetBus);
        reply = caseClient.getFrame();
        //BUS4
        commandSetBus.put("PowerBusParam", "Bus4"); // put can be used as replace
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame();
        //GET HOUSEKEEPING
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds. 
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOn, reply.get("B2_state").toString()); 
        Assert.assertEquals(busIsOn, reply.get("B3_state").toString()); 
        Assert.assertEquals(busIsOn, reply.get("B4_state").toString());
        commandSetBus.put("PowerBusParam", "Bus2"); // put can be used as replace
        commandSetBus.put("PowerBusState", "BusOff");
        caseClient.sendFrame(commandSetBus);  
        reply = caseClient.getFrame();
        //BUS3
        commandSetBus.put("PowerBusParam", "Bus3"); // put can be used as replace
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame(); 
        //BUS4
        commandSetBus.put("PowerBusParam", "Bus4"); // put can be used as replace
        caseClient.sendFrame(commandSetBus); 
        reply = caseClient.getFrame();
        //GET HOUSEKEEPING
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds. 
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B3_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B4_state").toString()); 
    }
    public void jtestBusError() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        // do  not say which bus, hihhihi
        output.append("j"); //confirms test order
        commandSetBus.put("PowerBusState", "BusOn");
        reply = caseClient.getFrame();
        Assert.assertEquals(servicePB, reply.get("Service").toString()); // validate response
        Assert.assertEquals(replyER, reply.get("Request").toString()); // validate response
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
    }
    
    
    @After
    public void tearDown() throws IOException
    {
        System.out.println("test complete");
        System.out.println(output);
    }
    
    @AfterClass
     public static void shutDown() throws IOException
    {
        caseClient.close();
    }
    
}

