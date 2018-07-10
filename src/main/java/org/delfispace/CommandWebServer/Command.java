/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.CommandWebServer;

import org.json.simple.JSONObject;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Command 
{
    private final String command;
    private final String data;
    
    public Command(String command, String data)
    {
        this.command = command;
        this.data = data;
    }
    
    public String getData()
    {
        return this.data;
    }
    
    public String getCommand()
    {
        return this.command;
    }
    
    public String toJSON()
    {
        JSONObject obj=new JSONObject();    
        obj.put("command", this.command);    
        obj.put("data",this.data);  
        return obj.toJSONString();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Command: \"");
        sb.append(this.command);
        sb.append("\", Data: \"");
        sb.append(this.data);
        sb.append("\"");
        return sb.toString();
    }
}
