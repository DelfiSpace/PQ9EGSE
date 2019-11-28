/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import javax.swing.JOptionPane;
import org.delfispace.pq9debugger.PQ9DataSocket.BK8500Driver;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TenmaDriver;
import org.json.simple.JSONObject;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCETMStream;

/**
 *
 * @author LocalAdmin
 */
interface TestClassInterface
{
    String comportTenma = "COM4";
    String comportBK = "COM10";
    
    String busIsOn = "{\"valid\":\"true\",\"value\":\"ON\"}";
    String busIsOff = "{\"valid\":\"true\",\"value\":\"OFF\"}";
    String servicePB = "{\"valid\":\"true\",\"value\":\"Execute\"}";

    String replyPB = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    String replyER = "{\"valid\":\"true\",\"value\":\"Error\"}";
    String replySH = "{\"valid\":\"true\",\"value\":\"\"}";
    
    static boolean askQuestionYESNO(String message)
    {
        boolean yesorno;
        //JOPtion pane stack
            int res = JOptionPane.showConfirmDialog(null,
            message,
            "Yes or NO?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.NO_OPTION)
                {
                    yesorno = false;
                }
                else 
                {
                    yesorno = true;
                }
    
        return yesorno;
    }
}
