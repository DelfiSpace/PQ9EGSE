/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/**
 *
 * @author LocalAdmin
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        PingTestCase1.class,
        PingTestCase2.class,
        PingTestCase3.class,
        PingTestCase4.class
})

public class PingTestSuite {    
}
