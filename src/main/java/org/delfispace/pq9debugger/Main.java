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
package org.delfispace.pq9debugger;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.delfispace.CommandWebServer.Command;
import org.delfispace.CommandWebServer.CommandWebServer;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.delfispace.protocols.pq9.PQ9PCInterface;
import org.delfispace.protocols.pq9.PQ9Receiver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Main 
{
    private static CommandWebServer srv;
    private static final JSONParser parser = new JSONParser(); 
    
    
    public static void main(String[] args) throws UnsupportedEncodingException, IOException, PQ9Exception, Exception
    {
        int status = 1;
        
        if (args.length < 1)
        {
            System.out.println("Usage: java -jar PQ9Debugger comport");
            return;
        }
        
        // create a fast console writer to avoid slowing down the bus 
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new
        FileOutputStream(java.io.FileDescriptor.out), "ASCII"), 1024);
        
        // define the date format
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
        
        SerialPort comPort = SerialPort.getCommPort(args[0]);
        //SerialPort comPort = SerialPort.getCommPort("/dev/tty.Bluetooth-Incoming-Port");

        // open con port
        comPort.openPort();
        
        // configure the seriql port parameters
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        
        // crete the HLDLC reader
        PQ9PCInterface p = new PQ9PCInterface(comPort.getInputStream(), comPort.getOutputStream());
        
        // setup an asynchronous callback on frame reception
        p.setReceiverCallback((PQ9 msg) -> {
            try
            {
                StringBuilder sb = new StringBuilder();
                sb.append("<font color=\"black\">");
                // print reception time
                sb.append(df.format(new Date()));
                // print the received frame
                sb.append("New frame received: <br>");
                sb.append("&emsp;&emsp;&emsp;&emsp;");
                sb.append(msg.toString().replace("\n", "<br>").replace("\t", "&emsp;&emsp;&emsp;&emsp;"));
                sb.append("</font>");
                srv.send(new Command("datalog", sb.toString()));
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
        
        srv = new CommandWebServer(8080);
        srv.serReceptionHandler((Command cmd) -> {
            String data = cmd.getData();
            JSONObject obj;
            try {
                obj = (JSONObject)parser.parse(data);
                int d = Integer.parseInt((String) obj.get("dest"));
                int s = Integer.parseInt((String) obj.get("src"));
                
                String[] parts = ((String) obj.get("data")).split(" ");
                byte[] n1 = new byte[parts.length];
                for(int n = 0; n < parts.length; n++) 
                {
                   n1[n] = (byte)Integer.parseInt(parts[n]);
                }
                PQ9 frame = new PQ9(d, s, n1);
                p.send(frame);
                StringBuilder sb = new StringBuilder();
                sb.append("<font color=\"yellow\">");
                // print reception time
                sb.append(df.format(new Date()));
                // print the received frame
                sb.append("New frame transmitted: <br>");
                sb.append("&emsp;&emsp;&emsp;&emsp;");
                sb.append(frame.toString().replace("\n", "<br>").replace("\t", "&emsp;&emsp;&emsp;&emsp;")); 
                sb.append("</font>");
                srv.send(new Command("datalog", sb.toString()));                
            } catch (ParseException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PQ9Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }                        
        });
        
        srv.start();
        srv.join();                            
    }
}
