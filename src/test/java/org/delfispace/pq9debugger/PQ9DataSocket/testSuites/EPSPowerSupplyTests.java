/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.delfispace.pq9debugger.PQ9DataSocket.BK8500Driver;
import org.delfispace.pq9debugger.PQ9DataSocket.Bk8500CException;
import org.delfispace.pq9debugger.PQ9DataSocket.Frame;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TenmaDriver;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestClassInterface.askQuestionYESNO;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestVarsMethods.output;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
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
public class EPSPowerSupplyTests implements TestClassInterface
{
    protected static PQ9DataClient caseClient;
    protected static TenmaDriver powerSupply;
    protected static BK8500Driver programResistance;
    protected JSONObject commandSetBus;
    protected JSONObject commandPing;
    protected JSONObject reply; 
    protected JSONObject commandGetTelemetry;
    
/* Intended to test EPS functions, with regards to power and current handling.  
 * cut-off power, 
 * cut-off current,
 * Intended to be run after EPSBusHandlingTest         
*/    
    
 @BeforeClass 
    public static void BeforeTestClass() throws IOException, InterruptedException 
    {
        System.out.println("Initializer of PowerTestClass");
        output = new StringBuilder("");   
        StringBuilder mesgLoader = new StringBuilder("");
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TestParameters.getTimeOut());
        
        boolean failed = false;
        boolean donotruntest = false;
        do
        {
            try
            {
                powerSupply = new TenmaDriver(comportTenma);
                donotruntest = false;
            }catch(IOException ex)
            {
                mesgLoader.append("Power Supply is not responding. Is it connected to ");
                mesgLoader.append(comportTenma).append("\n is this correctly connected?");
                // asks question: is Tenma connected correctly
                failed = askQuestionYESNO(mesgLoader.toString());
                // yes: try again, no: end program
                donotruntest = true;
            }
        }while(failed);
        if(donotruntest){
            System.out.println("Improper configuration for TEST");
            Thread.sleep(1000);
            System.exit(0);
        }
   
        failed = false;
        do
        {
            try
            {
                programResistance = new BK8500Driver(comportBK);
                donotruntest = false;
            }catch(IOException ex)
            {   mesgLoader.setLength(0);
                mesgLoader.append("Programmable Resistance is not responding. Is it connected to ");
                mesgLoader.append(comportBK);
                mesgLoader.append("\n is this correctly connected?"); 
                // asks question: is BK connected correctly
                failed = askQuestionYESNO(mesgLoader.toString());
                // yes: try again, no: end program
                donotruntest = true;
            }
        }while(failed);
        if(donotruntest)
        {   System.out.println("Improper configuration for test");
            Thread.sleep(1000);
            System.exit(0);
        }
    }

    
    
    @Before
    public void setup() throws IOException, Bk8500CException, TimeoutException, InterruptedException
    {
        String destination = "EPS";
        commandSetBus = new JSONObject();
        commandSetBus.put("_send_", "PowerBusControl");
        commandPing = new JSONObject();
        commandPing.put("_send_", "Ping");
        commandPing.put("Destination", destination);
        reply = new JSONObject(); 
        commandGetTelemetry = new JSONObject();
        commandGetTelemetry.put("_send_", "GetTelemetry");
        commandGetTelemetry.put("Destination", destination);
        powerSupply.setVoltage(4.15); 
        programResistance.startRemoteOperation();
        Thread.sleep(10);
        programResistance.setCurrentLim(2.5);
        powerSupply.setCurrent(2);
        powerSupply.sunUP();
        Thread.sleep(2500);
        System.out.println("start test: ");
    }
    /*
    @Test
    public void maximizeCurrentBus4() throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException, Exception{
        //set Bus 4 ON; 
        testBus(4, true);
        double resistance = 2.8/1.7; // assuming 3.2 volt bus  
        programResistance.setMode("CR");
        programResistance.setResistanceCR(resistance);
        Thread.sleep(100);
        programResistance.turnLoadON();
        StringBuilder outhere = new StringBuilder(100);
        double[] response = new double[4];
        for(int i = 0; i<25; i++)
        {
            response = programResistance.getValues();
            outhere.append("V: ").append(response[0]);
            outhere.append(" A: ").append(response[1]);
            System.out.println(outhere.toString());
            outhere.setLength(0);
        }
        Assert.assertTrue("protection does not work", programResistance.getCurrent()<1.6);
        testBus(4, false);
    }
    /**/
   
    @Test
      public void underVoltageProtectionTest() throws IOException, ParseException, TimeoutException, InterruptedException
    {
        double URBVoltage;
        do{
        }while(askQuestionYESNO("Is the battery disconnected?")==false);
        Thread.sleep(1500); // EPS needs time to boot
        caseClient.sendFrame(commandPing);
        reply = caseClient.getFrame();
        caseClient.sendFrame(commandGetTelemetry); 
        reply = caseClient.getFrame(); 
        Thread.sleep(100);
        Assert.assertTrue("GO", assertBus(1,true));

        double d = 3.5;
        
        do{
            powerSupply.setVoltage(d);
            Thread.sleep(50);
            d = d-0.01;
            System.out.print("PS voltage; ");
            System.out.println(powerSupply.getVoltageAct());
            Thread.sleep(1000);
            URBVoltage = getURBVoltage(); 
            System.out.print("URB Voltage : ");
            System.out.print(URBVoltage);
            System.out.print(" ");
            if(URBVoltage > 3.05)
            {
                assertBus(1,true);
                System.out.println("Bus 1 is on ");
            }
            else
            {
                System.out.print("Bus 1 is: ");
                System.out.println(checkBusState(1));
            }
            
        }while(URBVoltage>2.99);
        assertBus(1,false);
        
        do{
            d = d+0.01;
            System.out.print("PS voltage; ");
            System.out.println(powerSupply.getVoltageAct());
            powerSupply.setVoltage(d);
            Thread.sleep(1000);
            URBVoltage = getURBVoltage(); 
            System.out.print("URB Voltage : ");
            System.out.print(URBVoltage);
            if(URBVoltage < 3.18)
            {
                assertBus(1,false);
                System.out.println("Bus 1 is off");
            }
            else
            {
                System.out.print("Bus 1 is: ");
                System.out.println(checkBusState(1));
            }
        }while(URBVoltage<3.3);
        assertBus(1,true);
    }
    /**/
    @After
    public void tearDown() throws IOException, InterruptedException
    {
        System.out.println("test complete");
        System.out.println(output);
        programResistance.turnLoadOFF();
        //powerSupply.sunDown();
        programResistance.endRemoteOperation();
        Thread.sleep(1000);
    }
    
    @AfterClass
     public static void shutDown() throws IOException
    {
        powerSupply.closePort();
        programResistance.closePort();
        caseClient.close();
    }
    
    
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
    
    protected void testBus(int bus, boolean goal_on_true_off_false) throws Exception
    {
        commandBus(bus, goal_on_true_off_false); 
        reply = caseClient.getFrame();
        validateResponseToCommandBus(reply, goal_on_true_off_false);
        Thread.sleep(1000);// housekeeping data is refreshed every 1000 miliseconds.
        if(goal_on_true_off_false){Assert.assertTrue(assertBus(bus, goal_on_true_off_false));}
        else{Assert.assertTrue(assertBus(bus, goal_on_true_off_false)== false);}
    }
    
    protected boolean assertBus(int bus, boolean on) throws IOException, ParseException, TimeoutException
    {
        
        caseClient.sendFrame(commandGetTelemetry); 
        reply = caseClient.getFrame(); 
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
        StringBuilder key = new StringBuilder(8);
        // assert that Bus is equal to goal.
        key.append("B").append(String.valueOf(bus)).append("_state");
        if(on)
        {
            Assert.assertEquals(busIsOn, reply.get(key.toString()).toString()); 
            return true;
        }
        else
        {
            Assert.assertEquals(busIsOff, reply.get(key.toString()).toString()); 
            return false;
        }
    }
    
    protected String checkBusState(int bus) throws IOException, ParseException, TimeoutException
    {
        caseClient.sendFrame(commandGetTelemetry); 
        reply = caseClient.getFrame(); 
        Assert.assertEquals("EPSHousekeepingReply", reply.get("_received_").toString()); 
        StringBuilder key = new StringBuilder(8);
        // assert that Bus is equal to goal.
        key.append("B").append(String.valueOf(bus)).append("_state");
        if(reply.get(key.toString()).toString().equals(busIsOn))
        {
            return "ON";
        }
        else
        {
            if(reply.get(key.toString()).toString().equals(busIsOff))
            {
               return "OFF"; 
            }
            else
            {
                return "ERROR";
            }
        }
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
    protected double getURBVoltage() throws IOException, ParseException, TimeoutException
    {
        double output = 0.0;
        caseClient.sendFrame(commandGetTelemetry); 
        reply = caseClient.getFrame(); 
        JSONObject URB = stringToJSON(reply.get("URBVoltage").toString());
        output = Double.valueOf(URB.get("value").toString());
        return output;
    }
    
    protected JSONObject stringToJSON(String tobreak) throws ParseException
    {  
        JSONParser parser = new JSONParser();
        JSONObject tempJSON = (JSONObject) parser.parse(tobreak);
        return tempJSON;
    }
    
}


