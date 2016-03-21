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

import java.io.EOFException;  //If premature end of input
import java.io.IOException;   //If an IO Error occurs while using input stream
import java.io.InputStream;   //Input stream byte source
import java.nio.charset.StandardCharsets;

/**
 * Deserialization input source for messages
 * <p>Assignment: Program 2
 * @author Aaron D. Salinas
 *
 */
public class MessageInput{
	
	/* **********************************************
	 *                  Data Types 
	 ************************************************/
	private InputStream in;      //Input Stream
	
	//Used for debugging string
	java.util.Scanner scanner;
	
	
	/* **********************************************
	 *            	   Constructor(s) 
	 ************************************************/
	
	/**
	 * Constructs a new input source from an InputStream
	 * @param in byte input source
	 */
	public MessageInput(InputStream in){
		this.in = in; //Initialize the Input Stream
		scanner = new java.util.Scanner(in);
	}

	/* **********************************************
	 *             Utility Functions 
	 ************************************************/
	/**
	 * Returns the InputStream
	 * @return byte array input stream
	 * @throws IOException if premature end of data
	 */
	public int readByte() throws IOException{
		return in.read(); //Read next byte from input stream
	}
	
	public byte[] readHeader() throws IOException {
		String hdr = "";
		for(int i = 0; i < 16; i++){
			hdr = hdr + readUTF8Byte();
		}
		
		return hdr.getBytes(StandardCharsets.UTF_8);
	}
	
	public byte[] readData(int length) throws IOException {
		String data = "";
		for(int i = 0; i < length; i++){
			data += readUTF8Byte();
		}
		return data.getBytes(StandardCharsets.UTF_8);
	}
	
	
	/**
	 * Builds a string from an input stream up until the next occurrence of 
	 * a space character.
	 * @return String representation of ASCII bytes
	 * @throws EOFException if premature end of data
	 */
	public String readToNextSpaceASCII() throws EOFException{
		String nextStr = ""; //To Store string to be returned
		boolean breakLoop = false;     //Flag to break the loop
		String current;             //Holds string representation of a byte
		do{
			current = readASCIIByte(); //Read the next byte in the stream
			//Check for end of line sequence '\r' = 13, '\n' = 10
			if((byte)current.charAt(0) == 13){
				String prevChar = current;
				current = readASCIIByte(); //'\r' was read, check for '\n'
				if((byte)current.charAt(0) == 10){
					breakLoop = true; //EOLN has been reached, break from loop
				}else{
					nextStr = nextStr + prevChar + current; //EOLN not reached
				}                                      //append bytes to string
			}
			else{ //Check if the current byte is the space character
				if((byte)current.charAt(0) != 32){
					nextStr = nextStr + current;//Append current byte to string
				}else{
					breakLoop = true; 
				}
			}
		}while(breakLoop == false);
		
		return nextStr; //Return string that was read from input stream
	}
	
	/**
	 * Builds a string from an input stream up until the next occurrence of 
	 * a carriage return.
	 * @return String representation of ASCII bytes
	 * @throws EOFException if premature end of data
	 */
	public String readASCIILine() throws EOFException{
		String nextStr = ""; //Holds string to be returned
		String current;                //Holds string representation of a byte
		boolean breakLoop = false;
		try { //Continue to read bytes until new line or end of stream reached
			while(in.available() > 0 && !breakLoop){ 
				current = readASCIIByte(); //Read current byte from input
				if(current.matches("\r")){ //Check for carriage return byte
					String str = current;
					current = readASCIIByte();
					if(current.matches("\n")){
						breakLoop = true;
					}
					else{
						nextStr = nextStr + str;
					}
				}
				else{//Append current byte to the main string
				    nextStr = nextStr + readASCIIByte(); //Store byte
				}
			}
		} catch (IOException e) {//Premature end of data
			throw new EOFException(e.getMessage());
		}
		return nextStr; //Return read string
	}
	
	/**
	 * Returns the ASCII string version of a byte from an input stream
	 * @return String representation of ASCII bytes
	 * @throws EOFException if premature end of string
	 */
	public String readASCIIByte() throws EOFException{
		byte[] bytes; //Bytes to be returned as ASCII
		try{ 
			bytes = new byte[]{(byte)in.read()}; //Read the next byte in stream
			if(bytes[0] == -1){ //Check for premature end of input stream
				throw new EOFException("Premature end of stream");
			}
			return new String(bytes, "US-ASCII"); //Decode byte in ASCII
		}
		catch(IOException e){ //Premature end of input stream
			throw new EOFException(e.getMessage());
		}	
	}
	
	/**
	 * Returns the UTF-8 string version of a byte from an input stream
	 * @return String representation of ASCII bytes
	 * @throws EOFException if premature end of string
	 */
	public String readUTF8Byte() throws EOFException{
		byte[] bytes; //Bytes to be returned as ASCII
		try{ 
			bytes = new byte[]{(byte)in.read()}; //Read the next byte in stream
			if(bytes[0] == -1){ //Check for premature end of input stream
				throw new EOFException("Premature end of stream");
			}
			return new String(bytes, StandardCharsets.UTF_8); //Decode byte in ASCII
		}
		catch(IOException e){ //Premature end of input stream
			throw new EOFException(e.getMessage());
		}	
	}
	
	/**
	 * Constructs a string from a length given the length of the string and the
	 * string itself, which are separated by a space in an byte input stream.
	 * @return str string read
	 * @throws IOException if validation fails
	 */
	public String readString() throws Exception{
		//Read length of string
		int length = Integer.valueOf(readToNextSpaceASCII());
		
		//Check that value is not negative
		if(length < 0){
			throw new IOException("Invalid String Length");
		}
		
		String str = "";
		for(int i = 0; i < length; i++){
			str += readASCIIByte();
		}
		
		return str; //Return the resulting string
	}
	
	/**
	 * Returns the next int in an input stream if available.
	 * @return int next int in the input stream
	 * @throws EOFException if premature end of data
	 * @throws NumberFormatException if incorrect integer format
	 */
	public int readInt() throws NumberFormatException, EOFException{
		//Read the next int in the byte stream
		int intVal = Integer.valueOf(readToNextSpaceASCII());
		return intVal;
	}
	
	/**
	 * Marks the current position in the input stream
	 * @param readlimit tells this input stream to allow that many bytes to be 
	 *        read before the mark position gets invalidated.
	 */
	public void mark(int readlimit){
		in.mark(readlimit);
	}
	
	/**
	 * Repositions the stream to the last position when the mark function
	 * was called
	 * @throws IOException if stream has not been marked or the mark has been
	 *                     invalidated
	 */
	public void reset() throws IOException{
		in.reset();
	}
	
	/**
	 * Returns the estimation number of bytes that can be read from the
	 * input stream
	 * @return int the number of bytes that can be read (or skipped) or 0 if it
	 * 		   reaches the end of the input stream
	 * @throws IOException if an I/O error occurs
	 */
	public int available() throws IOException{
		return in.available();
	}
	
}






