package auctionserver;

import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.net.*;

/**
*
* @author Alex
*/
public class ServerProtocol {
	private static final int WAITING = 0;
	private static final int LOGGED_IN = 1;
	private static final AuctionDatabase aDatabase = AuctionDatabase.getInstance();
	private static final UserDatabase uDatabase = UserDatabase.getInstance();

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

	private int state = WAITING;

	private User user = null;

	public String processInput(String theInput, Socket socket){
		String output = "";
		String name = null;
		String next = "";
		int udpPort = 0;

		if(theInput == null){
			return "FEHLER";
		}

		Scanner sc = new Scanner(theInput);

		if(sc.hasNext()){
			next = sc.next();
		}else{
			output = "Useful Arguments: \"!list\" \"!login\" \"!create\"";
			sc.close();
			return output;
		}

		if(next.equals("!end")){
			if(user != null){
				user.logout();
			}
			output = "\n";
			sc.close();
			return output;
		}

		if(next.equals("!logout")){
			if(user == null){
				output = "Not logged in";
			}
			else{
				user.logout();
				output = "User " + user.getUserName() +  " successfully logged out";
				user = null;
			}
			return output;
		}

		if(next.equals("!list")){
			output = aDatabase.getListStrings();
			if(output == ""){
				output = "No Auctions listed";
			}
			sc.close();
			return output;
		}
		if(next.equals("!login")){
			if(user != null){
				output = "Already logged in";
			}
			else if(sc.hasNext() && !sc.hasNextInt()){
				name = sc.next();
				if(sc.hasNextInt()){
					udpPort = sc.nextInt();
				}
				user = uDatabase.getUser(name);
				if(user == null){ //user erstellen!
					user = new User(name);
					uDatabase.addUser(name, user);
				}
				user.loginUser(socket.getInetAddress().getHostAddress(), udpPort);
				state = LOGGED_IN;
				output = "User " + name + " successfully logged in";
			}
			else{
				output = "Usage: !login 'Username'";
			}
		}

		if(state == LOGGED_IN){
			if(next.equals("!create")){
				int duration = 0;
				String description = "";

				if(sc.hasNextInt()){
					duration = sc.nextInt();

					while(sc.hasNext()){
						description += sc.next() + " ";
					}
					description = description.trim();
					
					Auction createdAuction = new Auction(description, duration, user);
					aDatabase.addAuction(createdAuction);

					output = "An Auction '" + description +"' with ID '" + createdAuction.getID() +
							"' has been created and will end at " + 
							dateFormat.format(createdAuction.getEndTimeOfAuction().getTime());

				}
			else{
				output = "Usage: '!create' DESCRIPTION DURATION";
			}

		}else if(next.equals("!bid")){
			double amountOfBid = 0;
			int id = 0;

			if(sc.hasNextInt()){
				id = sc.nextInt();

				if(id > aDatabase.countAuctions() || id < 1){
					sc.close();
					return "Usage: !bid 'ID' '" +
					"BID'. Be sure to choose an existing" +
					" Auction ID";
				}

				if(sc.hasNextDouble()){
					amountOfBid = sc.nextDouble();
					if(aDatabase.getAuctionByID(id).setBid(user.getUserName(), amountOfBid)){
						output = "You successfully bid with '" + amountOfBid + "' on " + "'" +
								aDatabase.getAuctionByID(id).getDescription() + "'.";
					}else{
						output = "You unsuccessfully bid with '" + amountOfBid + "' on " + "'" + 
								aDatabase.getAuctionByID(id).getDescription() + "'. Current highest" +
										" bid is " + aDatabase.getAuctionByID(id).getHighestBid();
					}
				}
				else{
					output = "Usage: !bid 'ID' '" +
							"Bid'";
				}
			}else{
				output = "Usage: !bid 'ID' '" +
						"Bid'";
			}
		}
	}

	sc.close();

	if(output == "" || output == null){
		if(state == WAITING){
			output = "Usage: \"!login\" \"list\"";
		}else{
			output =  "Usage: \"!create\" \"!bid\" \"!list\"";
		}

	}

	return output;
}	
}