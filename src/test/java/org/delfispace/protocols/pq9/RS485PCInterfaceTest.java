/*
 * Copyright (C) 2020 , Stefano Speretta
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
import org.delfispace.pq9debugger.LoopbackStream;
import org.delfispace.pq9debugger.NullOutputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class RS485PCInterfaceTest 
{
    private PCInterface protocol;
    private int receivedFrames = 0;

    private void runTest(byte[] input, byte[] output) throws IOException
    {
        ByteArrayInputStream is = new ByteArrayInputStream(input);
        protocol = new RS485PCInterface(is, new NullOutputStream());
          
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

        runTest(new byte[]{(byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, (byte)0x80, 
                           (byte)0x08, (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x03, 
                           (byte)0x80, (byte)0x02, (byte)0x80, (byte)0x7E, (byte)0x80, 
                           (byte)0x7D, (byte)0x80, (byte)0x7C, (byte)0x80, (byte)0x4A, 
                           (byte)0x80, (byte)0x1D, (byte)0x80, (byte)0x7D}, 
                new byte[]{(byte)0x01, (byte)0x03, (byte)0x02, (byte)0x7E, (byte)0x7D, 
                           (byte)0x7C, (byte)0x4A, (byte)0x1D});
        runTest(new byte[]{(byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, (byte)0x80, 
                           (byte)0x05, (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x00, 
                           (byte)0x80, (byte)0x02, (byte)0x81, (byte)0x5B, (byte)0x81, 
                           (byte)0x6E, (byte)0x80, (byte)0x7D}, 
                new byte[]{(byte)0x01, (byte)0x00, (byte)0x02, (byte)0xDB, (byte)0xEE});
        runTest(new byte[]{(byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, (byte)0x80, 
                           (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x01, 
                           (byte)0x80, (byte)0x02, (byte)0x80, (byte)0x01, (byte)0x81, 
                           (byte)0x33, (byte)0x80, (byte)0x07, (byte)0x80, (byte)0x7D}, 
                new byte[]{(byte)0x01, (byte)0x01, (byte)0x02, (byte)0x01, (byte)0xB3, 
                           (byte)0x07});        
        runTest(new byte[]{(byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, (byte)0x80, 
                           (byte)0x08, (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x03, 
                           (byte)0x80, (byte)0x02, (byte)0x80, (byte)0x7E, (byte)0x80, 
                           (byte)0x7D, (byte)0x80, (byte)0x7C, (byte)0x80, (byte)0x4A, 
                           (byte)0x80, (byte)0x1D, (byte)0x80, (byte)0x7D, (byte)0x80, 
                           (byte)0x01, (byte)0x80, (byte)0x03, (byte)0x80, (byte)0x02, 
                           (byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x7D, (byte)0x80, 
                           (byte)0x7C, (byte)0x80, (byte)0x4A, (byte)0x80, (byte)0x1D, 
                           (byte)0x80, (byte)0x7D}, 
                new byte[]{(byte)0x01, (byte)0x03, (byte)0x02, (byte)0x7E, (byte)0x7D, 
                           (byte)0x7C, (byte)0x4A, (byte)0x1D});      
    }

    @Test
    public void testPartial() throws IOException
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        byte[] input = new byte[]{(byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, 
                                  (byte)0x80, (byte)0x06, (byte)0x80, (byte)0x01, 
                                  (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x02, 
                                  (byte)0x80, (byte)0x01, (byte)0x81, (byte)0x33, 
                                  (byte)0x80, (byte)0x07, (byte)0x80, (byte)0x7D,
                                  (byte)0x81, (byte)0x7F, (byte)0x80, (byte)0x7E, 
                                  (byte)0x80, (byte)0x00, (byte)0x80, (byte)0x05, 
                                  (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x00, 
                                  (byte)0x80, (byte)0x02, (byte)0x81, (byte)0x5B, 
                                  (byte)0x81, (byte)0x6E, (byte)0x80, (byte)0x7D,
                                  (byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, 
                                  (byte)0x80, (byte)0x06, (byte)0x80, (byte)0x01, 
                                  (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x02, 
                                  (byte)0x80, (byte)0x01, (byte)0x81, (byte)0x33, 
                                  (byte)0x80, (byte)0x07, (byte)0x80, (byte)0x7D};
        
        ByteArrayInputStream is = new ByteArrayInputStream(input);
        protocol = new RS485PCInterface(is, new NullOutputStream());
  
        PQ9 f = protocol.read();
        Assert.assertArrayEquals("Error", f.getFrame(), new byte[] {(byte)0x01, (byte)0x01, (byte)0x02, (byte)0x01, (byte)0xB3, (byte)0x07});
        
        f = protocol.read();       
        Assert.assertArrayEquals("Error", f.getFrame(), new byte[] {(byte)0x01, (byte)0x00, (byte)0x02, (byte)0xDB, (byte)0xEE});
        
        f = protocol.read(); 
        Assert.assertArrayEquals("Error", f.getFrame(), new byte[] {(byte)0x01, (byte)0x01, (byte)0x02, (byte)0x01, (byte)0xB3, (byte)0x07});
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
        
        protocol = new RS485PCInterface(ls.getInputStream(), os);
        byte[] output = new byte[]{(byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, 
           (byte)0x80, (byte)0x08, (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x03, 
           (byte)0x80, (byte)0x02, (byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x7D, 
           (byte)0x80, (byte)0x7C, (byte)0x80, (byte)0x4A, (byte)0x80, (byte)0x1D,
           (byte)0xA0, (byte)0x7D, (byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x00, 
           (byte)0x80, (byte)0x08, (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x03, 
           (byte)0x80, (byte)0x02, (byte)0x80, (byte)0x7E, (byte)0x80, (byte)0x7D, 
           (byte)0x80, (byte)0x7C, (byte)0x80, (byte)0x4A, (byte)0x80, (byte)0x1D, 
           (byte)0xA0, (byte)0x7D};
  
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
        LoopbackStream ls = new LoopbackStream();
        
        protocol = new RS485PCInterface(ls.getInputStream(), ls.getOutputStream());
               
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
    
    @Test
    public void testAsynchReceiveLongFrame() throws IOException, InterruptedException, PQ9Exception
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9[] frames = new PQ9[4];
        frames[0] = new PQ9(1, 2, new byte[100]);
        frames[1] = new PQ9(1, 2, new byte[120]);
        frames[2] = new PQ9(1, 2, new byte[250]);
        frames[3] = new PQ9(1, 2, new byte[255]);
        LoopbackStream ls = new LoopbackStream();
        
        protocol = new RS485PCInterface(ls.getInputStream(), ls.getOutputStream());

        receivedFrames = 0;
        protocol.setReceiverCallback((PQ9 data) -> {
            Assert.assertArrayEquals("Error", data.getFrame(), frames[receivedFrames].getFrame());
            receivedFrames++;
        });

        for (PQ9 frame : frames) 
        {
            protocol.send(frame);
        }
        
        ls.join();
        Assert.assertEquals("Error", frames.length, receivedFrames);
    }
    
    @Test
    public void testInit() throws IOException, InterruptedException, PQ9Exception
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        LoopbackStream ls = new LoopbackStream();        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        protocol = new RS485PCInterface(ls.getInputStream(), os);
               
        protocol.setReceiverCallback((PQ9 data) -> { });
        
        // check if the interface sends the initialisation command
        Assert.assertArrayEquals("Error", new byte[]{(byte)0xB0, (byte)0x02}, os.toByteArray());
        
        // request the initialisation command again
        ls.getOutputStream().write(new byte[]{(byte)0x90, (byte)0x00});
        ls.getOutputStream().flush();
        // give time to the thread to be scheduled
        Thread.sleep(1000);

        // check if both initialisation commands have been received
        Assert.assertArrayEquals("Error", new byte[]{(byte)0xB0, (byte)0x02, (byte)0xB0, (byte)0x02}, os.toByteArray());        
    }
}
