package edu.baylor.cs.hoochat;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MessageClientHandler implements Runnable {
	
	private MessageInput in = null;
	private MessageOutput out = null;
	private SSLSocket socket = null;
	private boolean loggedIn = false;
	private long sessionID = 0;
	String server;
	int servPort;
	boolean connected = false;
	
	public MessageClientHandler(String server, int servPort){
		this. server = server;
		this. servPort = servPort;
	}
	
	private boolean connect () {
		SSLSocketFactory fSock = (SSLSocketFactory)SSLSocketFactory.getDefault();
		try{
			// Create socket that is connected to server on specified port
			// System.out.println("Connecting to server...");
			socket = (SSLSocket) fSock.createSocket(server, servPort);
			socket.startHandshake(); //Start handshake with server
			in = new MessageInput(socket.getInputStream());
			out = new MessageOutput(socket.getOutputStream());
			return true;
		}catch(IOException e){
			System.err.print("\r\n### Unable to communicate: Failure connecting "
					+ "to server " + server + " on port " + servPort + "\r\n");
			return false;
		}
	}
	
	private void checkConnection () {
		if (true) {
			connected = connect ();
		}
	}
	
	public boolean LoginRequest(String user, String pass){
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. LOGIN_REQUEST, new String [] { user, pass }));
			return (sessionID = reply. getSID ()) != 0 && reply. getType () == PacketType. LOGIN_RESPONSE && reply.getData().size() == 1 && reply.getData().get(0).equals("1");			
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			connected = false;
			return false;
		}
	}
	
	public boolean sendRequest (String recipient, String message) {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. SEND_REQUEST, new String [] { recipient, message }));
			return sessionID == reply. getSID () && reply. getType () == PacketType. SEND_RESPONSE && reply. getData (). size () == 1 && reply. getData (). get (0).equals("1");			
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			connected = false;
			return false;
		}
	}
	
	public String PullRequest (String mid) {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. PULL_REQUEST, new String [] { mid }));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. PULL_RESPONSE && reply. getData (). size () == 1) {
				String message = reply. getData (). get (0);
				return message;
			}			
		} catch (Exception ex) {
			System. err. println (ex. getMessage ());
			connected = false;
		}
		return null;
	}
	
	public boolean RemoveRequest (String mid) {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. REMOVE_REQUEST, new String [] { mid }));
			return sessionID == reply. getSID () && reply. getType () == PacketType. REMOVE_RESPONSE && reply. getData (). size () == 1 && reply. getData (). get (0).equals("1");			
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			connected = false;
			return false;
		}
	}
	
	public boolean LogoutRequest () {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. LOGOUT_REQUEST));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. LOGOUT_RESPONSE) {
				sessionID = 0;
				return true;
			}
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			connected = false;
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
			connected = false;
		}
		return null;
	}
	
	public List<String> ListMessagesRequest () {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. LISTMESSAGES_REQUEST));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. LISTMESSAGES_RESPONSE) {
				return reply.getData();
			}
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			connected = false;
		}
		return null;
	}
	
	public List<String> ListSentMessagesRequest () {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. LISTSENTMESSAGES_REQUEST));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. LISTSENTMESSAGES_RESPONSE) {
				return reply.getData();
			}
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			connected = false;
		}
		return null;
	}
	
	public String LatestRequest () {
		try { 
			Packet reply = SendAndReceive (new Packet (sessionID, PacketType. LATEST_REQUEST));
			if (sessionID == reply. getSID () && reply. getType () == PacketType. LATEST_RESPONSE) {
				return reply.getData().isEmpty() ? null : reply.getData().get(0);
			}
		} catch (IOException ex) {
			System. err. println (ex. getMessage ());
			connected = false;
		}
		return null;
	}
	
	Packet SendAndReceive (Packet packet) throws IOException {
		checkConnection();
		byte [] data = packet. serialize ();
		out. write (data);
		Packet reply = Packet. initialize (in. readHeader ());
		reply. parseData (in. readData (reply. getLength ()));
		// System.out.println("R: " + reply.getLength() + " " + reply.getType() + " " + reply.getSID() + " " + reply.getData().size());
		if (reply. getSID () == 0) {
			sessionID = 0;
		}
		return reply;
	}
	

	public boolean isConnected () {
		return connected;
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
	public void setSessionID (long value){
		sessionID = value;
	}

	
	public void run() {
		String latest = "0", latestLatest;
		while (true) {
			if (sessionID != 0) {
				latestLatest = LatestRequest();
				if (latestLatest != latest && latestLatest != null && latestLatest.compareTo(latest) > 0) {
					System.out.print("\r\n\r\n*** NEW MESSAGE ** ID " + latestLatest + " ***\r\n\r\n>");
					latest = latestLatest;
				}
			}
			try {
				Thread. sleep (1234);
			} catch (InterruptedException e) { }
		}
	}
		
}
