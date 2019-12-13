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
 * @author Michael van den Bos
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        RadioTransmissionTest.class,
        /*
        genericLowVoltageTest.class,
        PingTestValidParameters.class,
        PingTestInvalidParameters.class,
        TestInvalidService.class,
        ResetTestValidParameters.class,
        */
})

public class COMMSTestSuite {   
    @BeforeClass
    public static void setUp() 
    {
        System.out.println("setting up");
        TestParameters.setDestination("COMMS");
        System.out.print("Testing :" );
        System.out.print(TestParameters.getDestination());
         System.out.print(" Board adress is : " );
        System.out.println(TestParameters.getDestinationInt());
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("tearing down");
    }
}