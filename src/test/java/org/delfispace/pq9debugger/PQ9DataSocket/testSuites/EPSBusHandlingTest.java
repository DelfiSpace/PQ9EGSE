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
 * @author Michael van den Bos
 */


public class EPSBusHandlingTest 
{
    protected static PQ9DataClient caseClient;
    protected StringBuilder output;  
    protected JSONObject commandPing;
    protected JSONObject commandSetBus;
    protected JSONObject commandReset;
    protected JSONObject commandGetTelemetry;
    protected JSONObject reply;
    
    protected final String busIsOn = "{\"valid\":\"true\",\"value\":\"ON\"}";
    protected final String busIsOff = "{\"valid\":\"true\",\"value\":\"OFF\"}";
    protected final String servicePB = "{\"valid\":\"true\",\"value\":\"Execute\"}";
    protected final String replyPB = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    protected final String replyER = "{\"valid\":\"true\",\"value\":\"Error\"}";
    protected final String replySH = "{\"valid\":\"true\",\"value\":\"\"}";
    
    protected String destination;
    
    @BeforeClass 
    public static void BeforeBusTestClass() throws IOException 
    {
        System.out.println("Initializer of Bus Handeling Test for EPS ");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TestParameters.getTimeOut());    
    }    
    
    @Before
    public void setup() throws IOException
    {
        destination = TestParameters.getDestination(); 
        
            if(destination == null){
                destination = "EPS";
            }
        /**/
        output = new StringBuilder("");
        commandSetBus = new JSONObject();
        commandGetTelemetry = new JSONObject();
        commandSetBus.put("_send_", "PowerBusControl");
        commandGetTelemetry.put("_send_", "GetTelemetry");
        commandGetTelemetry.put("Destination", destination);
    }
    /*
    @Test(timeout=2500)
    public void testBus4() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {      
        testBus(4, true);
    }
    
    @Test(timeout=1500)
    public void testBus3() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
        testBus(3,true);
    }
    
    @Test(timeout=5000)
    public void testBus2() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
        testBus(2,true);
    }
    
    @Test(timeout=5000)
    public void testBus1() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
        testBus(1,true);
    }
    
     @Test(timeout=1500)
    public void testBus4Inv() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {       
        testBus(4, false);
    }
    
    @Test(timeout=1500)
    public void testBus3Inv() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
        testBus(3, false);
    }
    
    @Test(timeout=1500)
    public void testBus2Inv() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
        testBus(2, false);
    }
    @Test(timeout=1500)
    public void testBus1Inv() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
        testBus(1, false);
    }
   
    @Test(timeout=25500)
    public void testBus2Dup() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {    
    //  STEP 1 turn on BUS 2
        testBus(2,true);
    //  STEP 2 turn off BUS 2
        testBus(2,false);
    //  STEP 3 send same command multiple times
        commandSetBus.put("PowerBusState", "BusOn");
        for(int i=0; i<100; i++){
            if(i==50){commandSetBus.put("PowerBusState", "BusOff");
                caseClient.sendFrame(commandSetBus); commandSetBus.put("PowerBusState", "BusOn");
            }else{caseClient.sendFrame(commandSetBus); }
            reply = caseClient.getFrame();//get response
            Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());// validate response
        }
    //  STEP 4 Assert that last command is recieved correct, and bus is ON. 
        validateResponseToCommandBus(reply,true);
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOn, reply.get("B2_state").toString());
    //  Turn off BUS 2
        testBus(2,false);
    }
     
    @Test(timeout=15500)
    public void testBus3Dup() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
    //  STEP 1 turn on BUS     
        testBus(3,true);
    //  STEP 2 turn off BUS 
        testBus(3,false);
    //  STEP 3 turn off BUS 
        testBus(3,true);    
    }
        @Test(timeout=15500)
    public void testBus4Dup() throws IOException, ParseException, TimeoutException, InterruptedException, Exception
    {
    //  STEP 1 turn on BUS     
        testBus(4,true);
    //  STEP 2 turn off BUS 
        testBus(4,false);
    //  STEP 3 turn off BUS 
        testBus(4,true);  
    }
     
    */
       @Test(timeout=4500)
    public void testAllBusses() throws IOException, ParseException, TimeoutException, InterruptedException
    {   
        //Make sure all busses are off
        setAllBusses(false);
        //GET HOUSEKEEPING
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds. 
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B3_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B4_state").toString()); 
       
        //Make sure all busses are on
        setAllBusses(true);
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds. 
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOn, reply.get("B2_state").toString()); 
        Assert.assertEquals(busIsOn, reply.get("B3_state").toString()); 
        Assert.assertEquals(busIsOn, reply.get("B4_state").toString());
        
        //And off again
        setAllBusses(false);
        Thread.sleep(999);// housekeeping data is refreshed every 1000 miliseconds. 
        caseClient.sendFrame(commandGetTelemetry);
        reply = caseClient.getFrame();
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString());
        Assert.assertEquals(busIsOff, reply.get("B2_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B3_state").toString()); 
        Assert.assertEquals(busIsOff, reply.get("B4_state").toString()); 
    }
    
    /*
    public void jtestBusError() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        // do  not say which bus, expect error: 
        commandSetBus.put("PowerBusState", "BusOn");
        caseClient.sendFrame(commandSetBus);
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
    /**/

    protected void commandBus(int bus, boolean on_true_off_false) throws Exception
    {
        if(bus>0 && bus<5){
            StringBuilder value = new StringBuilder(4);
            value.append("Bus").append(String.valueOf(bus));
            commandSetBus.put("PowerBusParam", value.toString()); 
            System.out.print(value);
        }
        else{throw new Exception("I cannot command " + bus);}
        if(on_true_off_false)
        {
            commandSetBus.put("PowerBusState", "BusOn");
        }
        else
        {
            commandSetBus.put("PowerBusState", "BusOff");
        }
        caseClient.sendFrame(commandSetBus);
    }
    
    protected void validateResponseToCommandBus(JSONObject reply1, boolean goal_on_true_off_false)
    {
        StringBuilder keySH = new StringBuilder(replySH);
        if(goal_on_true_off_false)
        {
            keySH.insert(25,String.valueOf(1));
            
        }
        else
        {
            keySH.insert(25,String.valueOf(0));
        }
        Assert.assertEquals(servicePB, reply.get("Service").toString());
        Assert.assertEquals(replyPB, reply.get("Request").toString()); 
        Assert.assertEquals("PowerBusReply", reply.get("_received_").toString());
        Assert.assertEquals(keySH.toString(), reply.get("State").toString());// validate response
    }
    
    protected void testBus(int bus, boolean goal_on_true_off_false) throws Exception
    {
        commandBus(bus, goal_on_true_off_false); 
        reply = caseClient.getFrame();
        validateResponseToCommandBus(reply, goal_on_true_off_false);
        Thread.sleep(1000);// housekeeping data is refreshed every 1000 miliseconds.   
        caseClient.sendFrame(commandGetTelemetry); 
        reply = caseClient.getFrame(); 
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
        StringBuilder key = new StringBuilder(8);
        // assert that Bus is equal to goal.
        key.append("B").append(String.valueOf(bus)).append("_state");
        if(goal_on_true_off_false)
        {
            Assert.assertEquals(busIsOn, reply.get(key.toString()).toString()); 
        }
        else
        {
            Assert.assertEquals(busIsOff, reply.get(key.toString()).toString()); 
        }
    }
    
    protected void setAllBusses(boolean on_true_off_false) throws IOException, ParseException, TimeoutException
    {
        JSONObject reply1 = new JSONObject();
        if(on_true_off_false)
        {
            commandSetBus.put("PowerBusState", "BusOn");
        }
        else
        {
            commandSetBus.put("PowerBusState", "BusOff");
        }
        commandSetBus.put("PowerBusParam", "Bus2"); 
        caseClient.sendFrame(commandSetBus);  
        reply1 = caseClient.getFrame();
        commandSetBus.put("PowerBusParam", "Bus3"); 
        caseClient.sendFrame(commandSetBus); 
        reply1 = caseClient.getFrame(); 
        commandSetBus.put("PowerBusParam", "Bus4"); 
        caseClient.sendFrame(commandSetBus); 
        reply1 = caseClient.getFrame();
    }
}

