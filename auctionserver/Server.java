package auctionserver;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

/**
 *
 * @author Alex
 */

/*
 * Main. Opens socket with specified port. Creates cachedThreadPool. 
 * A Thread is started, which keeps track of other Threads and uses the ThreadPool.
 * Waits until User gives any kind of input and closes down everything with method end().
 * 
 * */

public class Server {	
	private static ExecutorService threadPool;
	private static Thread thread;
	private static ServerSocket serverSocket;

	public static void main (String[] args) throws IOException{
		int port = 0;
		
		try{
			port = Integer.parseInt(args[0]);
		}catch(NumberFormatException ex){
			System.out.println("Please give a usable port");
			return;
		}catch(ArrayIndexOutOfBoundsException ex){
			System.out.println("Please give a usable port");
			return;
		}
		
		serverSocket = null;
		threadPool = Executors.newCachedThreadPool();


		try{
			serverSocket = new ServerSocket(port);
		} catch(IOException e){
			System.err.println("Could not listen to port " + port);
			threadPool.shutdownNow();
			return;
		}

		thread = new Thread(new ThreadStarter(serverSocket, threadPool));
		thread.start();

		try{
			BufferedReader inFromAdmin = new BufferedReader(new InputStreamReader(System.in));
			inFromAdmin.readLine(); //waiting for any kind of input
			
			end(); //end programm and free resources
		
		}catch(IOException ex){
			System.out.println("Input/Output exception");
		}
	}
	
	private static void end(){
		HashMap<String, User> users = new HashMap<String, User>();
		ArrayList<Auction> auctions = new ArrayList<Auction>();
		
		
		users = UserDatabase.getInstance().getUsersMap();
		auctions = AuctionDatabase.getInstance().getList();
		
		/*Iterates through all users in the Database in order to log them out
		  and send them an end message*/
		Iterator<User> iter = users.values().iterator();
		while(iter.hasNext()){
			User user = (User)iter.next();
			user.serverShutDown();
		}
		/*Iterates through all auctions and ends them*/
		for(Auction auction : auctions){
			auction.endTimer();
		}
		
		thread.interrupt(); //interrupts thread... Is it necessary
		
		try{
			serverSocket.close();
		}catch(IOException ex){
			System.out.println("Exception after closing serverSocket " + ex);
		}
		
		threadPool.shutdownNow();
	}
}