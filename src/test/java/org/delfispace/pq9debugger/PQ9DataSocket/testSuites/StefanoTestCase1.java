/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import static org.delfispace.pq9debugger.PQ9DataSocket.testSuites.TestVarsMethods.output;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class StefanoTestCase1 
{
    private static String destination;
    
    @BeforeClass 
    public static void BeforePingTestClass() 
    {
        destination = TestParameters.getDestination();
        System.out.println("BeforePingTestClass StefanoTestClass1 " + destination);
        output = new StringBuilder("");   
    }    
    
    @Before
    public void setup() throws IOException
    {
        System.out.println("Setup StefanoTestClass1 ");
    }
    
    @Test
    public void testStefanoOne() 
    { 
        System.out.println("testStefano1One " + destination);
    }
    
    @Test
    public void testStefanoTwo() 
    { 
        System.out.println("testStefano1Two");
    }
}
