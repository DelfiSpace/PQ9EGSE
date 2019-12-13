/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Michael van den Bos
 */

//This class should be used to check a JSON Object for/from PQ9 for certain values. 
//It should be constructed with a JSONObject
//This should make it much more clear what is tested for in the Test classes.
//This class makes use of the PQ9JSONObjectInterpreter
public class PQ9JSONObjectChecker {
    private JSONObject x;
    private PQ9JSONObjectInterpreter y;
    
    public PQ9JSONObjectChecker(JSONObject toBeChecked)
    {
        this.x = toBeChecked;
        this.y = new PQ9JSONObjectInterpreter(x);
    }
    public boolean keyContainsDouble(String key, double target)
    {
        if(target == y.getDoubleFromKey(key)){return true;}
        else{return false;}
    }
    public boolean keyContainsDouble(String mainkey, String subkey, double target) throws ParseException
    {
        if(target == y.getDoubleFromSubKey(mainkey, subkey)){return true;}
        else{return false;}
    }
    public boolean keyContainsInt(String key, int target)
    {
        if(target == y.getIntFromKey(key)){return true;}
        else{return false;}
    }
    public boolean keyContainsInt(String mainkey, String subkey, int target) throws ParseException
    {
        if(target == y.getIntFromSubKey(mainkey, subkey)){return true;}
        else{return false;}
    }    
    public boolean keyContainsString(String key, String target)
    {
        //System.out.println(y.getStringFromKey(key));
        if(target.equals(y.getStringFromKey(key))){return true;}
        else{return false;}
    }
    public boolean keyContainsString(String mainkey, String subkey, String target) throws ParseException
    {
        if(target.equals(y.getStringFromSubKey(mainkey, subkey))){return true;}
        else{return false;}
    }   
    public boolean checkPingReply() throws ParseException
    {
        System.out.println(x.get("Request").toString());
        System.out.println(x.get("_received_").toString());
        System.out.println(x.get("Size").toString());
        System.out.println(x.get("Service").toString());
        System.out.println("Request");
        System.out.println(keyContainsString("Request", "value", "Reply") );
        System.out.println("_received_");
        System.out.println(keyContainsString("_received_", "PingService") );
        System.out.println("Size");
        System.out.println(keyContainsInt("Size", "value", 2) );
        System.out.println("Service");
        System.out.println(keyContainsString("Service", "Ping"));
        
        if
        (
            keyContainsString("Request", "value", "Reply") &&
            keyContainsString("_received_", "PingService") &&
            keyContainsInt("Size", "value", 2) &&
            keyContainsString("Service", "value", "Ping")
        )
        {
            return true;
        }
        else
        {
            return false;
        }
    }        
    
}
