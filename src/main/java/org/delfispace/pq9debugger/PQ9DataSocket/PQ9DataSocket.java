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
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.delfispace.CommandWebServer.Command;
import org.delfispace.pq9debugger.Subscriber;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class PQ9DataSocket extends Thread 
{
    private final ServerSocket socketPool;
    private Socket connectionSocket = null;
    private DataOutputStream outputStream = null;
    private Subscriber cmdHandler = null;
    
    /**
     * Create a PQ9 Data socket object connected to the specified port. 
     * For automatic port selection, set the port 0.
     * 
     * @param port
     * @throws IOException
     */
    public PQ9DataSocket(int port) throws IOException
    {
        socketPool = new ServerSocket(port);    
    }
    
    /**
     * Return the local port being used by the server.
     * 
     * @return
     */
    public int getLocalPort()
    {
        return socketPool.getLocalPort();
    }
    
    /**
     *
     * @param hdl
     */
    public void setCommandHandler(Subscriber hdl)
    {
        cmdHandler = hdl;
    }
    
    /**
     *
     * @param data
     */
    public void send(HashMap<String, String> data)
    {
        if (outputStream != null)
        {
            try 
            {
                JSONObject obj=new JSONObject();
                data.forEach((k,v)->obj.put(k,v));
                outputStream.writeBytes(obj.toJSONString() + "\n");
                outputStream.flush();
            } catch (IOException ex) 
            {   
                // ignore error
            }
        }
    }
    
    /**
     *
     * @throws IOException
     */
    public void close() throws IOException
    {
        socketPool.close();
    }
    
    @Override
    public void run() 
    {
        while (true) 
        {           
            try 
            {
                connectionSocket = socketPool.accept();
                
                JSONParser parser = new JSONParser(); 
                BufferedReader inputStream =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                outputStream = new DataOutputStream(connectionSocket.getOutputStream());
                
                while(connectionSocket.isConnected())
                {
                    JSONObject obj = (JSONObject)parser.parse(inputStream.readLine());
                    if (cmdHandler != null)
                    {
                        cmdHandler.subscribe(new Command("SendCommand", (String)obj.toString()));
                    }                    
                }
            } catch (IOException | NullPointerException ex) 
            {
                // do not care about these ones, just drop the connection
            } catch (ParseException ex) 
            {   
                Logger.getLogger(PQ9DataSocket.class.getName()).log(Level.SEVERE, null, ex);
            } 
            outputStream = null;
            connectionSocket = null;
        }
    }
}
