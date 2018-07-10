/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public interface Subscriber 
{
    void subscribe(Command cmd);
}
