/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Michael van den Bos
 */

//This class should be used to retrieve values from a JSON Object in the PQ9 Tests.
//It should be constructed with a JSONObject

public class PQ9JSONObjectInterpreter {
    private final JSONObject x;
    
    public PQ9JSONObjectInterpreter(JSONObject toBeInterpreted)
    {
        this.x = toBeInterpreted; 
    }
    
    public double getDoubleFromKey(String key)
    {
        return Double.valueOf(x.get(key).toString());
    }
    
    public double getDoubleFromSubKey(String key, String subKey) throws ParseException
    {
        JSONObject tempJSON = stringToJSON(x.get(key).toString()) ;
        return Double.valueOf(tempJSON.get(subKey).toString());
    }
    public int getIntFromKey(String key)
    {
        return Integer.valueOf(x.get(key).toString());
    }
    
    public int getIntFromSubKey(String key, String subKey) throws ParseException
    {
        JSONObject tempJSON = stringToJSON(x.get(key).toString()) ;
        return Integer.valueOf(tempJSON.get(subKey).toString());
    }
    public String getStringFromKey(String key)
    {
        return x.get(key).toString();
    }
    
    public String getStringFromSubKey(String key, String subKey) throws ParseException
    {
        JSONObject tempJSON = stringToJSON(x.get(key).toString()) ;
        return tempJSON.get(subKey).toString();
    }

    protected JSONObject stringToJSON(String tobreak) throws ParseException
    {  
        JSONParser parser = new JSONParser();
        JSONObject tempJSON = (JSONObject) parser.parse(tobreak);
        return tempJSON;
    }
}
