/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
/**
 *
 * @author micha
 */
public class TaskList {
    private final Timer timer;
    private final PrintStream writer;
    private final TenmaDriver ps;
    private final static int TIMEOUT = 495; // in ms
    private static long starttime; 
    
    JSONObject command = new JSONObject();
    PQ9DataClient client = new PQ9DataClient("localhost", 10000);
           
 
    public TaskList(int seconds, String port) throws FileNotFoundException, IOException, InterruptedException 
    {
        Date start = new Date();
        starttime = start.getTime();
        writer = new PrintStream(new FileOutputStream("filename.txt", true));
        ps = new TenmaDriver(port);
        System.out.println(ps.ping());
        ps.setVoltage(4.20);
        ps.setCurrent(1.0);
        ps.sunUP();
        
        command.put("_send_", "GetTelemetry");
        command.put("Destination", "EPS");
        client.setTimeout(TIMEOUT);
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new ReminderOne(), 0, seconds * 1000);
        //timer.scheduleAtFixedRate(new ReminderTwo(), 500, seconds * 5000);

     
    }
  
    class ReminderOne extends TimerTask 
    {
        @Override
        public void run() 
        {
            try 
            {
                Date before = new Date();
                // inteeorgate power supply
                // * still to be defined. 
                // interrogate EPS
                StringBuilder sb = new StringBuilder();
                Date now = new Date(); //gets local computer time
                sb.append((now.getTime()-starttime) + ", " + ps.getVoltageAct()+ ", " + ps.getCurrentAct());
                //writer.flush();
                //System.out.format("Timer Task Finished..!%n");
           
                //Date before = new Date();
                client.sendFrame(command);

                try 
                {
                    Frame reply = client.getFrame2();
 
                    sb.append(", " + reply.get("BattVoltage").getValue() +", ");
                    sb.append(", " + reply.get("BattCapacity").getValue() +", ");
                   
                    Date after = new Date();
                    long delta = after.getTime() - before.getTime();
                    sb.append(", " + delta);
                    System.out.println(sb.toString());
                    writer.println(sb.toString());
                    writer.flush();
                } catch (TimeoutException ex) 
                {
                    // nothing to do here
                    writer.println();
                    ex.printStackTrace();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }   
            } catch (IOException ex) 
            {
                ex.printStackTrace();
                 //timer.cancel(); // Terminate the timer thread
                 //timer.cancel(); // Terminate the timer thread
            }
        }
    }
    
    /* class ReminderTwo extends TimerTask
    {
        int timesRun = 0;
        @Override
        public void run() 
        {
            double increment = 0.05;
            double voltage; 
            double maxvoltage = 4.2;
            double minvoltage = 4;
            voltage = (maxvoltage-minvoltage)/increment;
            int floor = (int)voltage;
            int overloop = 0;
            if (timesRun>floor*2){timesRun = timesRun-2*floor;}
            if (timesRun>floor){overloop = timesRun-floor;}
            
            try 
            {
              if(timesRun < 1){ps.setVoltage(minvoltage);}
              else{
                  voltage =  minvoltage + (timesRun - overloop*2)*increment;
                          //System.out.println("overloop = " + overloop);
                   
                  ps.setVoltage(voltage);
              }
            } catch (IOException ex) 
            {
                ex.printStackTrace();
                timer.cancel(); // Terminate the timer thread
                 //timer.cancel(); // Terminate the timer thread
            }
            timesRun++;
        }
    }*/
}
