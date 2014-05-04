package rickelectric.furkmanager.network.socks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import rickelectric.furkmanager.utils.SettingsManager;

public class ClientLink implements Runnable{
	
	public static final int
		PING=100,
		ADD_URL=101,
		ADD_HASH=102,
		REMOTE=103,
		ADD_TORRENT=104;
	
	private String text;
	private String response=null;
	
	public String response(){return response;}
	
	public ClientLink(int type,String t){
		if(type==PING) text="ping";
		else if(type==ADD_URL) text="url\r\n"+t;
		else if(type==ADD_HASH) text="infoHash\r\n"+t;
		else if(type==ADD_TORRENT) text="torrent\r\n"+t;
	}
	
	@Override
	public void run(){
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try{
			socket = new Socket("localhost", SettingsManager.getLocalPort());
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String textToServer;
			
			textToServer = text;
			out.print(textToServer + "\r\n"); // send to server
			out.flush();
			
			String serverResponse = null;
			while ((serverResponse = in.readLine()) != null)
                   response=serverResponse; // read from server and print it.
			
			out.close();
			in.close();
			socket.close();
		}
		catch (IOException e){
			response="no-server";
		}
	}
}