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
package org.delfispace.CommandWebServer;

import jakarta.servlet.Servlet;
import java.net.URL;
import org.delfispace.pq9debugger.Subscriber;
import org.delfispace.pq9debugger.cmdMultiPublisher;
import org.delfispace.pq9debugger.cmdMultiSubscriber;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class CommandWebServer
{
    private final Server server;
    private final cmdMultiSubscriber sub;
    private final cmdMultiPublisher pub;
    
    public CommandWebServer(int port) throws Exception
    {
        server = new Server(port);
 
        // Establish ServletContext for all servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        URL url = CommandWebServer.class.getClassLoader().getResource("webroot/");               
        context.setBaseResource(Resource.newResource(url.toURI()));
        server.setHandler(context);

        // Add a servlet
        context.addServlet(LogpageServlet.class, "/log");
        ServletHolder holderHello = context.addServlet(WebpageServlet.class,"/");
        holderHello.setInitOrder(0);

        // Add a websocket to a specific path spec        
        Servlet websocketServlet = new JettyWebSocketServlet() 
        {
            @Override
            protected void configure(JettyWebSocketServletFactory factory) 
            {
                factory.addMapping("/", (req, res) -> new CommandWebSocket());
            }
        };
        context.addServlet(new ServletHolder(websocketServlet), "/wss/*");
        JettyWebSocketServletContainerInitializer.configure(context, null);

        ServletHolder holderDef = new ServletHolder("default",DefaultServlet.class);
        holderDef.setInitParameter("dirAllowed","false");
        context.addServlet(holderDef,"/js/*");
        context.addServlet(holderDef,"/css/*");
        context.addServlet(holderDef,"/html/*");
        context.setErrorHandler(new WebSocketErrorHandler());

        pub = cmdMultiPublisher.getInstance();                
        sub = cmdMultiSubscriber.getInstance();        
    }
    
    public void start() throws Exception
    {
        server.start();
    }
    
    public void join() throws InterruptedException
    {
        server.join();
    }
    
    public void send(Command cmd)
    {
        sub.publish(cmd);
    }
    
    public void serReceptionHandler(Subscriber cmdSub)
    {
        pub.setSubscriber(cmdSub);
    }
}
