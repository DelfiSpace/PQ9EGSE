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

    public PQ9PCInterface(InputStream in, OutputStream out) 
    {
        super(in, out);
    }
    
    @Override
    protected void init() throws IOException
    {
        out.write( FIRST_BYTE | COMMAND | STOP_TRANSMISSION );
        out.write( INTERFACE_PQ9 );
        out.flush();
    }
    
    @Override
    protected PQ9 processWord(int value) throws IOException
    {
        int byteValue = ((value >> 1) & 0x80) | (value & 0x7F);
                
        if ((value & (ADDRESS_BIT << 8)) != 0) 
        {
            // first byte of the packet
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
            bs.write( byteValue );

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
                sizeFrame = byteValue;
            }
        }
        else
        {
            //a byte received without having received a start
            if (errorHdl != null)
            {
                errorHdl.error(new PQ9Exception(String.format("Unexpected byte: %02X", byteValue)));
            }
        }

        // prevent the buffer from growing too much
        if (bs.size() > 261)
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
    
    @Override
    public synchronized void sendRaw(byte[] data) throws IOException 
    {
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
    
    @Override
    public synchronized void resetEGSE() throws IOException
    {
        out.write( FIRST_BYTE | COMMAND | STOP_TRANSMISSION );
        out.write( RESET_EGSE );
        out.flush();
    }
    
    @Override
    public synchronized void toggleChargeEGSE() throws IOException
    {
        out.write( FIRST_BYTE | COMMAND | STOP_TRANSMISSION );
        out.write( TOGGLE_CHARGE_EGSE );
        out.flush();
    }
    
    @Override
    public synchronized void toggleDischargeEGSE() throws IOException
    {
        out.write( FIRST_BYTE | COMMAND | STOP_TRANSMISSION );
        out.write( TOGGLE_DISCHARGE_EGSE );
        out.flush();
    }
    
}
