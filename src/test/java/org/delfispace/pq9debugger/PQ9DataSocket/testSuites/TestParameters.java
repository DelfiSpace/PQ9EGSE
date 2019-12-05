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
    private static int TIMEOUT = 500; 
    
    //List of subSystems
    public static final String[] SUBSYSTEMS = {"EPS", "ADCS", "COMMS", "ABD"};
    
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
    
     static int getTimeOut()
    {
        return TIMEOUT;
    }
    
    static void setTimeOut(int TimeOut)
    {
        TIMEOUT = TimeOut;
    }
    
    static int getDestinationInt()
    {
        int destint;
        destint = 0;
        int runner = 0;
        do{
            System.out.println("runner");
            System.out.println(SUBSYSTEMS[runner]);
                System.out.println(runner);
            if (destination.equals(SUBSYSTEMS[runner]))
            {
                
                destint = 2+runner;
            }
            runner++;
        }while(destint == 0 && runner < SUBSYSTEMS.length);
        return destint;
    }
    static int getDestinationInt(String dest)
    {
        int destint;
        destint = 0;
        int runner = 0;
        do{
            if (dest.equals(SUBSYSTEMS[runner]))
            {
                destint = 2+runner;
            }
        }while(destint == 0 && runner < SUBSYSTEMS.length);
        return destint;
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
