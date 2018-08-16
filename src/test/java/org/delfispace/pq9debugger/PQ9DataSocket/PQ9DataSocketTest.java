/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class PQ9DataSocketTest
{
    @BeforeClass
    public static void setUpClass() 
    {
    }
    
    @AfterClass
    public static void tearDownClass() 
    {
    }
    
    @Before
    public void setUp() 
    {
        
    }
    
    @After
    public void tearDown() 
    {
    }

    @Test
    public void testCreateFrame() throws IOException, InterruptedException 
    {              
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
        
        Thread.sleep(100);
        
        Socket clientSocket = new Socket("localhost", srvPort);        
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        Thread.sleep(100);
        
        String string = "{\"_send_\":\"mamma\",\"A\":\"1\"}";
        outToServer.writeBytes(string + "\n");
        
        HashMap<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");
        data.put("key3", "value3");
        srv.send(data);
        String d = inFromServer.readLine();
        System.out.println(d);
        Thread.sleep(1000);
        srv.close();
    }
}
