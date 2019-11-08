/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import java.util.Date;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.xtce.toolkit.XTCEDatabaseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 *
 * @author LocalAdmin
 */
public class TestConcepts {
    private final static int TIMEOUT = 300; // in ms
    private final static ZoneId LOCALZ = ZoneId.of("Europe/Berlin");
    private final static long NANTOMIL = 1000*1000;
    private final static String TIMESTAMPEX1 = "2019-11-08 17:55:34.716";
    private final static String TIMESTAMPEX2 = "2019-11-08 17:55:34.326";
    private final static String TIMESTAMPEX3 = "2019-11-08 17:55:34.246";
    private final static String TIMESTAMPEX4 = "2019-11-08 17:55:34.166"; 
    private final static String TIMESTAMPEX5 = "2019-11-08 17:55:34.076";
    private final static String TIMESTAMPEX6 = "2019-11-08 17:55:36.076";
       
       
    JSONObject reply;
    JSONObject commandP;
    static long referencetime;
    static PQ9DataClient client;
    public static void main(String[]args)throws IOException, ParseException, TimeoutException, PQ9Exception, XTCEDatabaseException 
    {  
        referencetime = System.nanoTime();     
        client = new PQ9DataClient("localhost", 10000);
        client.setTimeout(TIMEOUT);
            
        JSONObject command = new JSONObject();
        command.put("_send_", "GetTelemetry");
        command.put("Destination", "EPS");
            
        int transmitted = 5;
            
        for (int h = 0; h < transmitted; h++) 
        {
            if ((h % 100) == 0)
            {
                System.out.println(h);
            }
            
            Instant before = Instant.now();
            long pt = System.nanoTime();
            client.sendFrame(command);
            try 
            {
                JSONObject reply = client.getFrame();
                long et = System.nanoTime()-pt;
                Instant after = before.plusNanos(et);
                System.out.print("Before: ");
                System.out.println(LocalDateTime.ofInstant(before, LOCALZ));
                System.out.println("et: "+et);
                long elapsed = et/NANTOMIL;
                System.out.println("Elapsed: "+ elapsed);
                System.out.print("After: " );
                LocalTime afterLocal =  LocalTime.from(after.atZone(LOCALZ));
                String timestamp = (String)reply.get("_timestamp_");
                String[] arrOfStr = timestamp.split(":");
                System.out.println(LocalDateTime.ofInstant(after, LOCALZ));
                //String formatted_date = after.getHours() + ":" + after.getMinutes() + ":" + after.getSeconds() + "." + after.getTime()%1000;
                    //System.out.println("After = "+formatted_date);
                    //System.out.println(System.nanoTime());
                    System.out.println("Timestamp: "+reply.get("_timestamp_"));
                    // System.out.println(System.nanoTime());
                }catch (TimeoutException ex) 
                {
                    // nothing to do here
                    
                }               
            }
    }  	
}


