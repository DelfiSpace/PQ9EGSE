/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger;

import org.delfispace.CommandWebServer.Command;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class cmdMultiPublisher 
{
    private static cmdMultiPublisher publisher;
    private Subscriber sub;
    
    private cmdMultiPublisher()
    {
        
    }
    
    public static synchronized cmdMultiPublisher getInstance( ) 
    {
        if (publisher == null)
        {
            publisher = new cmdMultiPublisher();
        }
        return publisher;
    }
    
    public synchronized void publish(Command cmd)
    {
        if (this.sub != null)
        {
            sub.subscribe(cmd);
        }
    }
    
    public void setSubscriber(Subscriber sub)
    {
        this.sub = sub;
    }
    
}
