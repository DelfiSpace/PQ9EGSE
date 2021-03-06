/*
 * Copyright (C) 2019 Stefano Speretta
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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class CommandTest 
{
    @Test
    public void testgetters()
    {
        Command cmd = new Command("A", "B");
        Assert.assertEquals("A", cmd.getCommand()); 
        Assert.assertEquals("B", cmd.getData()); 
    }
    
    @Test
    public void testEncoding()
    {
        Command cmd = new Command("A", "B");
        Assert.assertEquals("{\"data\":\"B\",\"command\":\"A\"}", cmd.toJSON());        
    }
    
    @Test
    public void testtoString() 
    {
        Command cmd = new Command("A", "B");
        Assert.assertEquals("Command: \"A\", Data: \"B\"", cmd.toString());
    }
}
