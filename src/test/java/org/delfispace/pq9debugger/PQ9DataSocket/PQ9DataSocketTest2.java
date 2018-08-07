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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class PQ9DataSocketTest2 
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
    Socket clientSocket = new Socket("localhost", 10000);        
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        for(int h = 0; h < 1; h++)
        {
            Thread.sleep(100);
            System.out.println("Connected");
            String string = "{\"_send_\":\"Ping\",\"Destination\":\"EPS\"}";
            outToServer.writeBytes(string + "\n");
        }
        System.out.println("Done");
        String rx;
        while((rx = inFromServer.readLine()) != null)
        {
            System.out.println(rx);
        }
        //Thread.sleep(10000);
    }
}
