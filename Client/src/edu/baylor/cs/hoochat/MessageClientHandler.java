package edu.baylor.cs.hoochat;

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
	
	public boolean LoginRequest(String user, String pass){
		Packet packet = new Packet (sessionID, PacketType. LOGIN_REQUEST);
		packet.getData().add(user);
		packet.getData().add(pass);
		byte[] data = packet.serialize();
		
		
		try { 
			// send byte[] data
			out.write(data);
		
			//Handle Reply
			//Store header
			byte[] hdr  = in.readHeader();
			Packet reply = Packet. initialize (hdr);
			
			//Store Data
			byte[] rcvData = in.readData(reply.getLength());
			byte[] pktByte = concateHdrData(hdr, rcvData);
			reply.parseData(pktByte, 16, reply.length);
			
			
			return (sessionID = reply.getSID()) != -1;
			
		}catch (IOException e){
			System.err.println(e.getMessage());
		}
		
		return false; //Not reached unless error occurs
	}
	
	public boolean sendRequest(String recipient, String message){
		Packet packet = new Packet (sessionID, PacketType. SEND_REQUEST);
		packet.getData().add(recipient);
		packet.getData().add(message);
		byte[] data = packet.serialize();

		// send data
		try {
			out.write(data); //Send the packet to the server
			
			//Handle the reply
			//Store header
			byte[] hdr  = in.readHeader();
			Packet reply = Packet. initialize (hdr);
			
			//Store Data
			byte[] rcvData = in.readData(reply.getLength());
			byte[] pktByte = concateHdrData(hdr, rcvData);
			reply.parseData(pktByte, 16, reply.length);
			
			return sessionID == reply. getSID () && reply. getType () == PacketType. LOGIN_RESPONSE && reply. getData (). size () == 1 && reply. getData (). get (0) == "1";
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return false;
	}
	
	public void PullRequest(Integer mid){
		Packet packet = new Packet (sessionID, PacketType. PULL_REQUEST);
		packet.getData().add(mid.toString());
		byte[] data = packet.serialize();

		// send data

		// receive byte[] read
		
		byte[] read = null;
		Packet reply = Packet. initialize (read);
		reply. parseData (data, 16, read. length - 16);
		if (sessionID == reply. getSID () && reply. getType () == PacketType. PULL_RESPONSE && reply. getData (). size () == 2) {
			String sender = reply. getData (). get (0);
			String message = reply. getData (). get (1);
			// RETURN
		}
	}
	
	public boolean RemoveRequest(Integer mid){
		Packet packet = new Packet (sessionID, PacketType. REMOVE_REQUEST);
		packet.getData().add(mid.toString());
		byte[] data = packet.serialize();

		// send data

		// receive byte[] read
		
		byte[] read = null;
		Packet reply = Packet. initialize (read);
		reply. parseData (data, 16, read. length - 16);
		return sessionID == reply. getSID () && reply. getType () == PacketType. REMOVE_RESPONSE && reply. getData (). size () == 1 && reply. getData (). get (0) == "1";
	}
	
	public boolean LogoutRequest(){
		Packet packet = new Packet (sessionID, PacketType. LOGOUT_REQUEST);
		byte[] data = packet.serialize();

		try { 
			// send byte[] data
			out.write(data);
		
			//Handle Reply
			
			//Store header
			byte[] hdr  = in.readHeader();
			Packet reply = Packet. initialize (hdr);
			
			//Store Data
			byte[] rcvData = in.readData(reply.getLength());
			byte[] pktByte = concateHdrData(hdr, rcvData);
			reply.parseData(pktByte, 16, reply.length);
			
			
			return sessionID == reply. getSID () && reply. getType () == PacketType. LOGOUT_RESPONSE;
			
		}catch (IOException e){
			System.err.println(e.getMessage());
		}
		
		return false;
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
	
	private byte[] concateHdrData(byte[] h, byte[] d){
		byte[] pktBytes = h;
		for(int i = h.length, j = 0; i < h.length + d.length; i++, j++){
			pktBytes[i] = d[j];
		}
		return pktBytes;
	}
	
}
