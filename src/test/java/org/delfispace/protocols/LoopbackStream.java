/*
 * Copyright (C) 2018 , Stefano Speretta
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
package org.delfispace.protocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class LoopbackStream
{
    private final LoopbackOutput out = new LoopbackOutput();
    private final LoopbackInput in = new LoopbackInput();
    private Object lock = new Object();
    
    private final ArrayList<Byte> l = new ArrayList<>();
   
    public OutputStream getOutputStream()
    {
        return out;
    }
    
    public InputStream getInputStream()
    {
        return in;
    }
    
    public void close() throws IOException
    {
        out.close();
        in.close();        
    }
    
    public void join()
    {
        try 
        {
            int len;
            synchronized(lock)
            {
                len = l.size();
            }
            while (len > 0)
            {
                Thread.sleep(100);  
                         
                synchronized(lock)
                {   
                    len = l.size();
                }
            }
        } catch (InterruptedException ex) 
        {
            // nothing to do
        }
    }
    class LoopbackInput extends InputStream
    {
        private boolean open = true;
        
        @Override
        public void close() throws IOException
        {
            open = false;
            super.close();
        }
                
        @Override
        public int read() throws IOException 
        {
            int len;
            synchronized(lock)
            {
                len = l.size();
            }
            while (len < 1)
            {                
                if (!open)
                {
                    return -1;
                }
                
                try 
                {
                    Thread.sleep(120);
                } catch (InterruptedException ex) 
                {
                    // nothing to do
                }
                synchronized(lock)
                {
                    len = l.size();
                }
            }
            synchronized(lock)
            {
                return (int)l.remove(0) & 0xFF;
            }
            
        }
    }

    class LoopbackOutput extends OutputStream
    {
        private boolean open = true;
        
        @Override
        public void write(int b) throws IOException 
        {
            if (open)
            {
                synchronized(lock)
                {
                    l.add(l.size(), (byte)(b & 0xFF));
                }                                
            }
        }
        
        @Override
        public void close() throws IOException
        {
            open = false;
            super.close();
        }
    }
}
