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
package org.delfispace.CommandWebServer;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.delfispace.pq9debugger.HeaderTab;
import org.delfispace.pq9debugger.cmdMultiPublisher;
import org.delfispace.pq9debugger.cmdMultiSubscriber;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class CommandWebSocket extends WebSocketAdapter
{
    private final JSONParser parser = new JSONParser();   
    private final cmdMultiPublisher cmd = cmdMultiPublisher.getInstance();
    private final cmdMultiSubscriber sub = cmdMultiSubscriber.getInstance();
    private String endpoint = "";
    
    // idle timeout in ms
    private final int IDLE_TIMEOUT = 5 * 60 * 1000;
    
    public CommandWebSocket()
    {        
        sub.setSubscriber((Command command) -> 
        {
            try 
            {
                if (this.isConnected())
                {
                    // generate the JSON encoding...
                    this.getRemote().sendString(command.toJSON());
                }
            } catch (IOException ex) 
            {
                Logger.getLogger(CommandWebSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        });                     
    }
    
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        // make sure a keep-alive timeout is enabled
        sess.setIdleTimeout(Duration.ofMillis(IDLE_TIMEOUT));
        endpoint = sess.getRemote().getRemoteAddress().toString();
        Logger.getLogger(CommandWebSocket.class.getName()).log(Level.FINEST, 
                "Websocket connected from {0}", endpoint);
        
        sub.publish(new Command("header", HeaderTab.generate()));
    }
    
    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        try 
        {
            JSONObject obj = (JSONObject)parser.parse(message);
            this.cmd.publish(new Command((String) obj.get("command"), (String) obj.get("data")));
        } catch (ParseException ex) 
        {
            Logger.getLogger(CommandWebSocket.class.getName()).log(Level.SEVERE, 
                    String.format("Invalid message from %s: %s", endpoint, message), ex);
        }              
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        Logger.getLogger(CommandWebSocket.class.getName()).log(Level.FINEST, 
                String.format("Websocket %s closed: [%d] %s",
                endpoint, statusCode, reason));
    }
    
    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        Logger.getLogger(CommandWebSocket.class.getName()).log(Level.FINEST, 
                String.format("Websocket %s error:", endpoint), cause);
    }
}
