/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataClient;
import org.delfispace.pq9debugger.PQ9DataSocket.TenmaDriver;
import org.delfispace.pq9debugger.PQ9DataSocket.TimeoutException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author LocalAdmin
 */
public class BatteryRun {
    private final Timer timer;
    private final PrintStream writer;
    private final TenmaDriver ps;
    private final static int TIMEOUT = 495; // in ms
    private static long starttime; 
    private double capacityCalc;
    private double capacityMeas;
    
    JSONObject command = new JSONObject();
    JSONObject command0 = new JSONObject();
    JSONObject command1 = new JSONObject();
    JSONObject command2 = new JSONObject();
    JSONObject reply = new JSONObject();
    PQ9DataClient client = new PQ9DataClient("localhost", 10000);
    Grapher report;
    boolean flipper;
           
 
    public BatteryRun(int seconds, String port, String filename, double currentLim, Grapher visualOut) throws FileNotFoundException, IOException, InterruptedException 
    {
        Date start = new Date();
        starttime = start.getTime();
        writer = new PrintStream(new FileOutputStream(filename, true));
        ps = new TenmaDriver(port);
        System.out.println(ps.ping());
        ps.setVoltage(4.05);
        ps.setCurrent(currentLim);
        ps.sunUP();
        this.report = visualOut; 
        client.setTimeout(TIMEOUT);
        command.put("Destination", "EPS");
        command.put("_send_", "GetTelemetry");
        timer = new Timer();
        flipper = false;
        timer.scheduleAtFixedRate(new ReminderOne(), 0, seconds * 1000);
    }
  
    class ReminderOne extends TimerTask 
    {
        @Override
        public void run() 
        {
            try 
            {
                Date before = new Date();
                StringBuilder sb = new StringBuilder();
                Date now = new Date(); //gets local computer time
                sb.append(now.getTime()-starttime).append(", ").append(ps.getVoltageAct()).append(", ").append(ps.getCurrentAct());
                client.sendFrame(command);

                try 
                {
                    PQ9JSONObjectInterpreter intep = new PQ9JSONObjectInterpreter(client.getFrame());
                    sb.append(", ").append(intep.getDoubleFromSubKey("BattVoltage","value"));
                    sb.append(", ").append(intep.getDoubleFromSubKey("BattCapacity","value"));
                    sb.append(", ").append(intep.getDoubleFromSubKey("URBCurrent","value"));
                    Date after = new Date();
                    long delta = after.getTime() - before.getTime();
                    sb.append(", ").append(delta);
                    System.out.println(sb.toString());
                    writer.println(sb.toString());
                    writer.flush();
                    capacityCalc = capacityCalc + 1000*ps.getCurrentAct()/3600;
                    capacityMeas = intep.getDoubleFromSubKey("BattCapacity","value")*0.085*50/33*16/128;
                    report.AddDataToSet(1,intep.getDoubleFromSubKey("BattVoltage","value"),(int)(now.getTime()-starttime));
                    report.AddDataToSet(2,intep.getDoubleFromSubKey("BattCapacity","value"),(int)(now.getTime()-starttime));
                    report.AddDataToSet(3,capacityCalc,(int)(now.getTime()-starttime));
                    if(flipper)
                    {
                        flipper = false;
                        report.UpdateChart();
                    }
                    else
                    {
                        flipper = true;
                    }
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
}
