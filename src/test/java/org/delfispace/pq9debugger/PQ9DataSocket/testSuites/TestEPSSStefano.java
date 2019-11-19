/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.IOException;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 *
 * @author LocalAdmin
 */
public class TestEPSSStefano
{
    protected final String subSystem = "EPS";
    protected final TestVarsMethods tm = new TestVarsMethods();
    
    @Test
    public void TestPing() throws IOException, ParseException, TimeoutException
    {
        tm.pingSubSystem(subSystem);
    }
     
    @Test
    public void TestReset() throws IOException, ParseException, TimeoutException
    {
        tm.resetSubSystem(subSystem, "Soft");
        tm.resetSubSystem(subSystem, "Hard");
        tm.resetSubSystem(subSystem, "PowerCycle");
    }
}
