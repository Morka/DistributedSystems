package auctionserver;
import java.util.HashMap;


public final class UserDatabase {
	private static final UserDatabase INSTANCE = new UserDatabase();

	private HashMap<String, User> users;
	
	private UserDatabase(){
		users = new HashMap<String, User>();
	}
	
	public static UserDatabase getInstance(){
		return INSTANCE;
	}
	
	public synchronized void addUser(String name, User user){
		users.put(name, user);
	}
	
	public  User getUser(String name){
		if(users.containsKey(name)){
			return users.get(name);
		}else{
			return null;
		}
	}
	public HashMap<String, User> getUsersMap(){
		return users;
	}
}