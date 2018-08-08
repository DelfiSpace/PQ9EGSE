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
import static j2html.TagCreator.button;
import static j2html.TagCreator.dd;
import static j2html.TagCreator.div;
import static j2html.TagCreator.dl;
import static j2html.TagCreator.dt;
import static j2html.TagCreator.each;
import static j2html.TagCreator.fieldset;
import static j2html.TagCreator.filter;
import static j2html.TagCreator.iffElse;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;
import static j2html.TagCreator.legend;
import static j2html.TagCreator.link;
import static j2html.TagCreator.option;
import static j2html.TagCreator.select;
import static j2html.TagCreator.textarea;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import org.omg.space.xtce.MetaCommandType;
import org.omg.space.xtce.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList.ArgumentAssignment;
import org.xtce.toolkit.XTCEArgument;
import org.xtce.toolkit.XTCETelecommand;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class UplinkTab 
{
    private static int tabIndex = 0;
    private static final StringBuilder idArray = new StringBuilder();
    private static List<String> ids = new ArrayList();
    
    public static String generate()
    {
        List<String[]> numbers = new ArrayList();
        numbers.add(new String[]{"raw:dest", "Destination", "7"});
        numbers.add(new String[]{"raw:src", "Source", "1"});
        
        tabIndex = 0;
        idArray.setLength(0);
        
        List<XTCETelecommand> tcs = Configuration.getInstance().getXTCEDatabase().getTelecommands();

        Tag t = div
        (
            link().withRel("stylesheet").withType("text/css").withHref("/css/uplink.css"),
            fieldset
            (                            
                legend("Raw Frame"),
                dl
                (
                    each(numbers, i -> entry(i[0], i[1], i[2])),
                    div
                    (
                        dt
                        ( 
                            label("Data" + ":").attr("for", "raw:data").attr("tabindex", tabIndex)
                        ), 
                        dd
                        (
                            textarea("17 1").withType("text").withId("raw:data").attr("title", 
                                    "Array of integers between 0 and 255 or hex bytes separated by blanks")
                        )
                    )
                ),
                button("Send").attr("id", "SendRaw").attr("onclick", "fetchData(this.id, [" + idArray.toString()+ ", 'raw:data'])").attr("tabindex", tabIndex).attr("title", "Raw Frame")
            ),
            each(filter(tcs, tc -> tc.isAbstract() != true), tc ->
                fieldset
                (
                    legend((tc.getShortDescription().isEmpty() ? tc.getName() : tc.getShortDescription())),
                    dl
                    (
                        each(filter(tc.getArguments(), arg -> !isArgumentRequired(tc, arg)), arg ->
                        div
                        (
                            dt
                            ( 
                                label((arg.getShortDescription().isEmpty() ? arg.getName() : arg.getDescription()) + ":")
                                        .attr("for", tc.getName() + ":" + arg.getName())
                                        .attr("tabindex", tabIndex++)
                                        .attr("title", arg.getLongDescription())
                            ),
                            dd
                            (
                                iffElse(arg.getEnumerations().isEmpty(), 
                                    input(attrs("#" + tc.getName() + ":" + arg.getName())).withType("text").
                                            withValue(arg.getInitialValue()).attr("title", 
                                            arg.getLongDescription()),
                                    select 
                                    (
                                        each(arg.getEnumerations(), e -> 
                                             option(e.getShortDescription() == null ? 
                                             e.getLabel() : e.getShortDescription()).withValue(e.getLabel()))
                                    ).withId(tc.getName() + ":" + arg.getName()).attr("title", 
                                            arg.getLongDescription())
                                )
                            )   
                        )
                    )),
                    button("Send").attr("id", tc.getName()).attr("onclick", "fetchData(this.id, [" + getIDs() + "])").attr("tabindex", tabIndex++)
                ).attr("title", tc.getLongDescription())
            )
        );
        return t.render();
    }
    
    private static ContainerTag entry(String id, String description, String value) 
    {
        tabIndex++;
        if (idArray.length() != 0)
        {
            idArray.append(", ");
        }
        idArray.append("\'");
        idArray.append(id);
        idArray.append("\'");        
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
            ids.add(tc.getName() + ":" + a.getName());
        }
        return found;
    }
    
    private static String getIDs()
    {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for(String id : ids)
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
        ids.clear();
        return sb.toString();
    }
}
