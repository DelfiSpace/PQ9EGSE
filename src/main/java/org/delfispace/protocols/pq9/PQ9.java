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
package org.delfispace.protocols.pq9;

import java.util.Arrays;

/**
 *
 * @author Nikitas Chronas <N.ChronasFoteinakis@tudelft.nl>
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class PQ9 
{
    private byte[] data;
    
    /**
     *
     * @param destination
     * @param source
     * @param input
     * @throws PQ9Exception
     */
    public PQ9(int destination, int source, byte[] input) throws PQ9Exception
    {
        if ((destination > 255) || (destination < 0))
        {
            throw new PQ9Exception("Invalid destination range (it should be between 0 and 255).");
        }
        
        if ((source > 255) || (source < 0))
        {
            throw new PQ9Exception("Invalid source range (it should be between 0 and 255).");
        }                
        
        if (input != null)
        {
            if (input.length > 255)
            {
                throw new PQ9Exception("Maximum data size is 255 bytes.");
            }
            data = new byte[5 + input.length];
            data[0] = (byte)(destination & 0xFF);
            data[1] = (byte)(input.length & 0xFF);
            System.arraycopy(input, 0, data, 3, data[1] & 0xFF);
        }
        else
        {
            data = new byte[5];
            data[0] = (byte)(destination & 0xFF);
        }
        data[2] = (byte)(source & 0xFF);
        
        short crc = crc16(data, 0, data[1] + 3);
        data[4 + data[1]] = (byte)(crc & 0xFF);
        data[3 + data[1]] = (byte)((crc >> 8) & 0xFF);                
    }
    
    /**
     *
     * @param input
     * @throws PQ9Exception
     */
    public PQ9(byte[] input) throws PQ9Exception
    {
        if (input.length > 260)
        {
            throw new PQ9Exception("Maximum frame size is 260 bytes.");
        }

        // check size
        if (input[1] == input.length - 5)
        {
            // check if the CRC is correct
            short crc1 = (short)((((int)input[input[1] + 4] & 0xFF) | 
                    ((short)input[input[1] + 3] << 8)) & 0xFFFF) ;
            short crc2 = crc16(input, 0, input[1] + 3);

            if (crc1 == crc2)
            {
                // Frame correct
                data = Arrays.copyOf(input, input.length);
            }
            else
            {
                throw new PQ9Exception(String.format(
                        "CRC mismatch: expected %02X %02X but got %02X %02X", 
                        crc2 & 0xFF, (crc2 & 0xFF00) >> 8 & 0xFF, 
                        input[3 + input[1]], input[4 + input[1]]));
            }
        }
        else
        {
            throw new PQ9Exception("Frame size mismatch: expected " + 
                    input[1] + " but got " + (input.length - 5) );
        }
    }
    
    /**
     *
     * @return
     */
    public byte getDestination()
    {
        return data[0];
    }
    
    /**
     *
     * @return
     */
    public byte getSource()
    {
        return data[2];
    }
    
    /**
     *
     * @return
     */
    public byte getDataSize()
    {
        return data[1];
    }
    
    /**
     * Returns the data field of the frame as byte[].
     * 
     * @return data field of the frame
     */
    public byte[] getData()
    {
        if (data[1] != 0)
        {
            byte[] tmp = new byte[data[1]];
            System.arraycopy(data, 3, tmp, 0, data[1]);
            return tmp;
        }
        return null;
    }
    
    /**
     *
     * @return
     */
    public byte[] getFrame()
    {
        return Arrays.copyOf(data, data.length);
    }
    
    /**
     * Create a PQ9 frame as a reply to the current one. Source and Destination addresses will be swapped.
     * 
     * @param data data field of the reply frame
     * @return new PQ9 frame as a reply to the current one
     * @throws org.delfispace.protocols.pq9.PQ9Exception in case of invalid parameters
     */
    public PQ9 reply(byte[] data) throws PQ9Exception
    {
        return new PQ9(getSource(), getDestination(), data);
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(); 
        sb.append("Full frame: ");
        for(int i = 0; i < data.length; i++)
        {
            sb.append(String.format("0x%02X ", data[i]));
        }
        sb.append("\n");
        sb.append(String.format("\tDestination: 0x%02X\n", getDestination()));
        sb.append(String.format("\tSource: 0x%02X\n", getSource()));
        sb.append("\tData: ");
        if (data[1] == 0)
        {
            sb.append("\n");
        }
        else
        {
            for(int i = 0; i < data[1]; i++)
            {
                sb.append(String.format("0x%02X ", data[3 + i]));
            }
            sb.append("\n");
        }
        int realCRC = (data[3 + data[1]] & 0xFF) | ((((int)data[4 + data[1]]) << 8) & 0xFF00);
        sb.append(String.format("\tCRC: 0x%04X\n", realCRC));
        return sb.toString();
    }
    
    private short crc16(byte[] bytes, int start, int length)
    {
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

        for (int j = start; j < start + length; j++) 
        {
            for (int i = 0; i < 8; i++) 
            {
                boolean bit = ((bytes[j]   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        return (short)(crc & 0xffff);
    }
}
