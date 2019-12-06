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
        PingTestValidParameters.class,
        PingTestInvalidParameters.class,
        EPSBusHandlingTest.class,
        ResetTestValidParameters.class,
        //TestInvalidService.class,
        //EPSPowerSupplyTests.class,
})

public class EPSTestSuite {   
    @BeforeClass
    public static void setUp() 
    {
        System.out.println("setting up");
        TestParameters.setDestination("EPS");
             System.out.print(" Testing :" );
        System.out.println(TestParameters.getDestination());
        System.out.println(TestParameters.getDestinationInt());
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("tearing down");
    }
}
