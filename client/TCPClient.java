package client;

import java.io.*;
import java.net.*;

public class TCPClient
{	
	private static String user;
	
	public static void main(String args[]) throws Exception
	{
		int port = 0;
		int udpPort = 0; //ACHTUNG AENDERN!
		String serverIP = "127.0.0.1";
				
		try{
		serverIP = args[0];
		System.out.println(serverIP);
		port = Integer.parseInt(args[1]);
		System.out.println(port);
		udpPort = Integer.parseInt(args[2]);
		System.out.println(udpPort);
		}catch(ArrayIndexOutOfBoundsException ex){
			System.out.println("Usage: Server-Ip tcpPort");
			return;
		}catch(NumberFormatException ex){
			System.out.println("Usage: Server-IP tcpPort");
			return;
		}
		
		String send; 	//Sentence to be sent *choosen by user*
		//String answer = "";	//Answer from Server
		
		BufferedReader inFromUser;
		BufferedReader inFromServer = null;
		DataOutputStream outToServer;
		Socket clientSocket = null;
		TCPClientThread tcpListen = null;
		
		DatagramSocket datagramSocket = null;
		UdpClientThread udpListen = null;
		
		try{
			clientSocket = new Socket(serverIP, port); //Server to connect with
			
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			
			tcpListen = new TCPClientThread(inFromServer);
			
		}catch ( UnknownHostException e){
			System.err.println("Don't know about host: stockholm.vitalab.tuwien.ac.at");
			return;
		}
		
		Thread tcpListenerThread = new Thread(tcpListen);
		tcpListenerThread.start();
		
		Thread udpListenerThread = null;
		
		try{
			datagramSocket = new DatagramSocket(udpPort);
			
			udpListen = new UdpClientThread(datagramSocket);
			
			udpListenerThread = new Thread(udpListen);
			udpListenerThread.start();
		}catch(SocketException ex){
			
		}
		
		//serverIsOnline = true;
		try{
			while(true){
				inFromUser = new BufferedReader( new InputStreamReader(System.in));
				outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
			
				send = inFromUser.readLine();
				
				if(send.startsWith("!login")){
					String tmp = send.substring(7);
					
					user = tmp;
					
					send += " " + udpPort;
				}
				outToServer.writeBytes(send + '\n');
				
				if(send.equals("!end")){
					System.out.println("Client now closing - Good Bye");
					break;
				}
			}
		}
		catch(IOException ex){
			System.out.println("Connection lost");
		}
		if(clientSocket != null){
			clientSocket.close();
		}
		if(datagramSocket != null){
			datagramSocket.close();
		}
	}
	
	public static  String getUser(){
		return user;
	}
}