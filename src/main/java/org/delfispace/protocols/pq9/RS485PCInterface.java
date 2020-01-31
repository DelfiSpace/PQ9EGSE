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
    
    static final byte START_OF_MESSAGE = (byte)0x7E;
    static final byte END_OF_MESSAGE = (byte)0x7D;
    
    private int state = 0;
    private int size = 0;
    private int index = 0;
    
    public RS485PCInterface(InputStream in, OutputStream out) 
    {
        super(in, out); 
    }

    @Override
    protected void init() throws IOException
    {
        out.write( FIRST_BYTE | COMMAND | STOP_TRANSMISSION );
        out.write( INTERFACE_RS485 );
        out.flush();
    }       
    
    @Override
    protected PQ9 processWord(int value) throws IOException
    {
        int byteValue = ((value >> 1) & 0x80) | (value & 0xFF);

        switch(state)
        {
            case 0:
                if (byteValue == 0x7E)
                {
                    state = 1;
                    bs.reset();
                }
                break;

            case 1:
                size = byteValue << 8;
                state = 2;
                break;

            case 2:
                size |= byteValue & 0xFF;
                index = 0;
                state = 3;
                break;

            case 3:
                if (index == 0)
                {
                    bs.write( byteValue );
                }
                if (index == 1)
                {
                    bs.write( byteValue );
                }
                if (index == 2)
                {
                    bs.write( byteValue );
                }
                if ((index > 2) && (index < 260))
                {
                    bs.write( byteValue );
                }
                if (index >= 260)
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
                if (byteValue == 0x7D)
                {
                    // frame found!
                    state = 0;
                    try
                    {
                        PQ9 t = new PQ9(bs.toByteArray());
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

        // transmit the packet and the CRC
        byte lengthH = (byte)((data.length & 0xFF00) >> 8);
        byte lengthL = (byte)(data.length & 0x00FF);

        out.write( FIRST_BYTE | (lengthH >> 7) & 0x01 );
        out.write( lengthH & 0x7F );
        
        out.write( FIRST_BYTE | (lengthL >> 7) & 0x01 );
        out.write( lengthL & 0x7F );
        
        // transmit the packet and the CRC
        for(int i = 0; i < data.length; i++)            
        {
            out.write( FIRST_BYTE | (data[i] >> 7) & 0x01 );
            out.write( data[i] & 0x7F );
        }

        byte end = END_OF_MESSAGE;
        out.write( FIRST_BYTE | STOP_TRANSMISSION | (end >> 7) & 0x01 );
        out.write( end & 0x7F );
        out.flush();
    }
    
    @Override
    public synchronized void sendRaw(byte[] data) throws IOException 
    {
        for(int i = 0; i < data.length - 1; i++)            
        {
            out.write( FIRST_BYTE | (data[i] >> 7) & 0x01 );
            out.write( data[i] & 0x7F );
        }

        out.write( FIRST_BYTE | STOP_TRANSMISSION | (data[data.length - 1] >> 7) & 0x01 );
        out.write( data[data.length - 1] & 0x7F );
        out.flush();
    }
}
