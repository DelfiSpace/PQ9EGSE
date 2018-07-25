/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;
import static j2html.TagCreator.legend;
import static j2html.TagCreator.link;
import static j2html.TagCreator.textarea;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import org.xtce.toolkit.XTCETelecommand;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class UplinkTab 
{
    private static int tabIndex = 0;
    private static final StringBuilder idArray = new StringBuilder();
    
    public static String generate()
    {
        List<String[]> numbers = new ArrayList();
        numbers.add(new String[]{"dest", "Destination", "7"});
        numbers.add(new String[]{"src", "Source", "1"});
        
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
                            label("Data" + ":").attr("for", "data").attr("tabindex", tabIndex)
                        ), 
                        dd
                        (
                            textarea("17 1").withType("text").withId("data").attr("title", 
                                    "Array of integers between 0 and 255 or hex bytes separated by blanks")
                        )
                    )
                ),
                button("Send").attr("id", "SendRaw").attr("onclick", "fetchData(this.id, [" + idArray.toString()+ ", 'data'])").attr("tabindex", tabIndex).attr("title", "Raw Frame")
            ),
            each(filter(tcs, tc -> tc.isAbstract() != true), tc ->
                fieldset
                (
                    legend(tc.getDescription()),
                        dl(),
                    button("Send").attr("id", tc.getName()).attr("onclick", "fetchData(this.id, \"\")").attr("tabindex", tabIndex++).attr("title", tc.getDescription())
                )
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
}
