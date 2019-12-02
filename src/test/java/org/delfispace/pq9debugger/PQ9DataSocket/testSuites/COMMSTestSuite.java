/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/**
 *
 * @author LocalAdmin
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        //PingTestValidParameters.class,
       // PingTestInvalidParameters.class,
       //PingTestCase3.class,
        //PingTestCase4.class
})

public class COMMSTestSuite {   
    @BeforeClass
    public static void setUp() 
    {
        System.out.println("setting up");
        TestParameters.setDestination("EPS");
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("tearing down");
    }
}