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
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.delfispace.protocols.hldlc.HLDLC;
import org.delfispace.protocols.hldlc.HLDLCReceiver;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Main 
{
    public static void main(String[] args) throws UnsupportedEncodingException
    {
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
        HLDLC p = new HLDLC(comPort.getInputStream(), comPort.getOutputStream());
        
        // setup an asynchronous callback on frame reception
        p.setReceiverCallback(new HLDLCReceiver() 
        {
            @Override
            public void received(byte[] data) 
            {
                try
                {
                    // print reception time
                    Calendar now = Calendar.getInstance();
                    out.write(df.format(now));

                    // print HEX data
                    for (int i = 0; i < data.length; i++)
                    {
                        out.write(String.format("%02X ", data[i]));
                    }
                    out.write("\n");

                    // print reception time again
                    out.write(df.format(now));
                    try {
                        PQ9 msg = new PQ9(data);
                        out.write("\n");
                        out.write(msg + "\n");
                    } catch (PQ9Exception ex) {
                        out.write(ex.getMessage());
                    }
                    out.write("\n");
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        
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
    }
}
