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
    
    final String busIsOn = "{\"valid\":\"true\",\"value\":\"ON\"}";
    final String busIsOff = "{\"valid\":\"true\",\"value\":\"OFF\"}";
    final String servicePB = "{\"valid\":\"true\",\"value\":\"Execute\"}";

    final String replyPB = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    final String replyER = "{\"valid\":\"true\",\"value\":\"Error\"}";
    final String replySH = "{\"valid\":\"true\",\"value\":\"\"}";
    final String ResetReply  = "{\"valid\":\"true\",\"value\":\"Reply\"}";
    
    final String ResetSoft  ="{\"valid\":\"true\",\"value\":\"Soft\"}";
    final String ResetHard  ="{\"valid\":\"true\",\"value\":\"Hard\"}";
    final String ResetReplySize  = "{\"valid\":\"true\",\"value\":\"3\"}";
    final String ResetReplyDest = "{\"valid\":\"true\",\"value\":\"OBC\"}";
    final String COMMSSource  = "{\"valid\":\"true\",\"value\":\"COMMS\"}";
    final String ResetPC  ="{\"valid\":\"true\",\"value\":\"PowerCycle\"}";
    final String EPSSource  = "{\"valid\":\"true\",\"value\":\"EPS\"}"; 
    
    final int WAITREFRESH = 50;
    final String[] SUBSYSTEMS = {"EPS", "ADCS", "COMMS", "ABD"};
    
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
