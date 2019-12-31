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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Nikitas Chronas <N.ChronasFoteinakis@tudelft.nl>
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public abstract class PCInterface 
{
    protected final InputStream in;
    protected final OutputStream out;
    protected PQ9Receiver callback;
    protected PQ9ErrorHandler errorHdl;
    protected readerThread reader;
    
    protected static final int FIRST_BYTE = 0x80;
    protected static final int SECOND_BYTE = 0x00;
    protected static final int ADDRESS_BIT = 0x40;
    protected static final int STOP_TRANSMISSION = 0x20;
    protected static final int COMMAND = 0x10;
    protected static final int INTERFACE_PQ9 = 0x01;
    protected static final int INTERFACE_RS485 = 0x02;
    
    public PCInterface(InputStream in, OutputStream out) 
    {
        this.in = in;
        this.out = out;        
    }
        
    public void close() throws IOException
    {
        if (reader != null)
        {
            this.in.close();
            this.out.close();
            reader.running = false;
            try 
            {
                reader.join();
            } catch (InterruptedException ex) 
            {
                // ignore exception
            }
        }
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

    public abstract PQ9 blockingread() throws IOException;

    public abstract void send(PQ9 frame) throws IOException;

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
                errorHdl.error(ex);
            }
        }
    }
}
