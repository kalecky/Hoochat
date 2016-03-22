package edu.baylor.cs.hoochat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
	
	public boolean LoginRequest(String user, String pass){
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. LOGIN_REQUEST, new String [] { user, pass }));
			return (sessionID = reply. getSID ()) != -1 && reply. getType () == PacketType. LOGIN_RESPONSE;			
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			return false;
		}
	}
	
	public boolean sendRequest (String recipient, String message) {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. SEND_REQUEST, new String [] { recipient, message }));
			return sessionID == reply. getSID () && reply. getType () == PacketType. SEND_RESPONSE && reply. getData (). size () == 1 && reply. getData (). get (0) == "1";			
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			return false;
		}
	}
	
	public String PullRequest (String mid) {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. PULL_REQUEST, new String [] { mid }));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. PULL_RESPONSE && reply. getData (). size () == 2) {
				String sender = reply. getData (). get (0);
				String message = reply. getData (). get (1);
				return sender + ": " + message;
			}			
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
		}
		return null;
	}
	
	public boolean RemoveRequest (String mid) {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. REMOVE_REQUEST, new String [] { mid }));
			return sessionID == reply. getSID () && reply. getType () == PacketType. REMOVE_RESPONSE && reply. getData (). size () == 1 && reply. getData (). get (0) == "1";			
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			return false;
		}
	}
	
	public boolean LogoutRequest () {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. LOGOUT_REQUEST));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. LOGOUT_RESPONSE) {
				sessionID = -1;
				return true;
			}
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
		}
		return false;
	}
	
	public List<String> NewMessagesRequest () {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. NEWMESSAGES_REQUEST));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. NEWMESSAGES_RESPONSE) {
				return reply.getData();
			}
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
		}
		return null;
	}
	
	Packet SendAndReceive (Packet packet) throws IOException {
		byte [] data = packet. serialize ();
		out. write (data);
		Packet reply = Packet. initialize (in. readHeader ());
		reply. parseData (in. readData (reply. getLength ()));
		return reply;
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
