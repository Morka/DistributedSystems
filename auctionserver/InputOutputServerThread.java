package auctionserver;

import java.net.*;
import java.io.*;

/**
*
* @author Alex
*/

public class InputOutputServerThread extends Thread {
	private Socket socket = null;

	public InputOutputServerThread(Socket socket){
		super("InputOutput");

		this.socket = socket;
	}

	@Override
	public void run(){
		String outputLine, inputLines = null;
		PrintWriter outWriter;
		BufferedReader inReader;

		try{

			outWriter = new PrintWriter(socket.getOutputStream(), true);
			inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			ServerProtocol protocol = new ServerProtocol();
			do{
				inputLines = inReader.readLine();
				outputLine = protocol.processInput(inputLines, socket);
				
				outWriter.println(outputLine);
					
			}while(inputLines != null && !inputLines.equals("!exit")); //!= null to 
			outWriter.close();
			inReader.close();
			socket.close();

		}catch(IOException e){
			System.out.println("IOServer Socketexception");
		}
	}

}