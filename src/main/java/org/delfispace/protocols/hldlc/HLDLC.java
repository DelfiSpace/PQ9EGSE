/*
 * Copyright (C) 2018 , Nikitas Chronas, Stefano Speretta
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
package org.delfispace.protocols.hldlc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Nikitas Chronas <N.ChronasFoteinakis@tudelft.nl>
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class HLDLC 
{
    private final InputStream in;
    private final OutputStream out;
    private HLDLCReceiver callback;
    private readerThread reader;
    private final ByteArrayOutputStream bs = new ByteArrayOutputStream();
    private boolean startFound = false;
    private boolean controlFound = false;

    static final int HLDLC_START_FLAG = 0x7E;
    static final int HLDLC_CONTROL_FLAG = 0x7D;
    static final int HLDLC_STOP_FLAG = 0x7C;
    static final int HLDLC_ESCAPE_START_FLAG = 0x5E;
    static final int HLDLC_ESCAPE_CONTROL_FLAG = 0x5D;
    static final int HLDLC_ESCAPE_STOP_FLAG = 0x5C;

    public HLDLC(InputStream in, OutputStream out) 
    {
        this.in = in;
        this.out = out;
    }

    public void setReceiverCallback(HLDLCReceiver clb) 
    {
        if (callback != null) 
        {            
            reader.running = false;
        }

        callback = clb;

        if (callback != null) 
        {
            reader = new readerThread();
            reader.start();
        }
    }

    public byte[] read() throws IOException 
    {
        if (callback == null) 
        {
            return blockingread();
        }
        return null;
    }

    private byte[] blockingread() throws IOException 
    {
        int tmprx = in.read();

        while (tmprx != -1) 
        {
            byte rx = (byte) (tmprx & 0xFF);

            if (rx == HLDLC_START_FLAG) 
            {
                // clear the buffer and get ready to process a new frame
                bs.reset();
                startFound = true;
                controlFound = false;
            } 
            else if (startFound) 
            {
                // only process data if a flag was found            
                if (rx == HLDLC_CONTROL_FLAG) 
                {
                    controlFound = true;
                } 
                else if (controlFound) 
                {
                    switch (rx) 
                    {
                        case HLDLC_ESCAPE_START_FLAG:
                            bs.write(HLDLC_START_FLAG);
                            break;
                        case HLDLC_ESCAPE_CONTROL_FLAG:
                            bs.write(HLDLC_CONTROL_FLAG);
                            break;
                        case HLDLC_ESCAPE_STOP_FLAG:
                            bs.write(HLDLC_STOP_FLAG);
                            break;
                        default:
                            // illegal sequence, aborting
                            startFound = false;
                            controlFound = false;
                            // throw exception here
                            System.out.println("exception 1");
                            break;
                    }
                    controlFound = false;
                } 
                else if (rx == HLDLC_STOP_FLAG) 
                {
                    // stop found, return the processed byte array
                    startFound = false;
                    controlFound = false;
                    return bs.toByteArray();
                } 
                else 
                {
                    // new data byte, add it to the buffer
                    bs.write(rx);
                }
            }

            // read new byte
            tmprx = in.read();
        }

        // no full frame received yet
        return null;
    }

    public void send(byte[] data) throws IOException 
    {
        out.write(HLDLC_START_FLAG);
        for (int i = 0; i < data.length; i++) 
        {
            byte tx = data[i];
            switch (tx) 
            {
                case HLDLC_START_FLAG:
                    out.write(HLDLC_CONTROL_FLAG);
                    out.write(HLDLC_ESCAPE_START_FLAG);
                    break;
                case HLDLC_CONTROL_FLAG:
                    out.write(HLDLC_CONTROL_FLAG);
                    out.write(HLDLC_ESCAPE_CONTROL_FLAG);
                    break;
                case HLDLC_STOP_FLAG:
                    out.write(HLDLC_CONTROL_FLAG);
                    out.write(HLDLC_ESCAPE_STOP_FLAG);
                    break;
                default:
                    out.write(tx);
                    break;
            }
        }
        out.write(HLDLC_STOP_FLAG);
        out.flush();
    }

    class readerThread extends Thread 
    {
        public boolean running;

        @Override
        public void run() 
        {
            running = true;
            try 
            {
                while (running) 
                {
                    byte[] d = blockingread();
                    if (d != null) 
                    {
                        callback.received(d);
                    }
                }
            } catch (IOException ex) 
            {
                running = false;
            }
        }
    }
}
