package auctionserver;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;

public class Auction {
	private static final UserDatabase uDatabase = UserDatabase.getInstance();

	private String description; 
	private int ID;
	private boolean hasEnded;

	private String nameOfHighestBidder = null;
	private double highestBid;
	private User seller;

	private Timer timer;
	private Calendar endTimeOfAuction = Calendar.getInstance();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

	private static int countIDs;

	public Auction(String description, int duration, User seller){
		this.description = description;
		this.seller = seller;

		this.hasEnded = false;
		endTimeOfAuction.add(Calendar.SECOND, duration);

		this.timer = new Timer();
		EndOfAuction endOfAuction = new EndOfAuction(this);
		this.timer.schedule(endOfAuction, duration*1000); //probably takes it in milliseconds?

		countIDs++;
		ID = countIDs;
	}

	public int getID(){
		return ID;

	}
	
	public Calendar getEndTimeOfAuction(){
		return endTimeOfAuction;
	}

	public boolean setBid(String name, double bid){
		if(hasEnded == true){
			return false;
		}
		if(this.nameOfHighestBidder == null){
			highestBid = bid;
			nameOfHighestBidder = name;
			return true;
		}
		else if(this.highestBid < bid){
			highestBid = bid;
			uDatabase.getUser(nameOfHighestBidder).sendMessages("!new-bid" + " " + description);
			nameOfHighestBidder = name;
			return true;
		}
		else{
			return false;
		}
	}

	public double getHighestBid(){
		return highestBid;
	}

	public String getDescription(){
		return description;
	}

	public boolean getHasEnded(){
		return hasEnded;
	}

	public String toString(){
		if(nameOfHighestBidder == null){
			return ID + ". " + description + " " + seller.getUserName() + " " + dateFormat.format(endTimeOfAuction.getTime()) +
					" " + highestBid + " none";
		}else{
			return ID + ". " + description + " " + seller.getUserName() + " " + dateFormat.format(endTimeOfAuction.getTime()) +
					" " + highestBid + " " + nameOfHighestBidder;
		}
	}

	private void endAuction(){
		seller.sendMessages("!auction-ended " + nameOfHighestBidder + " " + highestBid + " " + description);
		
		if(nameOfHighestBidder != null){
			uDatabase.getUser(nameOfHighestBidder).sendMessages("!auction-ended " + nameOfHighestBidder + " " 
													+ highestBid + " " + description);
		}
		
		hasEnded = true;
		this.timer.cancel();
	}
	
	public void endTimer(){
		this.timer.cancel();
	}

	private class EndOfAuction extends TimerTask{
		private Auction auction;

		public EndOfAuction(Auction auction){
			this.auction = auction;
		}

		@Override
		public void run(){
			auction.endAuction();
		}
	}
}

