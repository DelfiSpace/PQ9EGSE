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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class HelloServlet extends HttpServlet
{
    private String greeting="Hello World";
    public HelloServlet(){}
    public HelloServlet(String greeting)
    {
        this.greeting=greeting;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (!request.getRequestURI().endsWith("/"))
        {
            response.sendRedirect(request.getContextPath() + "/");
        }
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<script type=\"text/javascript\" src=\"/js/jquery-1.11.1.min.js\"></script>");
        response.getWriter().println("<script type=\"text/javascript\" src=\"/js/goldenlayout.min.js\"></script>");
        response.getWriter().println("<script type=\"text/javascript\" src=\"/js/layoutmanager.js\"></script>");
        response.getWriter().println("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/goldenlayout-base.css\" />");
        response.getWriter().println("<link type=\"text/css\" rel=\"stylesheet\" href=\"/css/goldenlayout-translucent-theme.css\" />");
                
        response.getWriter().println("<h1>"+greeting+"</h1>");
        response.getWriter().println("session=" + request.getSession(true).getId());
        response.getWriter().println("session=" + request.getRequestURI());
    }
}