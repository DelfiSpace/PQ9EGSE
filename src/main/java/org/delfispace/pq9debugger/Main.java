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
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.delfispace.protocols.pq9.PQ9PCInterface;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Main 
{
    public static void main(String[] args) throws UnsupportedEncodingException, IOException, PQ9Exception
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
                // print reception time
                out.write(df.format(new Date()));
                // print the received frame
                out.write("New frame received: \n");
                out.write(msg + "\n");
                    
                // reply to the message in case it is correct
                //PQ9 reply = msg.reply(msg.getData());
                //PQ9 reply = msg.reply(new byte[]{(byte)0x7E, (byte)0x7E, (byte)0x7D, (byte)0x7D, (byte)0x7C, (byte)0x7C});
                //p.send(reply);
                //Thread.sleep(1000);
                //PQ9 reply2 = msg.reply(new byte[25]);
                //p.send(reply2);
            } catch (IOException ex)
            {
                ex.printStackTrace();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
        
        // flush the console buffer once per second
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try 
            {
                out.flush();
            } catch (IOException ex)
            {
                // nothing to do
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // get telemetry
        /*final ScheduledExecutorService telemetry = Executors.newSingleThreadScheduledExecutor();
        telemetry.scheduleAtFixedRate(() -> {
            try 
            {
                PQ9 frame = new PQ9(7, 1, new byte[]{2, 1});
                p.send(frame);
                // print reception time
                out.write(df.format(new Date()));
                // print the received frame
                out.write(" New frame sent: \n");
                out.write(frame + "\n");
            } catch (IOException ex)
            {
                // nothing to do
            } catch (PQ9Exception ex) 
            {
                try 
                {
                    out.write("Error sending frame: " + ex.getMessage());
                } catch (IOException ex1) 
                {
                    // nothing to do here
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
        
        // get telemetry        
        final ScheduledExecutorService ping = Executors.newSingleThreadScheduledExecutor();
        ping.scheduleAtFixedRate(() -> {
            try 
            {
                PQ9 frame = new PQ9(7, 1, new byte[]{17, 1});
                p.send(frame);
                // print reception time
                out.write(df.format(new Date()));
                // print the received frame
                out.write(" New frame sent: \n");
                out.write(frame + "\n");
            } catch (IOException ex)
            {
                // nothing to do
            } catch (PQ9Exception ex) 
            {
                try 
                {
                    out.write("Error sending frame: " + ex.getMessage());
                } catch (IOException ex1) 
                {
                    // nothing to do here
                }
            }
        }, 0, 7, TimeUnit.SECONDS);
        
        // get telemetry
        final ScheduledExecutorService command = Executors.newSingleThreadScheduledExecutor();
        command.scheduleAtFixedRate(() -> {
            try 
            {
                int status = 1;
                PQ9 frame = new PQ9(7, 1, new byte[]{1, 1, 4, (byte)status});
                p.send(frame);
                // print reception time
                out.write(df.format(new Date()));
                // print the received frame
                out.write(" New frame sent: \n");
                out.write(frame + "\n");
                status++;
                status %= 2;
            } catch (IOException ex)
            {
                // nothing to do
            } catch (PQ9Exception ex) 
            {
                try 
                {
                    out.write("Error sending frame: " + ex.getMessage());
                } catch (IOException ex1) 
                {
                    // nothing to do here
                }
            }
        }, 0, 8, TimeUnit.SECONDS);*/
        
        while(true)
        {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String s = bufferRead.readLine();
            
            switch(s)
            {
                case "1":
                    System.out.println("Ping");
                    try 
                    {
                        PQ9 frame = new PQ9(7, 1, new byte[]{17, 1});
                        p.send(frame);
                        // print reception time
                        out.write(df.format(new Date()));
                        // print the received frame
                        out.write("New frame sent: \n");
                        out.write("\t" + frame.toString().replace("\n", "\n\t") + "\n");
                    } catch (IOException ex)
                    {
                        // nothing to do
                    } catch (PQ9Exception ex) 
                    {
                        try 
                        {
                            out.write("Error sending frame: " + ex.getMessage());
                        } catch (IOException ex1) 
                        {
                            // nothing to do here
                        }
                    }
                    break;
                    
                case "2":
                    System.out.println("TLM");
                    try 
                    {
                        PQ9 frame = new PQ9(7, 1, new byte[]{3, 1});
                        p.send(frame);
                        // print reception time
                        out.write(df.format(new Date()));
                        // print the received frame
                        out.write("New frame sent: \n");
                        out.write("\t" + frame.toString().replace("\n", "\n\t") + "\n");
                    } catch (IOException ex)
                    {
                        // nothing to do
                    } catch (PQ9Exception ex) 
                    {
                        try 
                        {
                            out.write("Error sending frame: " + ex.getMessage());
                        } catch (IOException ex1) 
                        {
                            // nothing to do here
                        }
                    }
                    break;
                
                case "3":
                    System.out.println("CMD");
                    try 
                    {
                        
                        PQ9 frame = new PQ9(7, 1, new byte[]{1, 1, 4, (byte)status});
                        p.send(frame);
                        // print reception time
                        out.write(df.format(new Date()));
                        // print the received frame
                        out.write("New frame sent: \n");
                        out.write("\t" + frame.toString().replace("\n", "\n\t") + "\n");
                        status++;
                        status %= 2;
                    } catch (IOException ex)
                    {
                        // nothing to do
                    } catch (PQ9Exception ex) 
                    {
                        try 
                        {
                            out.write("Error sending frame: " + ex.getMessage());
                        } catch (IOException ex1) 
                        {
                            // nothing to do here
                            ex.printStackTrace();
                        }
                    }
                    break;
                    
            }
            
        }
    }
}
