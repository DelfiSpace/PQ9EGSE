/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

/**
 *
 * @author stefanosperett
 */
public class FrameValue 
{
    private final String value;
    private final boolean valid;
    
    public FrameValue(String value, boolean valid)
    {
        this.value = value;
        this.valid = valid;
    }
    
    public FrameValue(String value)
    {
        this.value = value;
        this.valid = true;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder(); 
        str.append(value);
        if (!isValid())            
        {
            str.append(" INVALID\n");
        }
        return str.toString();
    }
}
