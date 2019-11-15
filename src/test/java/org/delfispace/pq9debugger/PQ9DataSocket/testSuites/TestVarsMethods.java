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
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.BusTestCase1.caseClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author LocalAdmin
 */
public class TestVarsMethods {
    
    // TEST VARIABLES
    protected final static int TIMEOUT = 500; // in ms
    static PQ9DataClient caseClient;
    
    protected final String busIsOn = "{\"valid\":\"true\",\"value\":\"ON\"}";
    protected final String busIsOff = "{\"valid\":\"true\",\"value\":\"OFF\"}";
    protected final String servicePB = "{\"valid\":\"true\",\"value\":\"Execute\"}";
    protected final String replyPB = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    protected final String replyER = "{\"valid\":\"true\",\"value\":\"Error\"}";
    protected final String replySH = "{\"valid\":\"true\",\"value\":\"1\"}";
    protected static StringBuilder output;
    protected final int testtimepar = 43; //wait time in miliseconds
    protected final static ZoneId LOCAL = ZoneId.of("Europe/Berlin");
    protected final static long NANTOMIL = 1000*1000;
    protected final String PingService = "{\"valid\":\"true\",\"value\":\"Ping\"}";
    protected final String PingRequest = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    
    protected int NumEPS = 2;
    protected int NumCOMMS =4;
    
    

    protected JSONObject reply;
    protected JSONObject commandPing;
    protected JSONObject commandSetBus;
    protected JSONObject commandReset;
    protected JSONObject commandGetTelemetry;
    protected JSONObject commandRaw;

    protected final String EPSSource  = "{\"valid\":\"true\",\"value\":\"EPS\"}";  
    protected final String COMMSSource  = "{\"valid\":\"true\",\"value\":\"COMMS\"}";
    
    protected final String ResetReply  = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    protected final String ResetReplyDest = "{\"valid\":\"true\",\"value\":\"OBC\"}";
    protected final String ResetService = "{\"valid\":\"true\",\"value\":\"Reset\"}";
    protected final String ResetReplySize  = "{\"valid\":\"true\",\"value\":3\"}";
    protected final String ResetSoft  ="{\"valid\":\"true\",\"value\":\"Soft\"}";
    protected final String ResetHard  ="{\"valid\":\"true\",\"value\":\"Hard\"}";
    protected final String ResetPC  ="{\"valid\":\"true\",\"value\":\"PowerCycle\"}";
    
    public static void TestVarsMethods(){
        
        
    } 
    protected JSONObject getTelemetry(String subSystem) throws IOException, ParseException, TimeoutException{
        
        JSONObject replyInt = new JSONObject();
        switch(subSystem)
            {
                case "EPS":
                      commandGetTelemetry.put("Destination", "EPS");
                      commandGetTelemetry.put("_send_", "GetTelemetry");
                      caseClient.sendFrame(commandGetTelemetry); //request housekeeping data
                      replyInt = caseClient.getFrame(); //receive housekeeping data
                    break;
                case "COMMS":
                    //code block
                    break;
                case "ADCS":
                    //code block
                    break;
                case "ADB":
                    //code block
                    break;    
                default: 
                    replyInt.put("_recieved_", "Error Unknown Subsystem");
            }
        return replyInt;           
    }
    
    protected JSONObject pingSubSystem(String subSystem){
        JSONObject replyInternal = new JSONObject();
        switch(subSystem)
            {
                case "EPS":
                    //code block
                    break;
                case "COMMS":
                    //code block
                    break;
                case "ADCS":
                    //code block
                    break;
                case "ADB":
                    //code block
                    break;    
                default: 
                    reply.put("_recieved_", "Error Unknown Subsystem");
            }    
        return replyInternal;   
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
}
