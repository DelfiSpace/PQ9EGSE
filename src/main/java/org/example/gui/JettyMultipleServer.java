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
package org.example.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.delfispace.pq9debugger.Command;
import org.delfispace.pq9debugger.Subscriber;
import org.delfispace.pq9debugger.cmdMultiPublisher;
import org.delfispace.pq9debugger.cmdMultiSubscriber;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.json.simple.JSONObject;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class JettyMultipleServer
{
    private static int counter = 0;
    
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
 
        // Establish ServletContext for all servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Path webrootPath = new File("src/main/resources").toPath().toRealPath();
        context.setBaseResource(Resource.newResource(webrootPath.toUri()));
        server.setHandler(context);

        // Add a servlet (technique #1)
        ServletHolder holderHello = context.addServlet(HelloServlet.class,"/");
        holderHello.setInitOrder(0);

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/wss/*");

        // Add default servlet last (always last) (technique #2)
        // Must be named "default", must be on path mapping "/"
        ServletHolder holderDef = new ServletHolder("default",DefaultServlet.class);
        holderDef.setInitParameter("dirAllowed","false");
        context.addServlet(holderDef,"/js/*");
        context.addServlet(holderDef,"/css/*");
        context.addServlet(holderDef,"/html/*");
        context.setErrorHandler(new ErrorHandler());

        cmdMultiPublisher cmd = cmdMultiPublisher.getInstance();
        cmd.setSubscriber((Command cmd1) -> {
            System.out.println("Received command: " + cmd1);
        });
        
        cmdMultiSubscriber sub = cmdMultiSubscriber.getInstance();
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if ((counter % 2) != 0)
            {
                // Sending log...
                sub.publish(new Command("log", (new Date()).toString()));
            }
            else
            {
                sub.publish(new Command("downlink", counter + " "));
            }
            if ((counter % 21) == 0)
            {
                sub.publish(new Command("abc", counter + " "));
            }
            counter++;
        }, 0, 5000, TimeUnit.MILLISECONDS); 
        
        server.start();
        server.join();
    }
}
