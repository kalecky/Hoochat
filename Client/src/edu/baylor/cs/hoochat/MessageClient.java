/* Author:    Aaron D. Salinas <aaron_salinas@baylor.edu>
 *
 *
 */

package edu.baylor.cs.hoochat;


import java.io.*; 					//For Stream Reading/Writing
import java.util.List;
import java.util.Random;

/**
 * @author Aaron D. Salinas
 */
public class MessageClient {
	
	private static BufferedReader bf = null;
	private static MessageClientHandler clientHandler, secondHandler;
	private static String user;
	private static Thread thread;
	
	
	public static void main(String [] args) throws IOException{		
		init(args); //Initialize Client Program
		
		System.out.println("\r\n*** Hoochat Client App 1.0 ***"); 
		
		//Run until user exits
		while(true){
			//Initial App Menu
			System.out.println("\r\nWhat would you like to do next?\n(1) Login\n(q) Quit");
			System.out.print("> ");
			try {
				switch(bf.readLine().trim()){
					case "1":
						if(login()){ //If user logs in successfully, start session
							boolean session = true;
							while(session){ //Continue while user in session
								operationPrompt(); //Prompt user selection
								String operation = bf.readLine().trim(); //Read Operation
								switch(operation){
									case "1": 
										checkMessages();
										break;
									case "2": 
										listAllMessages();
										break;
									case "3": 
										listSentMessages();
										break;
									case "5": 
										sendMessage();
										break;
									case "4": 
										readMessage();
										break;
									case "6": 
										removeMessage();
										break;
									case "7": 
										session = false;
										logout();
										break;
									case "q":
									case "Q":
										session = false;
										logout();
										kill();
									default:
										System.out.println("\r\n!!! Invalid selection");
								}
								if (clientHandler. getSessionID () == 0) {
									session = false;
							    	secondHandler.setSessionID(clientHandler.getSessionID());
								}
							}	
						}
						break;
					
					case "q":
							kill(); //Exit the program
						break;
					default:
						System.out.println("\r\n!!! Invalid Selection");
				}	
			} catch (Exception ex) { }
		}
	}
	
	
	private static void init(String [] args){
		//Expecting 2 total command line parameters
		if(args.length != 2){
			throw new IllegalArgumentException("Parameter(s): "
					                            + "<Server> <Port>");
		}
		//Store command line arguments
		String server = args[0]; //Store Server
		int servPort = Integer.parseInt(args[1]); //Store Port Numbers
		
		//Open Console Readers
		bf = new BufferedReader(new InputStreamReader(System.in));
		
		//Connect to the server
		clientHandler = new MessageClientHandler(server, servPort);
		(thread = new Thread (secondHandler = new MessageClientHandler(server, servPort))). start ();
	}
	
	private static void kill(){
		//Close open streams
		try {
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("***Client Terminated***");
		System.exit(0); //Kill Application
	}
	
	private static boolean login() throws IOException{
		System.out.println();
		System.out.print("Email: ");
		user = bf.readLine();
		System.out.print("Password: ");
	    String pass = bf.readLine();

	    if(!clientHandler.LoginRequest(user, pass)){
	    	user = null;
	    	if (clientHandler.isConnected()) {
	    		System.out.println("\r\n!!! Incorrect User or Password");
	    	}
	    	return false;
	    }else{
	    	System.out.println("* Successfully Logged In");
	    	secondHandler.setSessionID(clientHandler.getSessionID());
	    	return true;
	    }
	}
	
	private static void checkMessages(){
		System.out.println();
		System.out.println(":: Listing New Messages");
		List<String> mids = clientHandler.NewMessagesRequest();
		if(mids == null){
			System.err.println("!! ERROR");
		}else{
			if (mids.isEmpty ()) {
				System. out. println ("    ... (empty) ...");
			} else {
				for (String mid : mids) {
					System.out.println(mid);
				}
			}
			System.out.println(":: COMPLETE");
		} 
	}
	
	private static void listAllMessages(){
		System.out.println();
		System.out.println(":: Listing Received Messages");
		List<String> mids = clientHandler.ListMessagesRequest();
		if(mids == null){
			System.err.println("!! ERROR");
		}else{
			if (mids.isEmpty ()) {
				System. out. println ("    ... (empty) ...");
			} else {
				for (String mid : mids) {
					System.out.println(mid);
				}
			}
			System.out.println(":: COMPLETE");
		} 
	}
	
	private static void listSentMessages(){
		System.out.println();
		System.out.println(":: Listing Sent Messages");
		List<String> mids = clientHandler.ListSentMessagesRequest();
		if(mids == null){
			System.err.println("!! ERROR");
		}else{
			if (mids.isEmpty ()) {
				System. out. println ("    ... (empty) ...");
			} else {
				for (String mid : mids) {
					System.out.println(mid);
				}
			}
			System.out.println(":: COMPLETE");
		} 
	}
	
	private static void sendMessage() throws IOException{
		System.out.println();
		System.out.println(":: Sending Message");
		
		System.out.print("Enter Receiver Email: ");
		String rcvr = bf.readLine().trim();
		
		System.out.print("Enter Message:\n");
		String msg = bf.readLine().trim();
		
		if(!clientHandler.sendRequest(rcvr, msg)){
			System.err.println("!! ERROR");
		}else{
			System.out.println("* Message sent!");
			System.out.println(":: COMPLETE");
		}
	}
	
	private static void readMessage() throws IOException{
		System.out.println();
		System.out.println(":: Reading Message");
		
		System.out.print("Enter Message ID: ");
		String mid = bf.readLine().trim();
		
		String message = clientHandler.PullRequest(mid);
		if(message == null){
			System.err.println("!! ERROR");
		}else{
			System.out.println("==========");
			System.out.println(message);
			System.out.println("==========");
			System.out.println(":: COMPLETE");
		}
	}
	
	private static void removeMessage() throws IOException{
		System.out.println();
		System.out.println(":: Removing Message");
		
		System.out.print("Enter Message ID: ");
		String mid = bf.readLine().trim();
		
		if(!clientHandler.RemoveRequest(mid)){
			System.err.println("!! ERROR");
		}else{
			System.out.println("* Message removed!");
			System.out.println(":: COMPLETE");
		}
	}
	
	private static void logout(){
		if(clientHandler.LogoutRequest()){
			user = null;
		}else{
			System.err.println("!! ERROR");
		}
    	secondHandler.setSessionID(clientHandler.getSessionID());
	}
	
	private static void operationPrompt(){
		//System.out.println("\nLogged in as " + user);
		System.out.println();
		System.out.println("What would you like to do next?\n(1) List unread messages\n(2) List received messages\n(3) List sent messages\n(4) Read message\n(5) Send message\n(6) Delete message\n(7) Logout\n(q) Quit");
		System.out.print("> ");
	}
}
