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
public class PQ9PCInterface extends PCInterface
{
    private final ByteArrayOutputStream bs = new ByteArrayOutputStream();
    private boolean startFound = false;
    private int sizeFrame = -1;

    private boolean firstByteFound = false;
    private short tmpValue = 0;

    public PQ9PCInterface(InputStream in, OutputStream out) 
    {
        super(in, out);
        
        try 
        {
            init();
        } catch (IOException ex) 
        {
            if (errorHdl != null)
            {
                errorHdl.error(new PQ9Exception(ex));
            }
        }
    }
    
    private void init() throws IOException
    {
        out.write( FIRST_BYTE | COMMAND | STOP_TRANSMISSION );
        out.write( INTERFACE_PQ9 );
        out.flush();
    }

    @Override
    public PQ9 blockingread() throws IOException 
    {
        byte[] newData = new byte[1];
        int tmprx = in.read(newData);

        while (tmprx >= 0) 
        {
            if (tmprx > 0)
            {
                byte rx = (byte) (newData[0] & 0xFF);

                // were we waiting for the first byte?
                // did we receive the first byte?
                if (!firstByteFound && ((rx & FIRST_BYTE) != 0))
                {
                    tmpValue = (short)((((int)rx) << 8) & 0xFFFF);
                    firstByteFound = true;
                }
                else if (firstByteFound && ((rx & FIRST_BYTE) == 0))
                {
                    tmpValue |= (short)rx & 0xFF;
                    firstByteFound = false;
                    
                    // process the received short
                    return processShort(tmpValue);
                } 
            }
            // read new byte
            tmprx = in.read(newData);
        }

        // no full frame received yet
        return null;
    }

    private PQ9 processShort(short rx) throws IOException
    {
        int value = rx & 0xFFFF;
        byte b = 0;

        if (value == 0x9000)
        {
            // initialization request
            init();
            return null;
        }
        
        if ((value & (ADDRESS_BIT << 8)) != 0) 
        {
            // first byte
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
            sizeFrame = -1;
            startFound = true;
        }
        
        if (startFound)
        {
            b = (byte)((((value & 0x100) >> 1) | (value & 0x7F)) & 0xFF);
            bs.write(b);

            // check if we received all the bytes and can check the checksum
            if ((sizeFrame != -1) && (sizeFrame == bs.size() - 5))
            {
                try 
                {
                    PQ9 t = new PQ9(bs.toByteArray());   
                    // clean the buffer if a valid frame was found
                    bs.reset();
                    startFound = false;
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

            // initialize the counter to catch the frame size byte and use it to 
            // detect frame termination
            if (bs.size() == 2)
            {
                sizeFrame = b & 0xFF;
            }
        }
        else
        {
            //a byte received without having received a start
            if (errorHdl != null)
            {
                errorHdl.error(new PQ9Exception(String.format("Unexpected byte: %02X", b & 0xFF)));
            }
        }

        // prevent the buffer from growing too much
        if (bs.size() > 256)
        {
            bs.reset();
            startFound = false;
            sizeFrame = -1;
            if (errorHdl != null)
            {
                errorHdl.error(new PQ9Exception("Buffer overrun"));
            }
        }
        return null;
    }
    
    @Override
    public synchronized void send(PQ9 frame) throws IOException 
    {
        byte[] data = frame.getFrame();

        out.write( FIRST_BYTE | ADDRESS_BIT | (data[0] >> 7) & 0x01 );
        out.write( data[0] & 0x7F );

        for(int i = 1; i < data.length - 1; i++)            
        {
            out.write( FIRST_BYTE | (data[i] >> 7) & 0x01 );
            out.write( data[i] & 0x7F );
        }

        out.write( FIRST_BYTE | STOP_TRANSMISSION | (data[data.length - 1] >> 7) & 0x01 );
        out.write( data[data.length - 1] & 0x7F );
        out.flush();
    }
}
