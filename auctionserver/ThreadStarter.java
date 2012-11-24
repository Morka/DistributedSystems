package auctionserver;
import java.net.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.LinkedList;

/**
*
* @author Alex
*/

/*Accepts a new Tcp connection and opens a new Thread for every connection.
 * Maintains a list of Sockets that are used. If server is shutdown, sockets are closed
 * by the method end() and therefore a socketexception is thrown. Every socket will
 * get closed in this exception*/

public class ThreadStarter extends Thread{
	private ServerSocket serverSocket;
	private ExecutorService threadPool;
	private LinkedList<Socket> listOfSockets;

	public ThreadStarter(ServerSocket serverSocket, ExecutorService threadPool){
		this.serverSocket = serverSocket;
		this.threadPool = threadPool;
		
		this.listOfSockets = new LinkedList<Socket>();
	}

	public void run(){
		try{
			while(true){
				Socket socket = serverSocket.accept();
				listOfSockets.push(socket);
				threadPool.execute(new InputOutputServerThread(socket));
			}
		}catch(IOException ex){ //IOException could get thrown by another cause...
								//This would nevertheless lead to closing of all sockets!!
			while(!listOfSockets.isEmpty()){
				try{
					listOfSockets.pop().close();
				}catch(IOException e){
					System.out.println("could not close");
				}
			}
		}
	}
}