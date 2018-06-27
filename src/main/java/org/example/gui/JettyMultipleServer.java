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
import java.nio.file.Path;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class JettyMultipleServer
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
 
        // Establish ServletContext for all servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Path webrootPath = new File("src/main/resources").toPath().toRealPath();
        context.setBaseResource(Resource.newResource(webrootPath.toUri()));
        // What file(s) should be used when client requests a directory
        //context.setWelcomeFiles(new String[] { "index.html" });
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

        server.start();
        server.join();
    }
}
