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
package org.delfispace.pq9debugger.PQ9DataSocket;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class StatisticsGenerator 
{
    int counter = 0;
    double max = Double.MIN_VALUE;
    double min = Double.MAX_VALUE;
    double avg = 0;
    double std = 0;
    
    public void addPoint(double value)
    {
        counter++;
        if (value > max)
        {
            max = value;
        }
        if (value < min)
        {
            min = value;
        }
        avg += value;
        std += value*value;
    }
    
    public void reset()
    {
        counter = 0;
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;
        avg = 0;
        std = 0;
    }
    
    public int getPoints()
    {
        return counter;
    }
    
    public void printStatistics()
    {
        avg /= counter;
        std = Math.sqrt((std / counter) - avg*avg);
        System.out.println("Points: " + counter);
        System.out.println("Max: " + max);
        System.out.println("Min: " + min);
        System.out.println("Avg: " + avg);
        System.out.println("Std: " + std);
    }
}
