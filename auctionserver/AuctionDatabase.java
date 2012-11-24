package auctionserver;
import java.util.ArrayList;

public final class AuctionDatabase {
	private static final AuctionDatabase INSTANCE = new AuctionDatabase();
	
	private ArrayList<Auction> listOfAuctions;
	
	private AuctionDatabase(){
		listOfAuctions = new ArrayList<Auction>();

	}
	
	public static AuctionDatabase getInstance(){
		return INSTANCE;
		
	}
	
	public synchronized void addAuction(Auction auction){
		listOfAuctions.add(auction);
	}
	
	public ArrayList<Auction> getList(){
		return listOfAuctions;
	}
	
	public int countAuctions(){
		return listOfAuctions.size();
	}
	
	public Auction getAuctionByID(int ID){
		for(Auction a : listOfAuctions){
			if(ID == a.getID()){
				return a;
			}
		}
		return null;
	}
	
	public String getListStrings(){
		String stringsOfAuctions = "";
		for(Auction a : listOfAuctions){
			if(a.getHasEnded() == false){
				stringsOfAuctions += a.toString() + '\n';
			}
		}
		return stringsOfAuctions;
	}
}