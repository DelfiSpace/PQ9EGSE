/*
 * Copyright (C) 2019 Michael van den Bos
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

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Michael van den Bos
 * mfvandenbos@gmail.com
 */
public class TestEPS_Scenario 
{
    
    public static void main(String[] args) throws IOException, InterruptedException, ParseException 
    {
        //Timer scheduler = new Timer(); 
        
       // TimerTask nextTask1 = new TaskListItem();
        
        SerialPort[] seenPorts = SerialPort.getCommPorts();
        
        for (SerialPort item : seenPorts) {
            System.out.println(item);
        }
        String portName;
        portName = "COM4";seenPorts[2].getSystemPortName(); //note this is device specific. 
       
        try {
            TaskList taskList = new TaskList(1, "COM4");
        } catch (IOException ex) {
            Logger.getLogger(TaskList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}

 
