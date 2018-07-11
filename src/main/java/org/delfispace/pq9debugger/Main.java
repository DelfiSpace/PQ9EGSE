/*
 * Copyright (C) 2018 Stefano Speretta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.delfispace.pq9debugger;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.delfispace.CommandWebServer.Command;
import org.delfispace.CommandWebServer.CommandWebServer;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.delfispace.protocols.pq9.PQ9PCInterface;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEContainerEntryValue;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMStream;
import org.xtce.toolkit.XTCEValidRange;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Main 
{
    private static CommandWebServer srv;
    private static final JSONParser parser = new JSONParser(); 
    private static XTCETMStream stream;
    
    public static void main(String[] args) throws UnsupportedEncodingException, IOException, PQ9Exception, Exception
    {
        String file = "EPS.xml";
        XTCEDatabase db_ = new XTCEDatabase(new File(file), true, false, true);
        stream = db_.getStream( "PQ9bus" );
       
        if (args.length < 1)
        {
            System.out.println("Usage: java -jar PQ9Debugger comport");
            return;
        }
        
        // create a fast console writer to avoid slowing down the bus 
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new
        FileOutputStream(java.io.FileDescriptor.out), "ASCII"), 1024);
        
        // define the date format
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
        
        SerialPort comPort = SerialPort.getCommPort(args[0]);

        // open con port
        comPort.openPort();
        
        // configure the seriql port parameters
        comPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
                                       
        // crete the HLDLC reader
        PQ9PCInterface p = new PQ9PCInterface(comPort.getInputStream(), comPort.getOutputStream());
        
        // setup an asynchronous callback on frame reception
        p.setReceiverCallback((PQ9 msg) -> {
            try
            {
                StringBuilder sb = new StringBuilder();
                sb.append("<font color=\"black\">");
                // print reception time
                sb.append(df.format(new Date()));
                // print the received frame
                sb.append("New frame received: <br>");
                sb.append("&emsp;&emsp;&emsp;&emsp;");
                sb.append(msg.toString().replace("\n", "<br>").replace("\t", "&emsp;&emsp;&emsp;&emsp;"));
                sb.append("<br>&emsp;&emsp;&emsp;&emsp;");
                sb.append(processFrame(stream, msg.getFrame()).replace("\n", "<br>&emsp;&emsp;&emsp;&emsp;"));
                sb.append("</font>");
                srv.send(new Command("datalog", sb.toString()));
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
        
        srv = new CommandWebServer(8080);
        srv.serReceptionHandler((Command cmd) -> {
            String data = cmd.getData();
            JSONObject obj;
            try {
                obj = (JSONObject)parser.parse(data);
                int d = Integer.parseInt((String) obj.get("dest"));
                int s = Integer.parseInt((String) obj.get("src"));
                
                String[] parts = ((String) obj.get("data")).split(" ");
                byte[] n1 = new byte[parts.length];
                for(int n = 0; n < parts.length; n++) 
                {
                   n1[n] = (byte)Integer.parseInt(parts[n]);
                }
                PQ9 frame = new PQ9(d, s, n1);
                p.send(frame);
                StringBuilder sb = new StringBuilder();
                sb.append("<font color=\"yellow\">");
                // print reception time
                sb.append(df.format(new Date()));
                // print the received frame
                sb.append("New frame transmitted: <br>");
                sb.append("&emsp;&emsp;&emsp;&emsp;");
                sb.append(frame.toString().replace("\n", "<br>").replace("\t", "&emsp;&emsp;&emsp;&emsp;")); 
                sb.append("</font>");
                srv.send(new Command("datalog", sb.toString()));                
            } catch (ParseException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PQ9Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }                        
        });
        
        srv.start();
        srv.join();                            
    }
    
    static String processFrame(XTCETMStream stream, byte[] data) throws XTCEDatabaseException, Exception 
    {
        StringBuilder sb = new StringBuilder();
        
        XTCEContainerContentModel model = stream.processStream( data );
 
        List<XTCEContainerContentEntry> entries = model.getContentList();

        for (XTCEContainerContentEntry entry : entries) 
        {
            sb.append(entry.getName());
            
            XTCEContainerEntryValue val = entry.getValue();

            if (val == null) 
            {
                sb.append("\n");
            } else 
            {
                sb.append(": " + val.getCalibratedValue() + " "
                        + entry.getParameter().getUnits() + " ("
                        + val.getRawValueHex()+ ")");

                if (!isWithinValidRange(entry))
                {
                    sb.append(" INVALID!");
                    sb.append("\n");
                }
                else
                {
                    sb.append("\n");
                }
            }
        }
        List<String> warnings = model.getWarnings();
        Iterator<String> it = warnings.iterator();
        while(it.hasNext())
        {
            sb.append("WARNING: " + it.next());
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
    
    static private boolean isWithinValidRange(XTCEContainerContentEntry entry)
    {
        XTCEValidRange range = entry.getParameter().getValidRange();
        if (!range.isValidRangeApplied()) {
            return true;
        } else {
            String valLow =  range.isLowValueCalibrated() ? 
                    entry.getValue().getCalibratedValue() : 
                    entry.getValue().getUncalibratedValue();

            if (range.isLowValueInclusive()) {
                if (Double.parseDouble(valLow) < Double.parseDouble(range.getLowValue())) {
                    return false;
                }
            } else {
                if (Double.parseDouble(valLow) <= Double.parseDouble(range.getLowValue())) {
                    return false;
                }
            }
            
            String valHigh =  range.isHighValueCalibrated() ? 
                    entry.getValue().getCalibratedValue() : 
                    entry.getValue().getUncalibratedValue();
            
            if (range.isHighValueInclusive()) {
                if (Double.parseDouble(valHigh) > Double.parseDouble(range.getHighValue())) {
                    return false;
                }
            } else {
                if (Double.parseDouble(valHigh) >= Double.parseDouble(range.getHighValue())) {
                    return false;
                }
            }
        }
        return true;
    }
}
