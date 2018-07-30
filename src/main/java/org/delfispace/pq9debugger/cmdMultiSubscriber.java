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

import org.delfispace.CommandWebServer.Command;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class cmdMultiSubscriber 
{
    private static cmdMultiSubscriber publisher;
    LinkedList<Subscriber> linkedlist = new LinkedList<>();
    
    private cmdMultiSubscriber()
    {
        
    }
    
    public static synchronized cmdMultiSubscriber getInstance( ) 
    {
        if (publisher == null)
        {
            publisher = new cmdMultiSubscriber();
        }
        return publisher;
    }
    
    public synchronized void publish(Command cmd)
    {
        
        if (!linkedlist.isEmpty())
        {
            Iterator<Subscriber> it = linkedlist.iterator();
            while (it.hasNext())
            {
                it.next().subscribe(cmd);
            }
        }
    }
    
    public void setSubscriber(Subscriber sub)
    {
        linkedlist.add(sub);
    }    
}
