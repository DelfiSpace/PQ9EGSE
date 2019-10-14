/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket;

/**
 *
 * @author micha
 */
public class Bk8500CException extends Exception{
   
    public Bk8500CException(){
        
    }
    public Bk8500CException(String problem){
        super(problem);
    }
}
