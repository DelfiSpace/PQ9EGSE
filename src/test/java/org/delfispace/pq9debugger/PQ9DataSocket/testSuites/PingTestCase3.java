/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class PingTestCase3 {
   final protected String expected = "Reply";
  
    public PingTestCase3(){
        // do nothing
    }
   
   @Test
    public void testPingVVV(){
        assertEquals(expected, "Reply");  
    }
}
