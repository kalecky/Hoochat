package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import edu.baylor.cs.hoochat.*;		//Client Handler and Protocol

public class MessageClientHandler {
	
	private MessageInput in = null;
	private MessageOutput out = null;
	private SSLSocket socket = null;
	private boolean loggedIn = false;
	private long sessionID = -1;
	
	public MessageClientHandler(String server, int servPort){
		SSLSocketFactory fSock = (SSLSocketFactory)SSLSocketFactory.getDefault();
		try{
			// Create socket that is connected to server on specified port
			socket = (SSLSocket) fSock.createSocket(server, servPort);
			socket.startHandshake(); //Start handshake with server
			in = new MessageInput(socket.getInputStream());
			out = new MessageOutput(socket.getOutputStream());
			
		}catch(IOException e){
			System.err.print("Unable to communicate: Failure connecting "
					+ "to server " + server + " on port " + servPort + "\n");
		}
	}
	
	public boolean LoginRequest(String user, String pass, long sid){
		
		
		//Send Request
		
		//Handle Reply
		
		return false;
	}
	
	public void sendRequest(){
		
		//Send Request
		
		//Handle Reply
	}
	
	public void PullRequest(){
		//Send Request
		
		//Handle Reply
	}
	
	public void LogoutRequest(){
		//Send Request
		
		//Handle Reply
	}
	

	
	/**
	 * Returns if client currently has user logged in
	 * @return
	 */
	public boolean getLoggedIn(){
		return loggedIn;
	}
	
	/**
	 * Returns session ID for currently logged in user,
	 * returns -1 otherwise
	 * @return  session ID for user
	 * 			-1 if user not logged in
	 */
	public long getSessionID(){
		return sessionID;
	}
	
}
