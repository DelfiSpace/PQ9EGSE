/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.IOException;
import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class TestRX 
{
    private final static int TIMEOUT = 300; // in ms
    private final static int TO_BE_RECEIVED = 100;
    
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        try (PQ9DataClient client = new PQ9DataClient("localhost", 10000)) 
        {
            client.setTimeout(TIMEOUT);
            
            // the first frame has a timeout of 30s to ease testing
            JSONObject reply = client.getFrame(30000);
            Date before = new Date();
            
            if (reply != null)
            {
                double max = 0;
                double min = 1e12;
                double avg = 0;
                double std = 0;

                int received = 1;

                for (int h = 1; h < TO_BE_RECEIVED; h++) 
                {
                    reply = client.getFrame();
                    Date after = new Date();
                        
                    if (reply != null)
                    {                    
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
                    }  
                    before = after;
                }

                avg /= received;
                std = Math.sqrt((std / received) - avg*avg);

                System.out.println();
                System.out.println("Transmitted: " + TO_BE_RECEIVED);
                System.out.println("Received: " + received);
                System.out.println("Max: " + max);
                System.out.println("Min: " + min);
                System.out.println("Avg: " + avg);
                System.out.println("Std: " + std);
            }
        }
    }
}
