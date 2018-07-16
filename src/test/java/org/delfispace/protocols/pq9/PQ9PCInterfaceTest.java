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
package org.delfispace.protocols.pq9;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PQ9PCInterfaceTest 
{
    private PQ9PCInterface protocol;
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
        protocol = new PQ9PCInterface(is, null);
  
        byte[] data = protocol.read().getFrame();
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
        PQ9 t;
        try {
            t = new PQ9(1, 2, new byte[]{(byte)0x7E, (byte)0x7D, (byte)0x7C});
            System.out.println(t);
        } catch (PQ9Exception ex) {
            Logger.getLogger(PQ9PCInterfaceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        runTest(new byte[]{(byte)0x7E, (byte)0x01, (byte)0x00, (byte)0x02, (byte)0xDB, (byte)0xEE}, 
                new byte[]{(byte)0x01, (byte)0x00, (byte)0x02, (byte)0xDB, (byte)0xEE});
        runTest(new byte[]{(byte)0x7E, (byte)0x01, (byte)0x01, (byte)0x02, (byte)0x01, (byte)0xB3, (byte)0x07}, 
                new byte[]{(byte)0x01, (byte)0x01, (byte)0x02, (byte)0x01, (byte)0xB3, (byte)0x07});        
        runTest(new byte[]{(byte)0x7E, (byte)0x01, (byte)0x03, (byte)0x02, (byte)0x7D, (byte)0x5E, (byte)0x7D, (byte)0x5D, (byte)0x7D, (byte)0x5C, (byte)0x4A, (byte)0x1D}, 
                new byte[]{(byte)0x01, (byte)0x03, (byte)0x02, (byte)0x7E, (byte)0x7D, (byte)0x7C, (byte)0x4A, (byte)0x1D});      
    }

    @Test
    public void testPartial() throws IOException
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        byte[] input = new byte[]{(byte)0x7E, (byte)0x01, (byte)0x01, (byte)0x02, 
            (byte)0x01, (byte)0xB3, (byte)0x07, (byte)0xFF, (byte)0x7E, (byte)0x01, 
            (byte)0x00, (byte)0x02, (byte)0xDB, (byte)0xEE};
        
        ByteArrayInputStream is = new ByteArrayInputStream(input);
        protocol = new PQ9PCInterface(is, null);
  
        PQ9 f = protocol.read();
    
        System.out.println(f);
        Assert.assertArrayEquals("Error", f.getFrame(), new byte[] {(byte)0x01, (byte)0x01, (byte)0x02, (byte)0x01, (byte)0xB3, (byte)0x07});
        
        f = protocol.read();       
        System.out.println(f);
        Assert.assertArrayEquals("Error", f.getFrame(), new byte[] {(byte)0x01, (byte)0x00, (byte)0x02, (byte)0xDB, (byte)0xEE});
    }

    @Test
    public void testSend() throws IOException, PQ9Exception
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 frame = new PQ9(1, 2, new byte[]{(byte)0x7E, (byte)0x7D, (byte)0x7C});            
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        LoopbackStream ls = new LoopbackStream();
        
        protocol = new PQ9PCInterface(ls.getInputStream(), os);
        byte[] output = new byte[]{(byte)0x7E, (byte)0x01, (byte)0x03, (byte)0x02, 
                    (byte)0x7D, (byte)0x5E, (byte)0x7D, (byte)0x5D, (byte)0x7D, 
                    (byte)0x5C, (byte)0x4A, (byte)0x1D, (byte)0x7C, (byte)0x7E, 
                    (byte)0x01, (byte)0x03, (byte)0x02, (byte)0x7D, (byte)0x5E, 
                    (byte)0x7D, (byte)0x5D, (byte)0x7D, (byte)0x5C, (byte)0x4A, 
                    (byte)0x1D, (byte)0x7C};
  
        protocol.send(frame);
        protocol.send(frame);
        
        byte[] data = os.toByteArray();
        
        for (int i = 0; i < data.length; i++)
        {
            System.out.print(String.format("%02X ", data[i]));
        }        
        System.out.println();        
        
        Assert.assertArrayEquals("Error", data, output);
    }

    @Test
    public void testAsynchReceive() throws IOException, InterruptedException, PQ9Exception
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 frame = new PQ9(1, 2, new byte[]{(byte)0x7E, (byte)0x7D, (byte)0x7C});
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        LoopbackStream ls = new LoopbackStream();
        
        protocol = new PQ9PCInterface(ls.getInputStream(), ls.getOutputStream());
               
        receivedFrames = 0;
        protocol.setReceiverCallback((PQ9 data) -> {
            Assert.assertArrayEquals("Error", data.getFrame(), frame.getFrame());
            receivedFrames++;
        });
        
        protocol.send(frame);
        protocol.send(frame);
        
        ls.join();
        Assert.assertEquals("Error", 2, receivedFrames);
    }
}
