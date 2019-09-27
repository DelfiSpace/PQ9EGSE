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

import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.fieldset;
import static j2html.TagCreator.filter;
import static j2html.TagCreator.legend;
import static j2html.TagCreator.link;
import static j2html.TagCreator.p;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.tr;
import j2html.tags.Tag;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMContainer;
import org.xtce.toolkit.XTCETMStream;
import org.xtce.toolkit.XTCETypedObject.EngineeringType;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class DownlinkTab 
{
    public static String generate() 
    {
        try 
        {
            List<XTCEContainerContentModel> containers = new LinkedList();

            Iterator<XTCETMStream> s = Configuration.getInstance().getXTCEDatabase().getStreams().iterator();
            while (s.hasNext())
            {                
                Iterator<XTCETMContainer> ic = s.next().getContainers().iterator();
                while (ic.hasNext()) 
                {
                    XTCETMContainer cc = ic.next();
                    if (!cc.isAbstract()) 
                    {
                        containers.add(new XTCEContainerContentModel(cc,
                                Configuration.getInstance().getXTCEDatabase().getSpaceSystemTree(), null, false));                        
                    }                    
                }
            }

            Tag t = div
            (
                link().withRel("stylesheet").withType("text/css").withHref("/css/downlink.css"),
                div
                (
                    each(containers, container -> 
                        fieldset
                        (
                            legend((container.getDescription().isEmpty() ? container.getName() : container.getDescription())),
                            table
                            (
                                each(filter(container.getContentList(), containerEntry -> (containerEntry.getParameter() != null) && (!containerEntry.getParameter().getEngineeringType().equals(EngineeringType.ARRAY))), containerEntry -> tr
                                (
                                    tr
                                    (
                                        td
                                        (
                                            p(containerEntry.getName())
                                        ).attr("class", "namesColumn"),
                                        td
                                        (
                                            p("-")
                                        ).attr("id", "Downlink:" + container.getName() + ":" + containerEntry.getName()).attr("class", "valuesColumn"),
                                        td
                                        (
                                            p(containerEntry.getParameter().getUnits())
                                        ).attr("class", "unitsColumn")
                                    )
                                ))
                            ).attr("class", "outdatedFrame").attr("id", "Downlink:" + container.getName())
                        ).attr("title", container.getName())
                    )
                ).attr("class", "balancedColumns")
            );
            return t.render();
        } catch (XTCEDatabaseException ex) 
        {
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
}
