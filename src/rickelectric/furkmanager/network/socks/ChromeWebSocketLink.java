package rickelectric.furkmanager.network.socks;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import rickelectric.furkmanager.data.DataToJSON;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.views.windows.AddDownloadFrame;
import rickelectric.furkmanager.views.windows.FurkFileView;

/**
 * WebSocketServer implementation.
 * Connect To The Google Chrome Extension
 */
public class ChromeWebSocketLink extends WebSocketServer {

	WebSocket conn;

	public ChromeWebSocketLink(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public ChromeWebSocketLink(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake){
		this.conn = conn;
		System.out.println("Browser Connected");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		if (conn.equals(this.conn)) {
			System.out.println("Browser Disconnected");
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		// TODO Receive Event Here
		messageEvent(message);
		System.out.println("Message Recieved: " + message);
	}

	private void messageEvent(String message){
		JSONObject o=new JSONObject(message);
		String cl=o.getString("blob");
		if(cl.equals("request")){
			String type=o.getString("type");
			if(type.equals("files")){
				int limit=o.getInt("limit");
				int offset=o.getInt("offset");
				
				String ret=DataToJSON.getFiles(limit, offset);
				System.out.println(ret);
				sendMessage(ret);
			}
			else if(type.equals("search")){
				String term=o.getString("q");
				String ret=DataToJSON.search(term);
				sendMessage(ret);
			}
			else if(type.equals("view")){
				int num=o.getInt("num");
				List<FurkFile> files=API_File.getAllCached();
				if(files!=null){
					APIObject a=files.get(num-1);
					if(a instanceof FurkFile)
						new FurkFileView((FurkFile)a);
					
				}
			}
		}else if(cl.equals("link")){
			String link=o.getString("link");
			new AddDownloadFrame(link);
		}
		else if(cl.equals("ping")){
			JSONObject ro=new JSONObject();
			ro.put("status", "ok");
			String key=API.key();
			if(key==null)
				ro.put("state","not_logged_in");
			else{
				ro.put("state", "ready");
				ro.put("api_key",key);
			}
			sendMessage(ro.toString());
		}
	}

	public void sendMessage(String message) {
		conn.send(message);
	}
	
	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			System.err.println("Socket Error!");
		}
	}
}
