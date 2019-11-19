/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import java.time.ZoneId;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.EPSBusHandlingTest.caseClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class TestVarsMethods 
{
    protected final String subSystem;
    
    // TEST VARIABLES
    protected final int WAITREFRESH = 1111; // in ms
    protected final static int TIMEOUT = 500; // in ms
    protected static StringBuilder output;
    protected final int testtimepar = 43; //wait time in miliseconds
    protected final static ZoneId LOCAL = ZoneId.of("Europe/Berlin");
    protected final static long NANTOMIL = 1000*1000;
    
    protected final String busIsOn = "{\"valid\":\"true\",\"value\":\"ON\"}";
    protected final String busIsOff = "{\"valid\":\"true\",\"value\":\"OFF\"}";
    protected final String servicePB = "{\"valid\":\"true\",\"value\":\"Execute\"}";
    protected final String replyPB = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    protected final String replyER = "{\"valid\":\"true\",\"value\":\"Error\"}";
    protected final String replySH = "{\"valid\":\"true\",\"value\":\"1\"}";
    protected final String PingService = "{\"valid\":\"true\",\"value\":\"Ping\"}";
    protected final String PingRequest = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    protected final String EPSSource  = "{\"valid\":\"true\",\"value\":\"EPS\"}";  
    protected final String COMMSSource  = "{\"valid\":\"true\",\"value\":\"COMMS\"}";
    protected final String ResetReply  = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    protected final String ResetReplyDest = "{\"valid\":\"true\",\"value\":\"OBC\"}";
    protected final String ResetService = "{\"valid\":\"true\",\"value\":\"Reset\"}";
    protected final String ResetReplySize  = "{\"valid\":\"true\",\"value\":\"3\"}";
    protected final String ResetSoft  ="{\"valid\":\"true\",\"value\":\"Soft\"}";
    protected final String ResetHard  ="{\"valid\":\"true\",\"value\":\"Hard\"}";
    protected final String ResetPC  ="{\"valid\":\"true\",\"value\":\"PowerCycle\"}";
    
    
    //predefined PQ9Client
    static PQ9DataClient caseClient;
    
    //Subsystem locations
    protected int NumEPS = 2;
    protected int NumCOMMS = 4;
    
    //List of subSystems
    protected String[] subSystems = {"EPS", "COMMS", "ADCS", "ABD"};
    
    //predefined JSON Objects
    protected JSONObject reply;
    protected JSONObject commandPing;
    protected JSONObject commandSetBus;
    protected JSONObject commandReset;
    protected JSONObject commandGetTelemetry;
    protected JSONObject commandRaw;

    
    
    public TestVarsMethods()
    {
        subSystem = "EPS";
               // subSystem = TestParameters.getDestination();
    }
    

    protected JSONObject getTelemetry(String subSystem) throws IOException, ParseException, TimeoutException{
        
        JSONObject replyInt = new JSONObject();
        if(isKnown(subSystem, subSystems))
        {
            commandGetTelemetry.put("Destination", subSystem);
        }
        else
        {
            replyInt.put("_recieved_", "Error Unknown Subsystem");
            return replyInt; // return here, there is something wrong thererfore test will fail. 
        }
        //commandGetTelemetry should be initialized by the @Before method of the test
        caseClient.sendFrame(commandGetTelemetry); //request housekeeping data
        replyInt = caseClient.getFrame(); //receive housekeeping data
        return replyInt;           
    }
    
    protected JSONObject pingSubSystem(String subSystem) throws IOException, ParseException, TimeoutException{
        JSONObject replyInt = new JSONObject();
         if(isKnown(subSystem, subSystems))
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
    
    protected JSONObject resetSubSystem(String subSystem, String type)throws IOException, ParseException, TimeoutException{
        JSONObject replyInt = new JSONObject();
        if("Soft".equals(type) || "Hard".equals(type) || "PowerCycle".equals(type))
        {
            commandReset.put("Type", type);
        }
        else
        {
            replyInt.put("_received_", "Error Unknown Reset Type");
            return replyInt; // return here, there is something wrong thererfore test will fail.
        }
        if(isKnown(subSystem, subSystems))
        {
            commandReset.put("Destination", subSystem);
        }
        else
        {
            replyInt.put("_received_", "Error Unknown Subsystem");
            return replyInt; // return here, there is something wrong thererfore test will fail.
        }
        //commandReset should be initialized by the @Before method of the test
        caseClient.sendFrame(commandReset);  
        replyInt = caseClient.getFrame();
        return replyInt;   
    } 
    
    
    
    protected int timeStampGetMillis(String timestamp){
        String[] delta = breakTimestamp(timestamp);
            int loc = delta.length;
            int millis;
            millis = Integer.valueOf(delta[loc-1]);
        return millis;
    }
    protected String[] breakTimestamp(String timestamp){
        // Rememeber this only works if the timestamp formatting is not changed!
        String[] internalStringArray;
        String[] temp = timestamp.split(" ");// splits off the date
        String[] temp2 = temp[1].split(":");// splits up the time
        String[] temp3 = temp2[temp2.length-1].split("\\.");
        int size1 = temp2.length +2;
        internalStringArray = new String[size1];
        internalStringArray[0] = temp[0];
        int size2 = internalStringArray.length-2;
        System.arraycopy(temp2, 0, internalStringArray, 1, size1-2);
        System.arraycopy(temp3, 0, internalStringArray, size2, 2);
        return internalStringArray;
    }
    public String[] testBreakTimeStamp(String timestamp){
        String[] delta = breakTimestamp(timestamp);
        return delta;
    }
     public int testgetMillis(String timestamp){
        int delta = timeStampGetMillis(timestamp);
        return delta;
    }
    protected boolean isKnown(String x, String[] ax){
        for(String s: ax){
            if(x.equals(s))
                return true;
        }
        return false;
    } 
     public boolean testisKnown(String str, String[] Arr){
        boolean delta = isKnown(str,Arr); 
        return delta;
    }
     protected JSONObject stringToJSON(String tobreak) throws ParseException{
        
        JSONParser parser = new JSONParser();
        JSONObject tempJSON = (JSONObject) parser.parse(tobreak);
        return tempJSON;
     }
     protected int getUptime(String uptime) throws ParseException
     {
             JSONObject tempJSON = stringToJSON(uptime);
             int value = Integer.valueOf(tempJSON.get("value").toString());
             return value;
     }        
}
