package auctionserver;
import java.util.LinkedList;
import java.io.IOException;
import java.net.*;

public class User {

	private String hostname;
	private String userName;
	private boolean loggedIn = false;

	private int udpPort;

	private LinkedList<String> messages; 

	public User(String userName){
		this.userName = userName;

		messages = new LinkedList<String>();
	}

	public void sendMessages(String message){
		synchronized(this){
			if(this.loggedIn == true){
				this.sendUdpMessages(message);
			}else{
				messages.push(message);
			}
		}
	}

	public synchronized void loginUser(String hostname, int udpPort){
		this.hostname = hostname;
		this.udpPort = udpPort;
		loggedIn = true;
		while(!messages.isEmpty()){
			this.sendUdpMessages(messages.pop());
		}	
	}

	public void serverShutDown(){
		if(loggedIn == true){
			loggedIn = false;
			sendUdpMessages("!end");
		}
	}

	public synchronized void logout(){
		loggedIn = false;
	}

	public String getUserName(){
		return userName;
	}

	private void sendUdpMessages(String message){
		byte[] sendBuffer = new byte[256];
		try{
			sendBuffer = message.getBytes();
		}catch(IndexOutOfBoundsException ex){
			System.out.println("Buffer not big enough");
		}
		try{
			DatagramSocket datagramSocket = new DatagramSocket();
			DatagramPacket datagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
					InetAddress.getByName(hostname), udpPort);
			datagramSocket.send(datagramPacket);

		}catch(UnknownHostException ex){
			System.out.println("UnknownHostException");
		}catch (SocketException ex) {
			System.out.println("SocketException");
		} catch (IOException ex) {
			System.out.println("IOExcepiton");
		}

	}
}