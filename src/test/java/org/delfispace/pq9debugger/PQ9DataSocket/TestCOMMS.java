/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class TestCOMMS
{
    private final static int TIMEOUT = 300; // in ms
                
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        try (PQ9DataClient client = new PQ9DataClient("localhost", 10000)) 
        {
            client.setTimeout(TIMEOUT);
            
            JSONObject command = new JSONObject();
            command.put("_send_", "GetTelemetry");
            command.put("Destination", "EPS");
            
            double max = 0;
            double min = 1e12;
            double avg = 0;
            double std = 0;
            int transmitted = 10000;
            int received = 0;

            for (int h = 0; h < transmitted; h++) 
            {
                if ((h % 100) == 0)
                {
                    System.out.println(h);
                }
                Date before = new Date();
                client.sendFrame(command);

                try 
                {
                    JSONObject reply = client.getFrame();
                    
                    Date after = new Date();
                    received++;
                    long delta = after.getTime() - before.getTime();
                    if (delta > max)
                    {
                        max = delta;
                    }
                    if (delta < min)
                    {
                        min = delta;
                    }
                    avg += delta;
                    std += delta*delta;
                    
                } catch (TimeoutException ex) 
                {
                    //  nothing to do here
                }              
            }
            
            avg /= received;
            std = Math.sqrt((std / received) - avg*avg);
            
            System.out.println();
            System.out.println("Transmitted: " + transmitted);
            System.out.println("Received: " + received);
            System.out.println("Max: " + max);
            System.out.println("Min: " + min);
            System.out.println("Avg: " + avg);
            System.out.println("Std: " + std);
        }
    }
}
