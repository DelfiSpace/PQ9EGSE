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
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class PQ9DataClient implements Closeable
{
    private final Socket clientSocket;
    private final DataOutputStream outToServer;
    private final BufferedReader inFromServer;
    private final JSONParser parser;
    private int timeout = 1000; // timeout in ms
    
    public PQ9DataClient(String address, int port) throws IOException
    {
        clientSocket = new Socket(address, port);
        
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        parser = new JSONParser();
    }
    
    @Override
    public void close() throws IOException 
    {
        clientSocket.close();
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
    
    public void sendFrame(JSONObject obj) throws IOException
    {
        outToServer.writeBytes(obj.toJSONString() + "\n");
        outToServer.flush();
    }
    
    public JSONObject getFrame(int timeout) throws IOException, ParseException, TimeoutException
    {
        boolean found = false;
        Date before = new Date();
        Date after = new Date();
        while(!(found = inFromServer.ready()) && ((after.getTime() - before.getTime()) < timeout))
        {
            after = new Date();
            try 
            {
                Thread.sleep(10);
            } catch (InterruptedException ex) 
            {
                // ignore the exception
            }
        }
        if (found)
        {       
            return (JSONObject)parser.parse(inFromServer.readLine());            
        }             
        throw new TimeoutException();                   
    }
    
    public JSONObject getFrame() throws IOException, ParseException, TimeoutException
    {
        return getFrame(this.timeout);
    }
}
