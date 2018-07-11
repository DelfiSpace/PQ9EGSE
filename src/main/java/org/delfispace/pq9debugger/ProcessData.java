/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import static org.delfispace.pq9debugger.Main.processFrame;
import org.delfispace.protocols.pq9.PQ9Exception;
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
public class ProcessData 
{

    public static void main(String[] args) throws UnsupportedEncodingException, IOException, PQ9Exception, Exception
    {
        byte[] tdata = new byte[] {(byte)0x01, (byte)0x4B, (byte)0x02, (byte)0x03, 
            (byte)0x02, (byte)0x02, (byte)0xFE, (byte)0xCA, (byte)0xAD, (byte)0xDE, 
            (byte)0xEF, (byte)0xBE, (byte)0xAD, (byte)0xDE, (byte)0x00, (byte)0x20, 
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x24, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x3A, 
            (byte)0x0A, (byte)0x3A, (byte)0x0A, (byte)0x3A, (byte)0x0A, (byte)0x3C, 
            (byte)0x0A, (byte)0x17, (byte)0xB7, (byte)0x00, (byte)0x00, (byte)0x40, 
            (byte)0x2C, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x44, (byte)0x00, 
            (byte)0x5C, (byte)0x09, (byte)0x71, (byte)0x00, (byte)0x3A, (byte)0x0A, 
            (byte)0xFE, (byte)0xCA, (byte)0x56, (byte)0x46};
        
        String file = "EPS.xml";
        XTCEDatabase db_ = new XTCEDatabase(new File(file), true, false, true);
        XTCETMStream stream = db_.getStream( "PQ9bus" );
        
        System.out.println(processFrame(stream, tdata));
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
