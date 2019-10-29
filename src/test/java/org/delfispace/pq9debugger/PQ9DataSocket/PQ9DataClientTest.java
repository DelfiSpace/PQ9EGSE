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
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.IOException;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class PQ9DataClientTest 
{
    private final static int TIMEOUT = 300; // in ms
    private String receivedData;
    
    @Test
    public void testConnection() throws IOException 
    {  
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        // automaticall use a free local port
        PQ9DataSocket srv = new PQ9DataSocket(0);
        int srvPort = srv.getLocalPort();
        
        srv.setCommandHandler((cmd) -> 
        {
            System.out.println("Command:");
            System.out.println("cmd: " + cmd.getCommand());
            System.out.println("data: " + cmd.getData());
        });
        srv.start();
        
        try 
        {
            Thread.sleep(100);
        } catch (InterruptedException ex) 
        {
            // ignore
        }
        
        PQ9DataClient client = new PQ9DataClient("localhost", srvPort);
        client.setTimeout(TIMEOUT);
        
        srv.close();
    }
    
    @Test
    public void testConnectionRefused() throws IOException 
    {  
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        // automaticall use a free local port
        PQ9DataSocket srv = new PQ9DataSocket(0);
        int srvPort = srv.getLocalPort();
        
        srv.setCommandHandler((cmd) -> 
        {
            System.out.println("Command:");
            System.out.println("cmd: " + cmd.getCommand());
            System.out.println("data: " + cmd.getData());
        });
        srv.start();
        
        try 
        {
            Thread.sleep(100);
        } catch (InterruptedException ex) 
        {
            // ignore
        }
        
        try
        {
            PQ9DataClient client = new PQ9DataClient("localhost", srvPort+2);
            Assert.fail();
        } catch (java.net.ConnectException ex)
        {

        }        
    }
    
    @Test
    public void testServerToClient() throws IOException, ParseException, TimeoutException 
    {  
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        // automaticall use a free local port
        PQ9DataSocket srv = new PQ9DataSocket(0);
        int srvPort = srv.getLocalPort();
        
        srv.setCommandHandler((cmd) -> 
        {
            System.out.println("Command:");
            System.out.println("cmd: " + cmd.getCommand());
            System.out.println("data: " + cmd.getData());
        });
        srv.start();
        
        try 
        {
            Thread.sleep(100);
        } catch (InterruptedException ex) 
        {
            // ignore
        }
        
        PQ9DataClient client = new PQ9DataClient("localhost", srvPort);
        client.setTimeout(TIMEOUT);
        
        Frame f = new Frame();
        f.add("one", "OBC");
        f.add("two", "EPS");
        f.add("three", "COMMS", false);
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("one", "{\\\"valid\\\":\\\"true\\\",\\\"value\\\":\\\"OBC\\\"}");
        hmap.put("two", "{\\\"value\\\":\\\"EPS\\\"}");
        hmap.put("three", "{\\\"valid\\\":\\\"false\\\",\\\"value\\\":\\\"COMMS\\\"}");
        srv.send(hmap);

        try 
        {
            Thread.sleep(100);
        } catch (InterruptedException ex) 
        {
            // ignore
        }
        
        Frame o = client.getFrame2();
        Assert.assertEquals("OBC", o.get("one").getValue());
        Assert.assertEquals(true, o.get("one").isValid());
        Assert.assertEquals("EPS", o.get("two").getValue());
        Assert.assertEquals(true, o.get("two").isValid());
        Assert.assertEquals("COMMS", o.get("three").getValue());
        Assert.assertEquals(false, o.get("three").isValid());        
        Assert.assertEquals(null, o.get("four"));
        
        srv.close();
    }
    
    @Test
    public void testClientToServer() throws IOException, ParseException, TimeoutException
    {  
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        // automaticall use a free local port
        PQ9DataSocket srv = new PQ9DataSocket(0);
        int srvPort = srv.getLocalPort();
        
        srv.setCommandHandler((cmd) -> 
        {
            System.out.println("Command:");
            System.out.println("cmd: " + cmd.getCommand());
            System.out.println("data: " + cmd.getData());
            receivedData = cmd.getData();
            
        });
        srv.start();
        
        try 
        {
            Thread.sleep(100);
        } catch (InterruptedException ex) 
        {
            // ignore
        }
        
        PQ9DataClient client = new PQ9DataClient("localhost", srvPort);
        client.setTimeout(10000);
        
        Frame f = new Frame();
        f.add("one", "OBC");
        f.add("two", "EPS");
        f.add("three", "COMMS", false);
        client.sendFrame2(f);

        try 
        {
            Thread.sleep(1000);
        } catch (InterruptedException ex) 
        {
            // ignore
        }
        srv.close();
        
        JSONParser parser = new JSONParser();
        JSONObject parsed = (JSONObject) parser.parse(receivedData);

        JSONObject partial1 = (JSONObject) parser.parse((String)parsed.get("one"));
        JSONObject partial2 = (JSONObject) parser.parse((String)parsed.get("two"));
        JSONObject partial3 = (JSONObject) parser.parse((String)parsed.get("three"));
        
        Assert.assertEquals("OBC", partial1.get("value"));
        Assert.assertTrue((boolean)partial1.get("valid"));
        Assert.assertEquals("EPS", partial2.get("value"));
        Assert.assertTrue((boolean)partial2.get("valid"));
        Assert.assertEquals("COMMS", partial3.get("value"));
        Assert.assertFalse((boolean)partial3.get("valid"));        
    }

}
