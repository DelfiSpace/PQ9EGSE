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
package org.example.gui;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class EventSocket extends WebSocketAdapter
{
    JSONParser parser = new JSONParser();      
    private int counter = 0;
    
    public EventSocket()
    {        
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
        try {            
            if (this.isConnected())
                {
                    if ((counter % 2) != 0)
                    {
                        // Sending log...
                        JSONObject obj=new JSONObject();    
                        obj.put("command","log");    
                        obj.put("data",(new Date()).toString());     
                        super.getRemote().sendString(obj.toString());
                    }
                    else
                    {
                        //System.out.println("Sending downlink...");
                        super.getRemote().sendString("{\"command\":\"downlink\",\"data\":\"" + counter + "\"}");
                    }
                    if ((counter % 21) == 0)
                    {
                        super.getRemote().sendString("{\"command\":\"abc\",\"data\":\"" + counter + "\"}");
                    }
                    if ((counter % 14) == 0)
                    {
                        super.getRemote().sendString("ciao mamma");
                    }
                    counter++;
                }
        } catch (IOException ex) {
            Logger.getLogger(EventSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        }, 0, 500, TimeUnit.MILLISECONDS);      
    }
    
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        System.out.println("Socket Connected: " + sess);
        
        
    }
    
    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        try {
            JSONObject obj = (JSONObject)parser.parse(message);
            String cmd = (String) obj.get("command");
            String data = (String) obj.get("data");
            String test = (String) obj.get("mamma");
            System.out.println(cmd);
            System.out.println(data);
            System.out.println(test);
        } catch (ParseException ex) 
        {
            Logger.getLogger(EventSocket.class.getName()).log(Level.SEVERE, null, ex);
        }              
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }
    
    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}