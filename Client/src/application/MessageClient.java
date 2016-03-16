package application;
/* Author:    Aaron D. Salinas <aaron_salinas@baylor.edu>
 *
 *
 */

import java.io.BufferedReader;     //To handle user input from console
import java.io.IOException;        //If error with I/O
import java.io.InputStream;
import java.io.InputStreamReader;  //To read user input from console
import java.io.OutputStream;
import java.net.Socket;            //To connect to server
import java.util.Scanner;
import java.io.Console;

/**
 * @author Aaron D. Salinas
 */
public class MessageClient {
	private static Socket socket = null;
	private static Scanner scanner = null;
	private static BufferedReader bf = null;
	private static InputStream sockIn = null;
	private static OutputStream sockOut = null;
	private static String user;
	
	public static void main(String [] args){
		//Expecting 2 total command line parameters
		//if(args.length != 2){
		//	throw new IllegalArgumentException("Parameter(s): "
				//	                            + "<Server> <Port>");
		//}
		
		//Store command line arguments
		//String server = args[0]; //Store Server
		//int servPort   = Integer.parseInt(args[1]); //Store Port Numbers
		
		/*
		try{
			// Create socket that is connected to server on specified port
			socket = new Socket(server, servPort);
		}catch(IOException e){
			System.err.print("Unable to communicate: Failure connecting "
					+ "to server " + server + " on port " + servPort + "\n");
			System.exit(1); //Terminate program if no connection to server
		}
		*/
	
	
		//Start client application/Login process
		while(true){
			try{
				//Open Streams
				scanner = new Scanner(System.in);
				bf = new BufferedReader(
						        new InputStreamReader(System.in));
				//InputStream sockIn = new BufferedInputStream(socket.getInputStream());
				//OutputStream sockOut = new BufferedOutputStream(socket.getOutputStream());
				String pass;
				boolean loginAttempt = true;
				boolean login = false;
				
				//Prompt user to login
				while(loginAttempt){
					System.out.println("Login");
					System.out.print("Email: ");
					user = bf.readLine();
					System.out.print("Password: ");
				    pass = bf.readLine();
	
				    /******Send login request*****/
				    login = true; //Set true for testing purposes
				    
				    //Check login success
				    if(!login){
				    	System.out.println("Incorrect User or Password!");
				    	System.out.println("Would you like to try again? (y/n)");
				    	boolean flag = true;
				    	
				    	while(flag){
					    	switch(scanner.nextLine()){
					    		case "y":
					    		case "Y":
					    			loginAttempt = true;
					    			flag = false;
					    			break;
					    		case "n":
					    		case "N":
					    			clientTerminate();
					    		default:
					    			flag = true;
					    	}
				    	}
				    }else{
				    	System.out.println("Successfully Logged In!");
				    	loginAttempt = false;
				    }
				}
				
				while(login){
					operationPrompt();
					String operation = bf.readLine(); // Read Operation
					switch(operation){
						case "1": //Check for new messages
							checkMessages();
							break;
						case "2": //Send message
							sendMessage();
							break;
						case "3": //Logout
							login = false;
							logout();
							break;
						default:
							System.out.println("Invalid selection! Please try "
												+ "again.");
					}
				}
			}catch(IOException e){
				System.err.print("Unable to communicate: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	static void checkMessages(){
		//Make pull request to server
	}
	
	static void sendMessage(){
		System.out.println("Send Message: ");
		
		System.out.print("Enter Receiver Email: ");
		String rcvr = scanner.nextLine().trim();
		
		System.out.print("Enter Message:\n");
		String msg = scanner.nextLine();
		
		//Send Message
		//Prompt user if message was sent successfully or not
		
	}
	
	static void logout(){
		//Logout user, sever socket connection
	}
	
	static void clientTerminate(){
		System.out.println("***Client Terminated***");
		System.exit(0);
	}
	
	static void operationPrompt(){
		System.out.println("\nLogged in as " + user);
		System.out.println("Operations:\n1) Check for new messages"
							+ "\n2) Send Message\n3) Logout");
		System.out.print(">");
	}
}
