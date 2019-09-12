/*
 * Copyright (C) 2018 Stefano Speretta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public class TestRX 
{
    private final static int TIMEOUT = 300; // in ms
    private final static int TO_BE_RECEIVED = 100;
    
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        try (PQ9DataClient client = new PQ9DataClient("localhost", 10000)) 
        {
            client.setTimeout(TIMEOUT);
            
            Date before = new Date();
            
            // the first frame has a timeout of 30s to ease testing
            try 
            {
                JSONObject reply;
                
                double max = 0;
                double min = 1e12;
                double avg = 0;
                double std = 0;

                int received = 0;

                for (int h = 1; h < TO_BE_RECEIVED; h++) 
                {
                    reply = client.getFrame();
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
                
            } catch (TimeoutException ex) 
            {
                Logger.getLogger(TestRX.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
    }
}
