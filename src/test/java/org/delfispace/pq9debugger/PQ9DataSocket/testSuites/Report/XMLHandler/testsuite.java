/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites.Report.XMLHandler;
import java.util.ArrayList;
import javax.xml.bind.annotation.*;


/**
 *
 * @author Michael van den Bos
 */

@XmlRootElement(name = "testsuite")

public class testsuite {
    @XmlElement(name = "properties")
    private ArrayList<property> properties;
    
    private ArrayList<testcase> testcases;
    
}
