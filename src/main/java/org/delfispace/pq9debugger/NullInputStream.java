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
package org.delfispace.pq9debugger;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class NullInputStream extends InputStream
{
    private final Semaphore mutex = new Semaphore(1);
        
    @Override
    public void close() throws IOException
    {
        mutex.release();
    }

    @Override
    public int read() throws IOException 
    {
        try 
        {
            mutex.acquire();
        } catch (InterruptedException ex) 
        {
            // don't care
        }
        return -1;
    }
}
