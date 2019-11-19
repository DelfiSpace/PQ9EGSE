/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.EPSBusHandlingTest.caseClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author LocalAdmin
 */
public class PingTestValidParameters 
{ 
    protected final static long NANTOMIL = 1000*1000;
    private static String destination;
    private static StringBuilder output;     
    private JSONObject commandPing;
    protected JSONObject reply;
    @BeforeClass 
    public static void BeforePingTestClass() throws IOException 
    {
        destination = TestParameters.getDestination();    
        if(destination == null){
            destination = "EPS";
        }
        System.out.println("Initializer of PingTestClass1 " + destination);
        output = new StringBuilder("");   
        caseClient = new PQ9DataClient("localhost", 10000);
        caseClient.setTimeout(TestParameters.TIMEOUT);    
    }
    
    @Before
    public void setup() 
    {
        commandPing = new JSONObject();
        commandPing.put("_send_", "Ping");
        commandPing.put("Destination", destination);
        reply = new JSONObject();
    }
    
    @Test(timeout=1000)
    public void PingServiceTest() throws IOException, ParseException, TimeoutException
    {       
       reply = pingSubSystem(destination);
       Assert.assertEquals("PingService", reply.get("_received_").toString()); 
    }
    
    @Test(timeout=1000)
    public void PingReplyTest() throws IOException, ParseException, TimeoutException
    {       
       reply = pingSubSystem(destination);
       Assert.assertEquals(TestParameters.getExpectedReply("Ping"), reply.get("Service").toString());   
    }
    @Test(timeout=1000)
    public void pingRequestTest() throws IOException, ParseException, TimeoutException
    {       
       reply = pingSubSystem(destination);
       Assert.assertEquals(TestParameters.getExpectedReply("Request"), reply.get("Request").toString()); 
    }
     @Test(timeout=1000)
    public void pingSourceTest() throws IOException, ParseException, TimeoutException
    {       
       reply = pingSubSystem(destination);
       Assert.assertEquals(TestParameters.getExpectedReply("Source"), reply.get("Source").toString()); 
    }    
    
    @Test
    public void PingManyTimes() throws IOException, ParseException, TimeoutException
    {
            int testruns = 1200;
            long[] results = new long[testruns];
            long pt;
            long et;
            long elapsed;
            for(int i = 0; i<testruns; i++){
                if (i == 100){
                System.out.println(i);
                }
                //Instant before = Instant.now();
                pt = System.nanoTime();
                caseClient.sendFrame(commandPing);  
                try{
                    reply = caseClient.getFrame();
                }catch (TimeoutException ex){
                    results[i]=-1;
                } 
                et = System.nanoTime()-pt;
                elapsed = et/NANTOMIL;
                if(results[i]==-1){}
                else{
                if("PingService".equals(reply.get("_received_").toString())) {
                    if(TestParameters.getExpectedReply("Ping").equals(reply.get("Service").toString())){
                        if(TestParameters.getExpectedReply("Request").equals(reply.get("Request").toString())){
                            if(TestParameters.getExpectedReply("Source").equals(reply.get("Source").toString())){
                            results[i]=elapsed;
                            }
                            else{
                                Assert.assertEquals("Source should have been: " + "{\"valid\":\"true\",\"value\":\"OBC\"}" + " but Source = ",reply.get("Source").toString());
                            }
                        }
                        else{
                            Assert.assertEquals("Service should have been: Ping, but Service = ",reply.get("Request").toString()); 
                        }
                    } 
                    else{
                        Assert.assertEquals("Request should have been: Reply but Request = ",reply.get("Service").toString()); 
                    }
                }
                else {
                    Assert.assertEquals("_received_ should have been: PingService but _recieved_ = ",reply.get("_received_").toString()); 
                }
                }
            }
            int timeoutrate=0;
            for(int i=0; i<testruns; i++){
                if (results[i]==-1){timeoutrate=timeoutrate++;} 
            }
            double timeoutrateR;
            timeoutrateR = ((double)timeoutrate/(double)testruns)*100;
            String eRmessage = "The failure rate is above 0.001//% ";
            eRmessage = eRmessage + String.valueOf(timeoutrateR);
            Assert.assertTrue(eRmessage,timeoutrate<1);  
            //output.append("PingManyTimes ")
    }
    
    @After
    public void tearDown() throws IOException
    {
        System.out.println(output);
    }
    
    @AfterClass
     public static void shutDown() throws IOException
    {
        caseClient.close();
    }
    
    protected JSONObject pingSubSystem(String subSystem) throws IOException, ParseException, TimeoutException{
        JSONObject replyInt = new JSONObject();
         if(TestParameters.isKnown(subSystem))
        {
            commandPing.put("Destination", subSystem);
        }
        else
        {
            replyInt.put("_received_", "Error Unknown Subsystem");
            return replyInt; // return here, there is something wrong thererfore test will fail.
        }
        //commandPing should be initialized by the @Before method of the test
        commandPing.put("_send_", "Ping");
        caseClient.sendFrame(commandPing);  
        replyInt = caseClient.getFrame();
        return replyInt;   
    }  
     
     
}
