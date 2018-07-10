/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    LinkedList<Subscriber> linkedlist = new LinkedList<Subscriber>();
    private Subscriber sub;
    
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
