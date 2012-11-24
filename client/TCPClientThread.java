package client;

import java.io.*;

public class TCPClientThread extends Thread{
	private BufferedReader inFromServer;
	
	public TCPClientThread(BufferedReader inFromServer){
		this.inFromServer = inFromServer;
	}
	
	@Override
	public void run(){
		while(true){
			String inFromServerString = null;
			
			try{
				inFromServerString = inFromServer.readLine();
				if(inFromServerString == null){ //server was shut down
					break;
				}
				
			}catch(IOException ex){
				break;
			}
			System.out.println(inFromServerString);
		}
	}
}