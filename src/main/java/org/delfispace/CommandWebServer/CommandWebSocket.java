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

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.button;
import static j2html.TagCreator.dd;
import static j2html.TagCreator.div;
import static j2html.TagCreator.dl;
import static j2html.TagCreator.dt;
import static j2html.TagCreator.each;
import static j2html.TagCreator.fieldset;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;
import static j2html.TagCreator.legend;
import static j2html.TagCreator.link;
import static j2html.TagCreator.option;
import static j2html.TagCreator.select;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.delfispace.pq9debugger.clientsInterface;
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
    private final clientsInterface cs = clientsInterface.getInstance();
    private final cmdMultiPublisher cmd = cmdMultiPublisher.getInstance();
    private final cmdMultiSubscriber sub = cmdMultiSubscriber.getInstance();
    private int tabIndex = 0;
    private final StringBuilder idArray = new StringBuilder();
    // idle tioemout in ms
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
        sess.setIdleTimeout(IDLE_TIMEOUT);                
        System.out.println("Socket Connected");
        
        List<String[]> numbers = new ArrayList();
        numbers.add(new String[]{"dest", "Destination", "7"});
        numbers.add(new String[]{"src", "Source", "1"});
        numbers.add(new String[]{"data", "Data", "17 1"});
        
        Tag t = div
        (
            link().withRel("stylesheet").withType("text/css").withHref("/css/uplink.css"),
            fieldset
            (                            
                legend("Raw Frame"),
                dl
                (
                    each(numbers, i -> entry(i[0], i[1], i[2]))
                ),
                button("Send").attr("id", "send1").attr("onclick", "fetchData(this.id, [" + idArray.toString()+ "])").attr("tabindex", tabIndex)                            
            )
        );       
        sub.publish(new Command("uplink", t.render()));   
        
        Tag t1 = div
        (
            select
            (
                option("A").withValue("A"),
                option("B").withValue("B"),
                option("C").withValue("C")
            ).withId("serialPort"),
            button("Select").attr("onclick", "setSerialPort();"),
            button("Reset").attr("onclick", "resetLayout();")
        );
        sub.publish(new Command("header", t1.render()));
    }
    
    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        try {
            JSONObject obj = (JSONObject)parser.parse(message);
            this.cmd.publish(new Command((String) obj.get("command"), (String) obj.get("data")));
        } catch (ParseException ex) 
        {
            Logger.getLogger(CommandWebSocket.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public ContainerTag entry(String id, String description, String value) 
    {
        tabIndex++;
        if (idArray.length() != 0)
        {
            idArray.append(", ");
        }
        idArray.append("\'");
        idArray.append(id);
        idArray.append("\'");        
        return div
            (
                dt
                ( 
                    label(description + ":").attr("for", id).attr("tabindex", tabIndex)
                ), 
                dd
                (
                    input(attrs("#" + id)).withType("text").withValue(value)
                )
            );
    }
}
