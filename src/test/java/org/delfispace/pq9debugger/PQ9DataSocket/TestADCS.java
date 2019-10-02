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
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class TestADCS
{
    private final static int TIMEOUT = 300; // in ms
    private final static StatisticsGenerator[] STATS = new StatisticsGenerator[10];
    
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        STATS[0] = new StatisticsGenerator();
        STATS[1] = new StatisticsGenerator();
        STATS[2] = new StatisticsGenerator();
        STATS[3] = new StatisticsGenerator();
        STATS[4] = new StatisticsGenerator();
        STATS[5] = new StatisticsGenerator();
        STATS[6] = new StatisticsGenerator();
        STATS[7] = new StatisticsGenerator();
        STATS[8] = new StatisticsGenerator();
        STATS[9] = new StatisticsGenerator();
        
        try (PQ9DataClient client = new PQ9DataClient("localhost", 10000)) 
        {
            client.setTimeout(TIMEOUT);
            
            JSONObject command = new JSONObject();
            command.put("_send_", "GetTelemetry");
            command.put("Destination", "ADCS");
            
            int transmitted = 8000;
            
            for (int h = 0; h < transmitted; h++) 
            {
                if ((h % 1000) == 0)
                {
                    System.out.println(h);
                }
                Date before = new Date();
                client.sendFrame(command);

                try 
                {
                    JSONObject reply = client.getFrame();
                    
                    Date after = new Date();
                    long delta = after.getTime() - before.getTime();
                    STATS[0].addPoint(delta);

                    if (reply.get("TemperatureStatus").toString().equals("Active"))
                    {
                        STATS[1].addPoint(Double.parseDouble(reply.get("Temperature").toString()));
                    }
                    
                    // main bus is working
                    if (reply.get("BusStatus").toString().equals("Active"))
                    {
                        STATS[2].addPoint(Double.parseDouble(reply.get("BusVoltage").toString()));
                        STATS[3].addPoint(Double.parseDouble(reply.get("BusCurrent").toString()));
                    }                    

                    // Torquer X is working...
                    if (reply.get("TorquerXStatus").toString().equals("Active"))
                    {
                        STATS[4].addPoint(Double.parseDouble(reply.get("TorquerXVoltage").toString()));
                        STATS[5].addPoint(Double.parseDouble(reply.get("TorquerXCurrent").toString()));
                    }

                    // Torquer Y is working...
                    if (reply.get("TorquerYStatus").toString().equals("Active"))
                    {
                        STATS[6].addPoint(Double.parseDouble(reply.get("TorquerYVoltage").toString()));
                        STATS[7].addPoint(Double.parseDouble(reply.get("TorquerYCurrent").toString()));
                    }
                    
                    // Torquer Z is working...
                    if (reply.get("TorquerZStatus").toString().equals("Active"))
                    {
                        STATS[8].addPoint(Double.parseDouble(reply.get("TorquerZVoltage").toString()));
                        STATS[9].addPoint(Double.parseDouble(reply.get("TorquerZCurrent").toString()));
                    }    
                } catch (TimeoutException ex) 
                {
                    //  nothing to do here
                }             
            }           
            
            System.out.println();
            System.out.println("Transmitted: " + transmitted);
            STATS[0].printStatistics();
            
            System.out.println();
            System.out.println("Temperature:");
            STATS[1].printStatistics();
            
            System.out.println();
            System.out.println("BusVoltage:");
            STATS[2].printStatistics();
            
            System.out.println();
            System.out.println("BusCurrent:");
            STATS[3].printStatistics();
            
            System.out.println();
            System.out.println("TorquerXVoltage:");
            STATS[4].printStatistics();            
            
            System.out.println();
            System.out.println("TorquerXCurrent:");
            STATS[5].printStatistics();
            
            System.out.println();
            System.out.println("TorquerYVoltage:");
            STATS[6].printStatistics();            
            
            System.out.println();
            System.out.println("TorquerXYCurrent:");
            STATS[7].printStatistics();
            
            System.out.println();
            System.out.println("TorquerZVoltage:");
            STATS[8].printStatistics();            
            
            System.out.println();
            System.out.println("TorquerZCurrent:");
            STATS[9].printStatistics();
        }
    }
}
