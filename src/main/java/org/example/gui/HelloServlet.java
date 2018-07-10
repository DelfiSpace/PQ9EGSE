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

import static j2html.TagCreator.body;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.script;
import static j2html.TagCreator.title;
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (!request.getRequestURI().endsWith("/"))
        {
            response.sendRedirect(request.getContextPath() + "/");
        }        
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);        
        response.getWriter().write(
            html
            (
                head
                (
                    title("PQ9 EGSE"),
                    link().withRel("stylesheet").withType("text/css").withHref("/css/goldenlayout-base.css"),
                    link().withRel("stylesheet").withType("text/css").withHref("/css/goldenlayout-translucent-theme.css")
                ),
                body
                (
                    script().withSrc("/js/jquery-1.11.1.min.js").withType("text/javascript"),
                    script().withSrc("/js/goldenlayout.min.js").withType("text/javascript"),
                    script().withSrc("/js/jsonrpc.js").withType("text/javascript"),
                    script().withSrc("/js/layoutmanager.js").withType("text/javascript")
                )
            ).render());
    }
}