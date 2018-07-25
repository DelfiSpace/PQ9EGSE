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
import static j2html.TagCreator.button;
import static j2html.TagCreator.dd;
import static j2html.TagCreator.div;
import static j2html.TagCreator.dl;
import static j2html.TagCreator.dt;
import static j2html.TagCreator.each;
import static j2html.TagCreator.fieldset;
import static j2html.TagCreator.label;
import static j2html.TagCreator.legend;
import static j2html.TagCreator.link;
import static j2html.TagCreator.textarea;
import j2html.tags.Tag;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
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
import org.delfispace.protocols.pq9.PQ9Receiver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEContainerEntryValue;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMStream;
import org.xtce.toolkit.XTCETelecommand;
import org.xtce.toolkit.XTCETelecommandContentModel;
import org.xtce.toolkit.XTCEValidRange;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Main implements PQ9Receiver, Subscriber
{
    private final String LOOPBACK_PORT_NAME = "Loopback";
    private final CommandWebServer srv;
    private PQ9PCInterface pcInterface = null; 
    private final JSONParser parser = new JSONParser(); 
    private XTCETMStream stream;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
    private boolean loopback = true;
    private SerialPort comPort = null;
    
    public static void main(String[] args) 
    {
        try 
        {
            Main m = new Main();
            m.start();
        } catch (Exception ex) 
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Main() throws Exception 
    {
        String file = "EPS.xml";
        try 
        {
            Configuration.getInstance().setXTCEDatabase( new XTCEDatabase(new File(file), true, true, true) );
            stream = Configuration.getInstance().getXTCEDatabase().getStream( "PQ9bus" );                          
        } catch (XTCEDatabaseException ex) 
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        SerialPort[] sp = SerialPort.getCommPorts();

        List<String> spl = new ArrayList();
        spl.add(LOOPBACK_PORT_NAME);
        for (SerialPort sp1 : sp) 
        {
            spl.add(sp1.getSystemPortName());
        }
        
        Configuration.getInstance().setSerialPorts(spl);
                        
        srv = new CommandWebServer(8080);                                    
    }
    
    public void start() throws Exception
    {
        // select default serial port
        connectToSerialPort(LOOPBACK_PORT_NAME);
        
        // connect GUI command handler
        srv.serReceptionHandler(this);  
        
        // start the server
        srv.start();
        srv.join();
    }
    
    private void connectToSerialPort(String port) throws IOException
    {
        if (comPort != null)
        {
            comPort.closePort();
            comPort = null;
        }

        if (pcInterface != null)
        {
            pcInterface.close();
        }

        if (port.equals(LOOPBACK_PORT_NAME))
        {                        
            loopback = true;
            // select loopback port
            LoopbackStream ls = new LoopbackStream();
            // crete the HLDLC reader
            pcInterface = new PQ9PCInterface(ls.getInputStream(), ls.getOutputStream(), loopback);        
        }
        else
        {
            loopback = false;
            // first time we connectot  a serial port            
            comPort = SerialPort.getCommPort(port);

            // open con port
            comPort.openPort();

            // configure the seriql port parameters
            comPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            
            // crete the HLDLC reader
            pcInterface = new PQ9PCInterface(comPort.getInputStream(), comPort.getOutputStream());
        }
        
        // setup an asynchronous callback on frame reception
        pcInterface.setReceiverCallback(this);  
        // setup an asynchronous callback on error
        pcInterface.setErrorHandler((error) -> { handleException(error); });
        // set the current serial port
        Configuration.getInstance().setSerialPort(port);
    }
    
    private void handleException(Throwable ex)
    {
        StringBuilder sb = new StringBuilder();
        // print reception time
        sb.append(df.format(new Date()));
        // print the received frame
        sb.append(" ");
        sb.append(ex.getMessage().replace("\n", "<br>").replace("\t", "&emsp;&emsp;&emsp;&emsp;"));
        srv.send(new Command("log", sb.toString())); 
    }
    
    private String processFrame(XTCETMStream stream, byte[] data) throws XTCEDatabaseException, Exception 
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
            sb.append("WARNING: ");
            sb.append(it.next());
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
    
    private boolean isWithinValidRange(XTCEContainerContentEntry entry)
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

    @Override
    public void received(PQ9 msg) 
    {
        StringBuilder sb = new StringBuilder();
            try
            {
                Date rxTime = new Date();                
                sb.append("<font color=\"black\">");
                // print reception time
                sb.append(df.format(rxTime));
                // print the received frame
                sb.append("New frame received: <br>");
                sb.append("&emsp;&emsp;&emsp;&emsp;");
                sb.append(msg.toString().replace("\n", "<br>").replace("\t", "&emsp;&emsp;&emsp;&emsp;"));
                sb.append("</font>");
                srv.send(new Command("datalog", sb.toString()));
                
                sb.setLength(0);
                sb.append("<font color=\"black\">");
                sb.append("&emsp;&emsp;&emsp;&emsp;Decoded frame: ");
                sb.append(processFrame(stream, msg.getFrame()).replace("\n", "<br>&emsp;&emsp;&emsp;&emsp;"));
                sb.append("</font>");
                srv.send(new Command("datalog", sb.toString()));
            } catch (XTCEDatabaseException ex)            
            {                
                handleException(ex);
            } catch (NullPointerException ex)            
            {                
                handleException(new Exception("Invalid XTCE file"));
            } 
            catch (Exception ex)
            {
                handleException(ex);
            }
    }

    @Override
    public void subscribe(Command cmd) 
    {
        
        try 
        {
            switch(cmd.getCommand())
            {
                case "SendCommand":
                    handleSendCommand(cmd);
                break;

                //case "sendCommand":
                  //  break;
                case "setSerialPort":
                    connectToSerialPort(cmd.getData());
                    break;

                case "ping":
                    // ignore ping commands, they are only used 
                    // to keep the websocket connection alive
                    break;

                case "uplink":
                    srv.send(new Command("uplink", UplinkTab.generate()));
                    break;
                default:
                    handleException(new Exception("Unknown command: " + cmd));
        }
        } catch (ParseException | PQ9Exception | XTCEDatabaseException | IOException ex) 
        {
            handleException(ex);
        }            
    }
    private void handleSendCommand(Command cmd) throws ParseException, PQ9Exception, IOException, XTCEDatabaseException
    {
        try
        {
            PQ9 frame = null;
            String data = cmd.getData();
            JSONObject obj = (JSONObject)parser.parse(data);
            switch((String)obj.get("_command_"))
            {
                case "SendRaw":                                        
                    int d = Integer.parseInt((String) obj.get("dest"));
                    int s = Integer.parseInt((String) obj.get("src"));

                    String[] parts = ((String) obj.get("data")).split(" ");
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    for (String part : parts) 
                    {
                        if (part.length() != 0) 
                        {                                
                            bs.write((byte) (Integer.decode(part) & 0xFF));
                        }
                    }
                    frame = new PQ9(d, s, bs.toByteArray());
                    break;
                    
                default:
                    List<XTCETelecommand> tc = Configuration.getInstance().getXTCEDatabase().getTelecommands((String)obj.get("_command_"));
                    
                    if (tc.isEmpty())
                    {
                        throw new XTCEDatabaseException("Command not found: " + (String)obj.get("_command_"));
                    }
                    if (tc.size() > 1)
                    {
                        throw new XTCEDatabaseException((String)obj.get("_command_") + " identifies multiple commands");
                    }

                    XTCETelecommandContentModel model =
                            Configuration.getInstance().getXTCEDatabase().processTelecommand( tc.get(0), null, false );
                    BitSet rawBits    = model.encodeContainer();
                    byte[] rawcmd = rawBits.toByteArray();
                    byte[] rawcmd1 = new byte[rawcmd.length];
                    
                    // invert bit order
                    for(int i = 0; i < rawcmd.length; i++)
                    {
                        rawcmd1[i] = 0;
                        for(int j = 0; j < 8; j++)
                        {
                            if ((rawcmd[i] & (1 << j)) != 0)
                            {
                                rawcmd1[i] |= 1 << (7 - j);
                            }
                        }
                    }

                    frame = new PQ9(rawcmd1[0] & 0xFF, rawcmd1[2] & 0xFF, Arrays.copyOfRange(rawcmd1, 3, rawcmd.length));
                    break;
            }
            
            pcInterface.send(frame);
            if (!loopback)
            {
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
            }            
        } catch (NumberFormatException ex)
        {
            handleException(ex);
        } 
    }
}
