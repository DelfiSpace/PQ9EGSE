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
import static org.delfispace.protocols.pq9.PQ9PCInterface.COMMAND;

/**
 *
 * @author Nikitas Chronas <N.ChronasFoteinakis@tudelft.nl>
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class RS485PCInterface extends PCInterface
{
    private final ByteArrayOutputStream bs = new ByteArrayOutputStream();
    private boolean startFound = false;
    private int sizeFrame = -1;

    private boolean firstByteFound = false;
    private short tmpValue = 0;
    
    static final int HLDLC_START_FLAG = 0x7E;
    static final int HLDLC_CONTROL_FLAG = 0x7D;
    static final int HLDLC_STOP_FLAG = 0x7C;
    static final int HLDLC_ESCAPE_START_FLAG = 0x5E;
    static final int HLDLC_ESCAPE_CONTROL_FLAG = 0x5D;
    static final int HLDLC_ESCAPE_STOP_FLAG = 0x5C;
    
    static final byte START_OF_MESSAGE = (byte)0x7E;
    static final byte END_OF_MESSAGE = (byte)0x7D;
    
    private int state = 0;
    private int size = 0;
    private int index = 0;
    
    public RS485PCInterface(InputStream in, OutputStream out) 
    {
        super(in, out);   
        
        try 
        {
            out.write( FIRST_BYTE | COMMAND );
            out.write( INTERFACE_RS485 );
            out.flush();
        } catch (IOException ex) 
        {
            if (errorHdl != null)
            {
                errorHdl.error(new PQ9Exception(ex));
            }
        }
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
    
    private PQ9 processShort(short value)
    {
        value &= 0xFF;
        switch(state)
        {
            case 0:
                if (value == (short)0x7E)
                {
                    state = 1;
                    bs.reset();
                }
                break;

            case 1:
                size = (((int)value) & 0xFF) << 8;
                state = 2;
                break;

            case 2:
                size |= ((int)value) & 0xFF;
                index = 0;
                state = 3;
                break;

            case 3:
                if (index == 0)
                {
                    bs.write(value);
                }
                if (index == 1)
                {
                    bs.write(value);
                }
                if (index == 2)
                {
                    bs.write(value);
                }
                if ((index > 2) && (index < 258))
                {
                    bs.write(value);
                }
                if (index >= 258)
                {
                    state = 0;
                    bs.reset();
                }
                if (index == size - 1)
                {
                    state = 4;
                }
                index++;
                break;

            case 4:
                // last byte
                if (value == 0x7D)
                {
                    // frame found!
                    state = 0;
                    
                    try
                    {
                        PQ9 t = new PQ9(bs.toByteArray(), false);
                        bs.reset();
                        state = 0;
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
                
                break;

            default:
                state = 0;
        }
return null;
    }
    
    @Override
    public synchronized void send(PQ9 frame) throws IOException 
    {
        byte[] data = frame.getFrame();
        byte start = START_OF_MESSAGE;                
        
        out.write( FIRST_BYTE | (start >> 7) & 0x01 );
        out.write( start & 0x7F );

        // do not transmit the CRC
        byte lengthH = (byte)(((data.length - 2) & 0xFF00) >> 8);
        byte lengthL = (byte)((data.length - 2) & 0x00FF);

        out.write( FIRST_BYTE | (lengthH >> 7) & 0x01 );
        out.write( lengthH & 0x7F );
        
        out.write( FIRST_BYTE | (lengthL >> 7) & 0x01 );
        out.write( lengthL & 0x7F );
        
        // do not transmit the CRC
        for(int i = 0; i < data.length - 2; i++)            
        {
            out.write( FIRST_BYTE | (data[i] >> 7) & 0x01 );
            out.write( data[i] & 0x7F );
        }

        byte end = END_OF_MESSAGE;
        out.write( FIRST_BYTE | STOP_TRANSMISSION | (end >> 7) & 0x01 );
        out.write( end & 0x7F );
        out.flush();
    }
}
