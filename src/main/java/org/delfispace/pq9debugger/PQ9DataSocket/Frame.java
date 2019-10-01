/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 *
 * @author stefanosperett
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
