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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.delfispace.CommandWebServer.Command;
import org.delfispace.CommandWebServer.CommandWebServer;
import org.delfispace.pq9debugger.PQ9DataSocket.PQ9DataSocket;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.delfispace.protocols.pq9.PQ9PCInterface;
import org.delfispace.protocols.pq9.PQ9Receiver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xtce.toolkit.XTCEArgument;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEContainerEntryValue;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCETMStream;
import org.xtce.toolkit.XTCETelecommand;
import org.xtce.toolkit.XTCETelecommandContentModel;
import org.xtce.toolkit.XTCETypedObject;
import org.xtce.toolkit.XTCETypedObject.EngineeringType;
import org.xtce.toolkit.XTCEValidRange;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class Main implements PQ9Receiver, Subscriber
{
    private static final String NULL_PORT_NAME = " ";
    private static final String XTCE_FILE = "EPS.xml";
    private final CommandWebServer srv;
    private final PQ9DataSocket DatSktSrv;
    private PQ9PCInterface pcInterface = null; 
    private final JSONParser parser = new JSONParser(); 
    private XTCETMStream stream;
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS ");
    private SerialPort comPort = null;
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) 
    {
        try 
        {
            Main m = new Main((args.length < 1) ? NULL_PORT_NAME : args[0]);
            m.start();
        } catch (Exception ex) 
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    
    /** Main PQ9 Debugger class: it controls the whole software.
     *
     * @param defaultSerialPort
     * @throws Exception
     */
    public Main(String defaultSerialPort) throws Exception 
    {
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "PQ9 EGSE started");
        
        try 
        {
            loadXTCEFile(XTCE_FILE);
        } catch (XTCEDatabaseException ex) 
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        enumerateSerialPorts(defaultSerialPort);
        
        srv = new CommandWebServer(8080);         
        DatSktSrv = new PQ9DataSocket(10000);
    }
    
    /**
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "Using serial port {0}", 
                Configuration.getInstance().getSerialPort());
                    
        // select default serial port
        connectToSerialPort(Configuration.getInstance().getSerialPort());
        
        // connect GUI command handler
        srv.serReceptionHandler(this);  
        
        // connect the test socket to the command handler
        DatSktSrv.setCommandHandler(this);
        
        // start the server
        srv.start();
        DatSktSrv.start();
        
        srv.join();        
    }
    
    private void loadXTCEFile(String file) throws XTCEDatabaseException
    {
        // first de-allocate the previous instance (in case it exists)
        Configuration.getInstance().setXTCEDatabase(null);
        // now create a new instance
        Configuration.getInstance().setXTCEDatabase( new XTCEDatabase(new File(file), true, true, true) );
        stream = Configuration.getInstance().getXTCEDatabase().getStream( "PQ9bus" );  
        if (Configuration.getInstance().getXTCEDatabase().getErrorCount() != 0)
        {
            Configuration.getInstance().getXTCEDatabase().getDocumentWarnings().forEach((item) -> 
            {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "XML parsing error: {0}", item);
            });
        }
    }
    private void enumerateSerialPorts(String defaultSerialPort)
    {
        SerialPort[] sp = SerialPort.getCommPorts();

        List<String> spl = new ArrayList();
        spl.add(NULL_PORT_NAME);
        for (SerialPort sp1 : sp) 
        {
            spl.add(sp1.getSystemPortName());
        }
        
        // make sure the provided serial port exists, if not terminate the application
        if (!spl.contains(defaultSerialPort))
        {
            handleException(new Exception("Unknown default serial port: " + defaultSerialPort));
            defaultSerialPort = NULL_PORT_NAME;
        }
        
        Configuration.getInstance().setSerialPorts(spl);
        // set the default serial port
        Configuration.getInstance().setSerialPort(defaultSerialPort);
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

        if (port.equals(NULL_PORT_NAME))
        {                        
            // crete the HLDLC reader
            pcInterface = new PQ9PCInterface(new NullInputStream(), new NullOutputStream());        
        }
        else
        {
            // first time we connectot  a serial port            
            comPort = SerialPort.getCommPort(port);

            // open con port
            comPort.openPort();

            // configure the seriql port parameters
            comPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            
            // set the serial port in blocking mode
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            
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
    
    private String processFrame(XTCETMStream stream, byte[] data, HashMap<String, String> values) throws XTCEDatabaseException, Exception 
    {
        StringBuilder sb = new StringBuilder();
        
        XTCEContainerContentModel model = stream.processStream( data );        
        List<XTCEContainerContentEntry> entries = model.getContentList();

        values.put("_received_", model.getName());
        sb.append(model.getName());
        sb.append("\n");
        
        entries.forEach((XTCEContainerContentEntry entry) -> 
        {
            XTCEContainerEntryValue val = entry.getValue();
            if (val != null) 
            {          
                String value;
                if (entry.getParameter().getEngineeringType() == EngineeringType.FLOAT32)
                {
                    value = Float.valueOf(val.getCalibratedValue()).toString();
                }
                else if (entry.getParameter().getEngineeringType() == EngineeringType.FLOAT64)
                {
                    value = Double.valueOf(val.getCalibratedValue()).toString();
                }
                else
                {
                    value = val.getCalibratedValue();
                }
                
                JSONObject obj=new JSONObject();
                obj.put("value", value);
                obj.put("valid", "true");
                
                
                sb.append("\t");
                sb.append(entry.getParameter().getShortDescription().isEmpty() ? entry.getName() : entry.getParameter().getShortDescription());
                sb.append(": ");
                
               
                sb.append(val.getCalibratedValue());
                sb.append(" ");
                sb.append(entry.getParameter().getUnits());
                sb.append(" (");
                sb.append(val.getRawValueHex());
                sb.append(")");

                if (!isWithinValidRange(entry))
                {
                    sb.append(" INVALID!");
                    sb.append("\n");
                    obj.put("valid", "false");
                }
                else
                {
                    sb.append("\n");
                    obj.put("valid", "true");
                }
                
                values.put(entry.getName(), obj.toJSONString());
            }
        });
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
        if (!range.isValidRangeApplied()) 
        {
            return true;
        } else 
        {
            String valLow =  range.isLowValueCalibrated() ? 
                    entry.getValue().getCalibratedValue() : 
                    entry.getValue().getUncalibratedValue();

            if (range.isLowValueInclusive()) 
            {
                if (Double.parseDouble(valLow) < Double.parseDouble(range.getLowValue())) 
                {
                    return false;
                }
            } else 
            {
                if (Double.parseDouble(valLow) <= Double.parseDouble(range.getLowValue())) 
                {
                    return false;
                }
            }
            
            String valHigh =  range.isHighValueCalibrated() ? 
                    entry.getValue().getCalibratedValue() : 
                    entry.getValue().getUncalibratedValue();
            
            if (range.isHighValueInclusive()) 
            {
                if (Double.parseDouble(valHigh) > Double.parseDouble(range.getHighValue())) 
                {
                    return false;
                }
            } else 
            {
                if (Double.parseDouble(valHigh) >= Double.parseDouble(range.getHighValue())) 
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void newFrameToGUI(PQ9 msg, Date time, boolean received) throws XTCEDatabaseException, Exception
    {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> data = new HashMap<>();
        
        byte[] frame = msg.getFrame();

        StringBuilder sb1 = new StringBuilder();
        sb1.append("[");
        for (int i = 0; i < frame.length; i++)
        {
            sb1.append(frame[i] & 0xFF);
            if (i < frame.length - 1)
            {
                sb1.append(", ");
            }
        }
        sb1.append("]");
        data.put("_raw_", sb1.toString());
        data.put("_timestamp_", time.toString());
        
        if (received)
        {
            sb.append("<font color=\"black\">");
        }
        else
        {
            sb.append("<font color=\"yellow\">");
        }
        // print time
        sb.append(df.format(time));
        // print the received frame
        if (received)
        {
            sb.append("New frame received: <br>");
        }
        else
        {
            sb.append("New frame transmitted: <br>");
        }
        sb.append("&emsp;&emsp;&emsp;&emsp;");
        sb.append(msg.toString().replace("\n", "<br>").replace("\t", "&emsp;&emsp;&emsp;&emsp;"));
        sb.append("</font>");
        //srv.send(new Command("datalog", sb.toString()));

        sb.setLength(0);
        if (received)
        {
            sb.append("<font color=\"black\">");
        }
        else
        {
            sb.append("<font color=\"yellow\">");
        }
        sb.append("&emsp;&emsp;&emsp;&emsp;Decoded frame: ");
        sb.append(processFrame(stream, msg.getFrame(), data).replace("\n", "<br>&emsp;&emsp;&emsp;&emsp;").replace("\t", "&emsp;&emsp;&emsp;&emsp;"));
        sb.append("</font>");
        //srv.send(new Command("datalog", sb.toString()));  
        JSONObject obj=new JSONObject();
        data.forEach((k,v)->obj.put(k,v));
        srv.send(new Command("downlink", obj.toJSONString() + "\n"));  

        if (received)
        {
            DatSktSrv.send(data);
        }
    }
    /**
     * Handle a frame received on the serial port
     * 
     * @param msg PQ9 message received on the serial port
     */
    @Override
    public void received(PQ9 msg) 
    {        
        Date rxTime = new Date();                                 
        
        try
        {
            newFrameToGUI(msg, rxTime, true);
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

    /**
     * Handle external commands received from the GUI
     * 
     * @param cmd Command object received from the GUI
     */
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

                case "setSerialPort":
                    connectToSerialPort(cmd.getData());
                    // TODO: update the header for all existing conenctions
                    break;
                    
                case "reloadSerialPorts":
                    enumerateSerialPorts(Configuration.getInstance().getSerialPort());
                    srv.send(new Command("header", HeaderTab.generate()));
                    break;

                case "reloadXTCEFile":
                    // reload the XTCE file
                    loadXTCEFile(XTCE_FILE);
                    // force an update in the Uplink panel
                    srv.send(new Command("uplink", UplinkTab.generate()));
                    srv.send(new Command("downlinkgui", DownlinkTab.generate()));
                    break;
                    
                case "ping":
                    // ignore ping commands, they are only used 
                    // to keep the websocket connection alive
                    break;

                case "uplink":
                    srv.send(new Command("uplink", UplinkTab.generate()));
                    break;
                    
                case "downlinkgui":
                    srv.send(new Command("downlinkgui", DownlinkTab.generate()));                    
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
            PQ9 frame;
            String data = cmd.getData();
            JSONObject obj = (JSONObject)parser.parse(data);
            switch((String)obj.get("_send_"))
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
                    List<XTCETelecommand> tc = Configuration.getInstance().getXTCEDatabase().getTelecommands((String)obj.get("_send_"));
                    List<XTCEContainerEntryValue> values = new ArrayList<>();
                    
                    if (tc.isEmpty())
                    {
                        throw new XTCEDatabaseException("Command not found: " + (String)obj.get("_send_"));
                    }
                    if (tc.size() > 1)
                    {
                        throw new XTCEDatabaseException((String)obj.get("_send_") + " identifies multiple commands");
                    }                                          
                    obj.remove("_send_");

                    obj.keySet().forEach((key) -> 
                    {
                        try 
                        {                                                
                            XTCEArgument a = tc.get(0).getArgument((String)key);
                            XTCEContainerEntryValue valueObj =
                                    new XTCEContainerEntryValue( a,
                                            (String)obj.get((String)key),
                                            "==",
                                            "Calibrated" );
                            values.add(valueObj);
                        } catch (XTCEDatabaseException ex)
                        {
                            // ignore this error: it means that the key was not 
                            // found in the command definition
                        }
                    });
                    XTCETelecommandContentModel model =
                            Configuration.getInstance().getXTCEDatabase().processTelecommand( tc.get(0), values, false );

                    BitSet rawBits    = model.encodeContainer();
                    long   sizeInBits = model.getTotalSize();
                    
                    byte[] rawcmd = XTCEFunctions.getStreamByteArrayFromBitSet(rawBits, (int)Math.ceil((float)sizeInBits / 8));                 
                    frame = new PQ9(rawcmd[0] & 0xFF, rawcmd[2] & 0xFF, Arrays.copyOfRange(rawcmd, 3, rawcmd.length));
                    break;
            }
            pcInterface.send(frame);
            
            newFrameToGUI(frame, new Date(), false);
                     
        } catch (java.lang.NumberFormatException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, 
                    String.format("Invalid value: %s", cmd.toString()), ex);
            handleException(ex);
        } catch (Exception ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, 
                    String.format("Invalid value: %s", cmd.toString()), ex);
            handleException(ex);
        } 
    }
}
