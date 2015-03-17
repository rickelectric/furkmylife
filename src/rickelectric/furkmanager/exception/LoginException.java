package rickelectric.furkmanager.exception;

public class LoginException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private int triesLeft;
	
	public LoginException(String message){
		super("Login Error"+message==null?"":(": "+message));
	}
	
	public LoginException(String message,int triesLeft){
		this(message);
		this.triesLeft=triesLeft;
	}

	public int getTriesLeft() {
		return triesLeft;
	}

}
