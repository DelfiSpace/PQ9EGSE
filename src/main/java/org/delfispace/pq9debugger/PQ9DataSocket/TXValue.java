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
public class TXValue 
{
    private final String value;
    private final boolean valid;
    
    public TXValue(String value, boolean valid)
    {
        this.value = value;
        this.valid = valid;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public boolean isValid()
    {
        return valid;
    }
}
