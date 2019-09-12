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
public class TestEPS
{
    private final static int TIMEOUT = 300; // in ms
    private final static StatisticsGenerator[] stats = new StatisticsGenerator[22];
    
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        stats[0] = new StatisticsGenerator();
        stats[1] = new StatisticsGenerator();
        stats[2] = new StatisticsGenerator();
        stats[3] = new StatisticsGenerator();
        stats[4] = new StatisticsGenerator();
        stats[5] = new StatisticsGenerator();
        stats[6] = new StatisticsGenerator();
        stats[7] = new StatisticsGenerator();
        stats[8] = new StatisticsGenerator();
        stats[9] = new StatisticsGenerator();
        stats[10] = new StatisticsGenerator();
        stats[11] = new StatisticsGenerator();
        stats[12] = new StatisticsGenerator();
        stats[13] = new StatisticsGenerator();
        stats[14] = new StatisticsGenerator();
        stats[15] = new StatisticsGenerator();
        stats[16] = new StatisticsGenerator();
        stats[17] = new StatisticsGenerator();
        stats[18] = new StatisticsGenerator();
        stats[19] = new StatisticsGenerator();
        stats[20] = new StatisticsGenerator();
        stats[21] = new StatisticsGenerator();

        
        try (PQ9DataClient client = new PQ9DataClient("localhost", 10000)) 
        {
            client.setTimeout(TIMEOUT);
            
            JSONObject command = new JSONObject();
            command.put("_send_", "GetTelemetry");
            command.put("Destination", "EPS");
            
            int transmitted = 80;
            
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
                    stats[0].addPoint(delta);
                    
                    if (reply.get("EPS_DC_INA_Status").toString().equals("Working"))
                    {
                        stats[1].addPoint(Double.parseDouble(reply.get("IntVoltage").toString()));
                        stats[2].addPoint(Double.parseDouble(reply.get("IntCurrent").toString()));
                    }
                    
                    if (reply.get("EPS_UR_INA_Status").toString().equals("Working"))
                    {
                        stats[3].addPoint(Double.parseDouble(reply.get("URBVoltage").toString()));
                        stats[4].addPoint(Double.parseDouble(reply.get("URBCurrent").toString()));
                    }                    

                    // bus 1 is working...
                    if (reply.get("EPS_B1_INA_Status").toString().equals("Working"))
                    {
                        stats[5].addPoint(Double.parseDouble(reply.get("B1_voltage").toString()));
                        stats[6].addPoint(Double.parseDouble(reply.get("B1_current").toString()));
                    }

                    // bus 2 is working...
                    if (reply.get("EPS_B2_INA_Status").toString().equals("Working"))
                    {
                        stats[7].addPoint(Double.parseDouble(reply.get("B2_voltage").toString()));
                        stats[8].addPoint(Double.parseDouble(reply.get("B2_current").toString()));
                    }
                    
                    // bus 3 is working...
                    if (reply.get("EPS_B3_INA_Status").toString().equals("Working"))
                    {
                        stats[9].addPoint(Double.parseDouble(reply.get("B3_voltage").toString()));
                        stats[10].addPoint(Double.parseDouble(reply.get("B3_current").toString()));
                    }
                    
                    // bus 4 is working...
                    if (reply.get("EPS_B4_INA_Status").toString().equals("Working"))
                    {
                        stats[11].addPoint(Double.parseDouble(reply.get("B4_voltage").toString()));
                        stats[12].addPoint(Double.parseDouble(reply.get("B4_current").toString()));
                    }
                    
                    // solar array Yp is working...
                    if (reply.get("SA_YP_INA_Status").toString().equals("Working"))
                    {
                        stats[13].addPoint(Double.parseDouble(reply.get("SA_YP_voltage").toString()));
                        stats[14].addPoint(Double.parseDouble(reply.get("SA_YP_current").toString()));
                    }
                    
                    // solar array Ym is working...
                    if (reply.get("SA_YM_INA_Status").toString().equals("Working"))
                    {
                        stats[15].addPoint(Double.parseDouble(reply.get("SA_YM_voltage").toString()));
                        stats[16].addPoint(Double.parseDouble(reply.get("SA_YM_current").toString()));
                    }
                    
                    // solar array Xp is working...
                    if (reply.get("SA_XP_INA_Status").toString().equals("Working"))
                    {
                        stats[17].addPoint(Double.parseDouble(reply.get("SA_XP_voltage").toString()));
                        stats[18].addPoint(Double.parseDouble(reply.get("SA_XP_current").toString()));
                    }
                    
                    // solar array Xm is working...
                    if (reply.get("SA_XM_INA_Status").toString().equals("Working"))
                    {
                        stats[19].addPoint(Double.parseDouble(reply.get("SA_XM_voltage").toString()));
                        stats[20].addPoint(Double.parseDouble(reply.get("SA_XM_current").toString()));
                    }
                    
                    // battery is working...
                    if (reply.get("EPS_LTC_Status").toString().equals("Working"))
                    {
                        stats[21].addPoint(Double.parseDouble(reply.get("BattVoltage").toString()));
                    }
                } catch (TimeoutException ex) 
                {
                    // nothing to do here
                }                                
            }           
            
            System.out.println();
            System.out.println("Transmitted: " + transmitted);
            stats[0].printStatistics();
            
            System.out.println();
            System.out.println("IntVoltage:");
            stats[1].printStatistics();
            
            System.out.println();
            System.out.println("IntCurrent:");
            stats[2].printStatistics();
            
            System.out.println();
            System.out.println("URBVoltage:");
            stats[3].printStatistics();
            
            System.out.println();
            System.out.println("URBCurrent:");
            stats[4].printStatistics();            
            
            System.out.println();
            System.out.println("B1Voltage:");
            stats[5].printStatistics();
            
            System.out.println();
            System.out.println("B1Current:");
            stats[6].printStatistics();
            
            System.out.println();
            System.out.println("B2Voltage:");
            stats[7].printStatistics();
            
            System.out.println();
            System.out.println("B2Current:");
            stats[8].printStatistics();
            
            System.out.println();
            System.out.println("B3Voltage:");
            stats[9].printStatistics();
            
            System.out.println();
            System.out.println("B3Current:");
            stats[10].printStatistics();
            
            System.out.println();
            System.out.println("B4Voltage:");
            stats[11].printStatistics();
            
            System.out.println();
            System.out.println("B4Current:");
            stats[12].printStatistics();
            
            System.out.println();
            System.out.println("SAYpVoltage:");
            stats[13].printStatistics();
            
            System.out.println();
            System.out.println("SAYpCurrent:");
            stats[14].printStatistics();
            
            System.out.println();
            System.out.println("SAYmVoltage:");
            stats[15].printStatistics();
            
            System.out.println();
            System.out.println("SAYmCurrent:");
            stats[16].printStatistics();
            
            System.out.println();
            System.out.println("SAXpVoltage:");
            stats[17].printStatistics();
            
            System.out.println();
            System.out.println("SAXpCurrent:");
            stats[18].printStatistics();
            
            System.out.println();
            System.out.println("SAXmVoltage:");
            stats[19].printStatistics();
            
            System.out.println();
            System.out.println("SAXmCurrent:");
            stats[20].printStatistics();
            
            System.out.println();
            System.out.println("BattVoltage:");
            stats[21].printStatistics();
        }
    }
}
