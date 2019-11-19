/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

/**
 *
 * @author LocalAdmin
 */
public class TestParameters 
{
    private static String destination;
    public final static int TIMEOUT = 500; 
    
    //List of subSystems
    public static final String[] SUBSYSTEMS = {"EPS", "COMMS", "ADCS", "ABD"};
    
    //responses
    private static final String replyER = "{\"valid\":\"true\",\"value\":\"Error\"}";
    
    static String getDestination()
    {
        return destination;
    }
    
    static void setDestination(String dest)
    {
        destination = dest;
    }
    static String getExpectedReply(String service)
    {
        switch(service){
            case "Ping":
                return "{\"valid\":\"true\",\"value\":\"Ping\"}";    
            case "Request":
                return "{\"valid\":\"true\",\"value\":\"Reply\"}";
            case "SetPowerBus":
                return "{\"valid\":\"true\",\"value\":\"Execute\"}";
            case "Source":
                switch(destination)
                {
                    case "EPS":
                       return "{\"valid\":\"true\",\"value\":\"EPS\"}";
                    case "COMMS":
                       return "{\"valid\":\"true\",\"value\":\"COMMS\"}";
                    case "ADCS":   
                       return "{\"valid\":\"true\",\"value\":\"ADCS\"}";
                    case "ABD":   
                        return "{\"valid\":\"true\",\"value\":\"ABD\"}";
                    case "OBC":   
                        return "{\"valid\":\"true\",\"value\":\"OBC\"}";
                    default: 
                        return replyER;
                }
            default:
                return replyER;
        } 
    }
    public static boolean isKnown(String x){
        for(String s: SUBSYSTEMS){
            if(!x.equals(s)) {
            } else {
                return true;
            }
        }
        return false;
    } 
}
