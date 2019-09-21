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

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.dd;
import static j2html.TagCreator.div;
import static j2html.TagCreator.dt;
import static j2html.TagCreator.each;
import static j2html.TagCreator.fieldset;
import static j2html.TagCreator.filter;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;
import static j2html.TagCreator.legend;
import static j2html.TagCreator.link;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.tr;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.omg.space.xtce.MetaCommandType;
import org.omg.space.xtce.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList.ArgumentAssignment;
import org.xtce.toolkit.XTCEArgument;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMContainer;
import org.xtce.toolkit.XTCETMStream;
import org.xtce.toolkit.XTCETelecommand;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class DownlinkTab 
{
    private static int tabIndex = 0;
    private static final StringBuilder IDARRAY = new StringBuilder();
    private static final List<String> IDS = new ArrayList();
    
    public static String generate() 
    {
        try
        {
            List<String[]> numbers = new ArrayList();
            numbers.add(new String[]{"raw:dest", "Destination", "7"});
            numbers.add(new String[]{"raw:src", "Source", "1"});

            tabIndex = 0;
            IDARRAY.setLength(0);

            List<XTCETMStream> ls = Configuration.getInstance().getXTCEDatabase().getStreams();
            System.out.println("Size " + ls.size());
            Iterator<XTCETMStream> streamItrator = ls.iterator();
 
List<XTCEContainerContentModel> tlm = new LinkedList();
while(streamItrator.hasNext()) 
{
    XTCETMStream s = streamItrator.next();
    System.out.println("Stream: " + s.getName());
    List<XTCETMContainer> c = s.getContainers();
    Iterator<XTCETMContainer> ic = c.iterator();
    while(ic.hasNext()) 
    {
        XTCETMContainer cc = ic.next();
               
        XTCEContainerContentModel cm = new XTCEContainerContentModel(cc, 
                Configuration.getInstance().getXTCEDatabase().getSpaceSystemTree(), null, false);

        if (!cc.isAbstract())
        {
            tlm.add(cm);
        }
        }
    }
     

System.out.println();
System.out.println();
            //List<XTCETelecommand> tcs = Configuration.getInstance().getXTCEDatabase().getTelecommands();

            Tag t = div
            (
                link().withRel("stylesheet").withType("text/css").withHref("/css/uplink.css"),                
                each(tlm, tl ->
                    fieldset
                    (
                        legend((tl.getDescription().isEmpty() ? tl.getName() : tl.getDescription())),
                        table
                        (                            
                            each(filter(tl.getContentList(), containerEntry -> containerEntry.getParameter() != null), containerEntry ->
                            tr
                            (
                                th
                                ( 
                                    label(containerEntry.getName())
                                ),
                                td
                                (
                                    label("Data" + ":")
                                ),                               
                                td
                                (
                                    label(containerEntry.getParameter().getUnits())
                                )
                            )
                        )).attr("style", "width:100%")
                    ).attr("title", tl.getName())
                )
            );
            System.out.println(t.render());
            return t.render();
        } catch (XTCEDatabaseException ex) {
            // make sure that, in case of exceptions, the message is printed out
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return sw.toString();
        } catch (Exception ex)
        {
            // make sure that, in case of exceptions, the message is printed out
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return sw.toString();
        }
    }
    
    private static ContainerTag entry(String id, String description, String value) 
    {
        tabIndex++;
        if (IDARRAY.length() != 0)
        {
            IDARRAY.append(", ");
        }
        IDARRAY.append("\'");
        IDARRAY.append(id);
        IDARRAY.append("\'");        
        return div
        (
            dt
            ( 
                label(description + ":").attr("for", id).attr("tabindex", tabIndex)
            ), 
            dd
            (
                input(attrs("#" + id)).withType("text").withValue(value).attr("title", "Integer between 0 and 255")
            )
        );
    }
    
    private static boolean isArgumentRequired(XTCETelecommand tc, XTCEArgument a)
    {
        boolean found = false;
        List<ArgumentAssignment> ass = tc.getMetaCommandReference().
                                            getBaseMetaCommand().getArgumentAssignmentList().
                                            getArgumentAssignment();
        for (MetaCommandType.BaseMetaCommand.ArgumentAssignmentList.ArgumentAssignment b : ass)
        {
            if (a.getName().equals(b.getArgumentName()))
            {
                found = true;
            }
        }
        if (!found)
        {
            IDS.add(tc.getName() + ":" + a.getName());
        }
        return found;
    }
    
    private static String getIDs()
    {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for(String id : IDS)
        {
            if (index != 0)
            {
                sb.append(", ");
            }
            index++;
            sb.append("'");
            sb.append(id);
            sb.append("'");
        }
        IDS.clear();
        return sb.toString();
    }
}
