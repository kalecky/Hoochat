/* Author:    Aaron D. Salinas <aaron_salinas@baylor.edu>
 * Class:     CSI 4321.01
 * 
 * Assignment: Program 2
 * Due Date:  Sept. 29 2015
 * 
 * Version:   1.0
 * Copyright: CSI 4321 Data Communications, Baylor University
 */
package edu.baylor.cs.hoochat;

import java.io.IOException;  //Used if an I/O error occurs
import java.io.OutputStream; //Class is a wrapper for an OutputStream source

/**
 * Serialization output source for messages
 * <p>
 * Assignment: Program 2
 * @author Aaron D. Salinas
 */
public class MessageOutput{
	
	/* **********************************************
	 *            	 Data Types 
	 ************************************************/
	private OutputStream out; //Byte output stream
	
	/* **********************************************
	 *            	 Constructor(s)
	 ************************************************/
	/**
	 * Constructs a new output source from an OutputStream
	 * @param out - byte output source
	 */
	public MessageOutput(OutputStream out){
		this.out = out; //Initialize the output stream
	}
	
	/* **********************************************
	 *            Utility Functions 
	 ************************************************/
	/**
	 * Writes a byte to a output stream
	 * @param b data to be written
	 * @throws IOException if an I/O Error Occurs
	 */
	public void write(byte[] b) throws IOException{
		out.write(b); //Write byte to the output stream
	}
}
