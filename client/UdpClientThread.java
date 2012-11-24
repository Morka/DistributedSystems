package client;

import java.net.*;
import java.io.*;

public class UdpClientThread extends Thread{
	private DatagramSocket datagramSocket; 
	
	public UdpClientThread(DatagramSocket socket){
		this.datagramSocket = socket;
	}
	
	@Override
	public void run(){
		byte[] bufferForReceiving = new byte[256];
		String received;
		
		DatagramPacket datagramPacket = new DatagramPacket(bufferForReceiving, bufferForReceiving.length);
		
		while(true){
			try{
				datagramSocket.receive(datagramPacket);
				
				received = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
				
				String messageToUser = null;
				
				if(received.startsWith("!auction-ended")){
					String[] split = received.split(" ", 4);
					messageToUser = "Auction " + split[3] + "has ended. ";
					
					if(TCPClient.getUser().equals(split[1]) && !split[1].equals("null")){
						messageToUser += "You won with " + split[2];				
					}else if(split[1].equals("null")){
						messageToUser += "Noone Bid";
					}
					else{
						messageToUser += split[1] +" won with " + split[2];
					}
				}else if(received.startsWith("!new-bid")){
					String[] split = received.split(" ", 2);
					messageToUser = "You have been overbid on " + split[1];
				}else if(received.startsWith("!end")){
					messageToUser = "Server shutdown";
					System.out.println(messageToUser);
					break;
				}
				else{
					messageToUser = "FEHLER";
				}
				
				System.out.println(messageToUser);
			}catch (IOException ex) { //socket closed
				break;
            }
		}
		datagramSocket.close();
	}
}