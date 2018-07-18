/*
 * Copyright (C) 2018, Nikitas Chronas, Stefano Speretta
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
package org.delfispace.protocols.pq9;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Nikitas Chronas <N.ChronasFoteinakis@tudelft.nl>
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class PQ9PCInterface 
{
    private final InputStream in;
    private final OutputStream out;
    private final boolean loopback;
    private PQ9Receiver callback;
    private PQ9ErrorHandler errorHdl;
    private readerThread reader;
    private final ByteArrayOutputStream bs = new ByteArrayOutputStream();
    private boolean startFound = false;
    private boolean controlFound = false;
    private int sizeFrame = -1;

    static final int HLDLC_START_FLAG = 0x7E;
    static final int HLDLC_CONTROL_FLAG = 0x7D;
    static final int HLDLC_STOP_FLAG = 0x7C;
    static final int HLDLC_ESCAPE_START_FLAG = 0x5E;
    static final int HLDLC_ESCAPE_CONTROL_FLAG = 0x5D;
    static final int HLDLC_ESCAPE_STOP_FLAG = 0x5C;

    public PQ9PCInterface(InputStream in, OutputStream out) 
    {
        this.in = in;
        this.out = out;
        this.loopback = false;
    }

    public PQ9PCInterface(InputStream in, OutputStream out, boolean loopback) 
    {
        this.in = in;
        this.out = out;
        this.loopback = loopback;
    }
    
    public void setReceiverCallback(PQ9Receiver clb) 
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

    public void setErrorHandler(PQ9ErrorHandler hdl) 
    {
        errorHdl = hdl;
    }

    public PQ9 read() throws IOException 
    {
        if (callback == null) 
        {
            return blockingread();
        }
        return null;
    }

    private PQ9 blockingread() throws IOException 
    {
        int tmprx = in.read();

        while (tmprx != -1) 
        {
            byte rx = (byte) (tmprx & 0xFF);
            if (rx == HLDLC_START_FLAG) 
            {
                // clear the buffer and get ready to process a new frame
                if (bs.size() != 0)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bytes have been discarded: ");
                    byte[] d = bs.toByteArray();
                    for (int i = 0; i < d.length; i++)
                    {
                        sb.append(String.format("%02X ", d[i]));
                    }
                    if (errorHdl != null)
                    {
                        errorHdl.error(new PQ9Exception(sb.toString()));
                    }
                }
                bs.reset();
                startFound = true;
                controlFound = false;
                sizeFrame = -1;
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
                            if (errorHdl != null)
                            {
                                errorHdl.error(new PQ9Exception("exception 1"));
                            }
                            break;
                    }
                    controlFound = false;
                } 
                else 
                {
                    // new data byte, add it to the buffer
                    bs.write(rx);
                    if ((sizeFrame != -1) && (sizeFrame == bs.size() - 5))
                    {
                        startFound = false;
                        controlFound = false;
                        sizeFrame = -1;
                        try 
                        {
                            PQ9 t = new PQ9(bs.toByteArray());   
                            // clean the buffer if a valid frame was found
                            bs.reset();
                            return t;
                        } catch (PQ9Exception ex) 
                        {
                            // the frame is not valid, throw away the data and wait for a new frame
                            if (errorHdl != null)
                            {
                                errorHdl.error(ex);
                            }
                            return null;
                        }                        
                    }
                }
                
                // initialize the counter to catch the frame size byte and use it to 
                // detect frame termination
                if (bs.size() == 2)
                {
                    
                    sizeFrame = rx & 0xFF;
                }
            }
            else
            {
                //a byte received without having received a start
                if (errorHdl != null)
                {
                    errorHdl.error(new PQ9Exception(String.format("Unexpected byte: %02X", tmprx & 0xFF)));
                }
            }
        
            // prevent the buffer from growing too much
            if (bs.size() > 256)
            {
                bs.reset();
                startFound = false;
                controlFound = false;
                sizeFrame = -1;
                if (errorHdl != null)
                {
                    errorHdl.error(new PQ9Exception("Buffer overrun"));
                }
            }
            // read new byte
            tmprx = in.read();
        }

        // no full frame received yet
        return null;
    }

    public synchronized void send(PQ9 frame) throws IOException 
    {
        out.write(HLDLC_START_FLAG);
        byte[] data = frame.getFrame();
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
        if (!this.loopback)
        {
            out.write(HLDLC_STOP_FLAG);
        }
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
                    PQ9 d = blockingread();
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
