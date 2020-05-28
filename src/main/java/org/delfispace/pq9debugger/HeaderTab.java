/*
 * Copyright (C) 2019 Stefano Speretta
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
 *
 */
package org.delfispace.pq9debugger;

import static j2html.TagCreator.button;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.option;
import static j2html.TagCreator.select;
import static j2html.TagCreator.span;
import j2html.tags.Tag;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author stefanosperett
 */
public class HeaderTab 
{
    public static String generate()
    {
        try
        {
            List<String> spl = Configuration.getInstance().getSerialPorts();
            List<String> modes = Arrays.asList(new String[]{"PQ9", "RS485"});
            
            Tag t1 = div
            (                
                div(), 
                div
                (
                    span("Mode: "),
                    select
                    (
                        each(modes, p -> option(p).withValue(p).condAttr(Configuration.getInstance().getEGSEMode().equals(p), "selected",  "selected"))
                    ).withId("EGSEMode").attr("onChange", "setMode();")
                ),
                div
                (
                    span("Serial port: "),
                    select
                    (
                        each(spl, p -> option(p).withValue(p).condAttr(Configuration.getInstance().getSerialPort().equals(p), "selected",  "selected"))
                    ).withId("serialPort").attr("onChange", "setSerialPort();")
                ),
                div
                (
                    button("Reload Serial ports").attr("onclick", "reloadSerialPorts();")                    
                ),
                div
                (
                    button("Reload XTCE File").attr("onclick", "reloadXTCEFile();")
                ),
                div
                (
                    button("Reset Layout").attr("onclick", "resetLayout();")
                ),
                div
                (
                    button("Reset EGSE").attr("onclick", "resetEGSE();")
                ),
                div()
            ).attr("style","display: flex; justify-content: space-between;");
            return t1.render();
        } catch (Exception ex)
        {
            // make sure that, in case of exceptions, the message is printed out
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);  
            return sw.toString();
        }
    }
}
