/*
 * Copyright (C) 2018 Stefano Speretta
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

import java.util.List;
import org.xtce.toolkit.XTCEDatabase;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Configuration 
{
    private static Configuration instance;
    private static List<String> serialPorts;
    private static String serialPort;
    private static XTCEDatabase db = null;
    
    private Configuration()
    {
        
    }
    
    public static synchronized Configuration getInstance( ) 
    {
        if (instance == null)
        {
            instance = new Configuration();
        }
        return instance;
    }
    
    public String getFile()
    {
        return "EPS.xml";
    }
    
    public void setSerialPorts(List<String> sp)
    {
        serialPorts = sp;
    }
    
    public List<String> getSerialPorts()
    {
        return serialPorts;
    }
    
    public void setSerialPort(String sp)
    {
        serialPort = sp;
    }
    
    public String getSerialPort()
    {
        return serialPort;
    }
    
    public void setXTCEDatabase(XTCEDatabase database)
    {
        db = database;
    }
    
    public XTCEDatabase getXTCEDatabase()
    {
       return db;
    }
}
