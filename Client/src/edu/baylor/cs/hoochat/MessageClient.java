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
	private static MessageClientHandler clientHandler;
	private static String user;
	
	
	public static void main(String [] args) throws IOException{		
		init(args); //Initialize Client Program
		
		System.out.println("HooNet Client App 1.0"); 
		
		//Run until user exits
		while(true){
			//Initial App Menu
			System.out.println("\nPlease Make a Selection:\n(1) Login\n(q) Quit");
			System.out.print("> ");
			switch(bf.readLine().trim()){
				case "1":
					if(login()){ //If user logs in successfully, start session
						boolean session = true;
						while(session){ //Continue while user in session
							operationPrompt(); //Prompt user selection
							String operation = bf.readLine().trim(); //Read Operation
							switch(operation){
								case "1": //Check for new messages
									checkMessages();
									break;
								case "2": //Send message
									sendMessage();
									break;
								case "3": //Send message
									readMessage();
									break;
								case "4": //Send message
									removeMessage();
									break;
								case "5": //Logout
									session = false;
									logout();
									break;
								case "q":
								case "Q":
									session = false;
									logout();
									kill();
								default:
									System.out.println("Invalid selection! "
														+ "Please try again.");
							}
						}	
					}
					break;
				
				case "q":
						kill(); //Exit the program
					break;
				default:
					System.out.println("Invalid Selection!\n");
			}
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
		boolean loginAttempt = true;
		boolean login = false;
		
		//Prompt user to login
		while(loginAttempt){
			System.out.println();
			System.out.println("Login");
			System.out.print("Email: ");
			user = bf.readLine();
			System.out.print("Password: ");
		    String pass = bf.readLine();

		    /******Send login request*****/
		    login = clientHandler.LoginRequest(user, pass);
		    
		    //Check login success
		    if(!login){
		    	user = null;
		    	System.out.println("Incorrect User or Password!");
		    	System.out.println("Would you like to try again? (y/n)");
		    	boolean flag = true;
		    	
		    	while(flag){
			    	switch(bf.readLine()){
			    		case "y":
			    		case "Y":
			    			loginAttempt = true;
			    			flag = false;
			    			break;
			    		case "n":
			    		case "N":
			    			kill();
			    		default:
			    			flag = true;
			    	}
		    	}
		    }else{
		    	System.out.println("Successfully Logged In! Session Started");
		    	return true;
		    }
		}
		return false;
	}
	
	private static void checkMessages(){
		System.out.println();
		System.out.println("Checking Messages: ");
		List<String> mids = clientHandler.NewMessagesRequest();
		if(mids == null){
			System.err.println("Error Checking Messages");
		}else{
			for (String mid : mids) {
				System.out.println(mid);
			}
		} 
	}
	
	private static void sendMessage() throws IOException{
		System.out.println();
		System.out.println("Send Message: ");
		
		System.out.print("Enter Receiver Email: ");
		String rcvr = bf.readLine().trim();
		
		System.out.print("Enter Message:\n");
		String msg = bf.readLine().trim();
		
		if(!clientHandler.sendRequest(rcvr, msg)){
			System.err.println("Error Sending Message");
		}else{
			System.out.println("Message Sent!\n");
		}
	}
	
	private static void readMessage() throws IOException{
		System.out.println();
		System.out.println("Read Message: ");
		
		System.out.print("Enter Message ID: ");
		String mid = bf.readLine().trim();
		
		String message = clientHandler.PullRequest(mid);
		if(message == null){
			System.err.println("Error Reading Message");
		}else{
			System.out.println(message);
		}
	}
	
	private static void removeMessage() throws IOException{
		System.out.println();
		System.out.println("Remove Message: ");
		
		System.out.print("Enter Message ID: ");
		String mid = bf.readLine().trim();
		
		if(clientHandler.RemoveRequest(mid)){
			System.err.println("Error Removing Message");
		}else{
			System.out.println("Message removed!");
		}
	}
	
	private static void logout(){
		if(clientHandler.LogoutRequest()){
			user = null;
			
		}else{
			System.err.println("Error Logging User Out!");
		}
	}
	
	private static void operationPrompt(){
		System.out.println("\nLogged in as " + user);
		System.out.println("Operations:\n(1) Check for new messages"
							+ "\n(2) Send Message\n(3) Logout\n(q) Quit");
		System.out.print("> ");
	}
}
