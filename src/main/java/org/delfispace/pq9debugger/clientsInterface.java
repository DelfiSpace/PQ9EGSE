package org.delfispace.pq9debugger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class clientsInterface 
{

   private static clientsInterface singleton;
   private static ConcurrentLinkedQueue txQueue, rxQueue;
   
   private clientsInterface(){ }

   public static synchronized clientsInterface getInstance( ) 
   {
      if (singleton == null)
      {
          singleton = new clientsInterface();
      }
      return singleton;
   }
   
   public void sendToClients()
   {
       txQueue.add(new Object());
   }
   
   public void sendToServer()
   {
       rxQueue.add(new Object());
   }

   private class threadedQueue extends Thread
   {
       private final ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
       
       @Override
       public void run()
       {
            while(true)
            {
                synchronized(queue) 
                {
                    if(!queue.isEmpty()) 
                    {
                        //queue.poll(null);
                    }
                    else
                    {
                        try 
                        {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) 
                        {
                            // nothing to do here
                        }
                    }
                }
            }
        }
    }
}