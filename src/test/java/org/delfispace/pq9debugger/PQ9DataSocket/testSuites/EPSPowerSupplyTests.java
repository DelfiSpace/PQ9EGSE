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
            System.out.println("I am going to my trailer!!");
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
        {   System.out.println("I am going to my trailer!!");
            Thread.sleep(1000);
            System.exit(0);
        }
    }

    @Before
    public void setup() throws IOException, Bk8500CException, TimeoutException
    {
        commandSetBus = new JSONObject();
        commandSetBus.put("_send_", "PowerBusControl");
        commandPing = new JSONObject();
        reply = new JSONObject(); 
        commandGetTelemetry = new JSONObject();
        commandGetTelemetry.put("_send_", "GetTelemetry");
        commandGetTelemetry.put("Destination", "EPS");
        powerSupply.setVoltage(4.15); 
        programResistance.startRemoteOperation();
        programResistance.setCurrentLim(2.5);
        powerSupply.setCurrent(2);
        powerSupply.sunUP();
    }
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
    
    @Test
      public void underVoltageProtectionTest()
    {
        do{
        }while(askQuestionYESNO("Is the battery disconnected?")==false);
        Assert.assertTrue("GO", true);
    }
    
    @After
    public void tearDown() throws IOException, InterruptedException
    {
        System.out.println("test complete");
        System.out.println(output);
        programResistance.turnLoadOFF();
        powerSupply.sunDown();
        programResistance.endRemoteOperation();
        Thread.sleep(200);
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
    
 
}


