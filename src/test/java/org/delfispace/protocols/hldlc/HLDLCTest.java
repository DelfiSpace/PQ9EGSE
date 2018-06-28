/*
 * Copyright (C) 2018 , Stefano Speretta
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
package org.delfispace.protocols.hldlc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.delfispace.protocols.LoopbackStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class HLDLCTest 
{
    private HLDLC protocol;
    private int receivedFrames = 0;
            
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

    private void runTest(byte[] input, byte[] output) throws IOException
    {
        ByteArrayInputStream is = new ByteArrayInputStream(input);
        protocol = new HLDLC(is, null);
  
        byte[] data = protocol.read();
        for (int i = 0; i < data.length; i++)
        {
            System.out.print(String.format("%02X ", data[i]));
        }        
        System.out.println();

        Assert.assertArrayEquals("Error", output, data);
    }

    @Test
    public void testBlockingReceive() throws IOException 
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        runTest(new byte[]{(byte)0x7E, (byte)0x01, (byte)0x02, (byte)0x7C}, 
                new byte[]{(byte)0x01, (byte)0x02});
        runTest(new byte[]{(byte)0x7E, (byte)0x7D, (byte)0x5E, (byte)0x7C}, 
                new byte[]{(byte)0x7E});
        runTest(new byte[]{(byte)0x7E, (byte)0x77, (byte)0x7D, (byte)0x5D, (byte)0x7C}, 
                new byte[]{(byte)0x77, (byte)0x07D});
        runTest(new byte[]{(byte)0x7E, (byte)0x4D, (byte)0x7D, (byte)0x5C, (byte)0x7C}, 
                new byte[]{(byte)0x4D, (byte)0x07C});        
        runTest(new byte[]{(byte)0x1E, (byte)0x7E, (byte)0x42, (byte)0x7D, (byte)0x5C, (byte)0x7C}, 
                new byte[]{(byte)0x42, (byte)0x07C});        
        runTest(new byte[]{(byte)0x7E, (byte)0x41, (byte)0x7D, (byte)0x5C, (byte)0x7C, (byte)0x2E}, 
                new byte[]{(byte)0x41, (byte)0x07C});        
    }

    @Test
    public void testPartial() throws IOException
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        byte[] input = new byte[]{(byte)0x7E, (byte)0x01, (byte)0x02, (byte)0x7C, (byte)0xFF, (byte)0x7E, (byte)0x02, (byte)0x02, (byte)0x7C};
        
        ByteArrayInputStream is = new ByteArrayInputStream(input);
        protocol = new HLDLC(is, null);
  
        byte[] data = protocol.read();
        for (int i = 0; i < data.length; i++)
        {
            System.out.print(String.format("%02X ", data[i]));
        }        
        System.out.println();
        Assert.assertArrayEquals("Error", data, new byte[] {(byte)0x01, (byte)0x02});
        
        data = protocol.read();
        for (int i = 0; i < data.length; i++)
        {
            System.out.print(String.format("%02X ", data[i]));
        }        
        System.out.println();
        Assert.assertArrayEquals("Error", data, new byte[] {(byte)0x02, (byte)0x02});
    }
    
    @Test
    public void testSend() throws IOException
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        LoopbackStream ls = new LoopbackStream();
        
        protocol = new HLDLC(ls.getInputStream(), ls.getOutputStream());
        byte[] input = new byte[]{(byte)0x01, (byte)0x02, (byte)0x7E, (byte)0xFF, (byte)0xFE };
  
        protocol.send(input);
        protocol.send(input);
        
        byte[] data = protocol.read();
        
        for (int i = 0; i < data.length; i++)
        {
            System.out.print(String.format("%02X ", data[i]));
        }        
        System.out.println();        
                        
        data = protocol.read();
        
        Assert.assertArrayEquals("Error", data, input);
    }
    
    @Test
    public void testAsynchReceive() throws IOException, InterruptedException
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        LoopbackStream ls = new LoopbackStream();
        
        protocol = new HLDLC(ls.getInputStream(), ls.getOutputStream());
        byte[] input = new byte[]{(byte)0x01, (byte)0x02, (byte)0x7E, (byte)0xFF, (byte)0xFE };
               
        receivedFrames = 0;
        protocol.setReceiverCallback((byte[] data) -> {
            Assert.assertArrayEquals("Error", data, input);
            receivedFrames++;
        });
        
        protocol.send(input);
        protocol.send(input);
        
        ls.join();
        Assert.assertEquals("Error", receivedFrames, 2);
    }
}
