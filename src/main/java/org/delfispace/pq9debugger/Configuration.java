/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
