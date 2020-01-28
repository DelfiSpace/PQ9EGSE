/*
 * Copyright (C) 2019 Stefano Speretta
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
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Frame 
{
    private final HashMap<String, FrameValue> hmap = new HashMap<String, FrameValue>();
    
    public void add(String name, String value, boolean valid)
    {
        hmap.put(name, new FrameValue(value, valid));
    }
    
    public void add(String name, String value)
    {
        hmap.put(name, new FrameValue(value, true));
    }
    
    public FrameValue get(String name)
    {
        return hmap.get(name);
    }
    
    public void forEach(BiConsumer<String, FrameValue> action) 
    {
        hmap.forEach(action);
    }
    
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder(); 
        hmap.forEach((k,v)->
        {
            str.append(k);
            str.append(": ");
            str.append(((FrameValue)v).toString());
            str.append("\n");            
        });
        return str.toString();
    }
}
