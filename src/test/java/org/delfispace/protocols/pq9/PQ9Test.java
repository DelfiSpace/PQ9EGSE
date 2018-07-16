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

import java.io.IOException;
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
public class PQ9Test 
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
    public void testCreateFrame() throws IOException, PQ9Exception 
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 t1 = new PQ9(0x01, 0x02, new byte[]{(byte)0xAA, (byte)0xBB, (byte)0xCC});
        
        Assert.assertArrayEquals("Error", t1.getFrame(), 
                new byte[]{(byte)0x01, (byte)0x03, (byte)0x02, (byte)0xAA, 
                    (byte)0xBB, (byte)0xCC, (byte)0xE8, (byte)0x60});
    }

    @Test
    public void testCreateFrame4() throws IOException, PQ9Exception 
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 t1 = new PQ9(0x01, 0x02, null);
        
        
        //Assert.assertArrayEquals("Error", t1.getData(), new byte[]{});
        System.out.println(t1);
    }

    @Test
    public void testCreateFrame2() throws IOException, PQ9Exception 
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 t1 = new PQ9(new byte[]{(byte)0x01, (byte)0x03, (byte)0x02, 
            (byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xE8, (byte)0x60});
        
        System.out.println(t1);
    }

    @Test
    public void testCreateFrame3() throws IOException, PQ9Exception 
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 t1 = new PQ9(new byte[]{(byte)0x01, (byte)0x00, (byte)0x02, 
                        (byte)0xDB, (byte)0xEE});
        
        System.out.println(t1);
    }

    @Test
    public void testCreateFrame5() throws IOException, PQ9Exception 
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 t1 = new PQ9(new byte[]{(byte)0x01, (byte)0x00, (byte)0x02, 
                        (byte)0xDB, (byte)0xEE});
        
        System.out.println(t1);
    }
}
