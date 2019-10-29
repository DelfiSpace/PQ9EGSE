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
package org.delfispace.CommandWebServer;

import org.json.JSONObject;

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
        JSONObject obj = new JSONObject();    
        obj.put("command", this.command);    
        obj.put("data",this.data);  
        return obj.toString();
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
