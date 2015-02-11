package rickelectric.furkmanager.network.api;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIMessage;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.network.FurkBridge;
import rickelectric.furkmanager.views.panels.APIMessagePanel;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class API {

	private static ArrayList<APIMessage> msgCache;
	private static APIMessagePanel msgPanel;

	public static void init(String key) {
		FurkBridge.initialize(key);
		msgCache = new ArrayList<APIMessage>();
		msgPanel = new APIMessagePanel();
	}

	public static String key() {
		return FurkBridge.getKey();
	}

	public static Iterator<APIMessage> messages() {
		if (msgCache == null)
			return null;
		return msgCache.iterator();
	}

	protected static void setMessages(JSONArray j) {  
		if (j == null)
			return;
		msgCache.removeAll(msgCache);
		for (int i = 0; i < j.length(); i++) {
			msgCache.add(API.jsonMessage(j.getJSONObject(i)));
		}
		msgPanel.update();
		try {
			FurkManager.getMainWindow().loadMessages();
		} catch (Exception e) {
		}
	}

	private static APIMessage jsonMessage(JSONObject o) {
		String type = o.getString("type");
		String key = o.getString("key");
		APIMessage msg = new APIMessage(type, key);
		try {
			String txt = o.getString("txt");
			txt = HTMLEntities.unhtmlentities(txt);
			msg.setText(txt);
		} catch (Exception e) {
		}
		return msg;
	}

	public static void flushAll() {
		API_Download.flush();
		API_File.flush();
		API_Label.flush();
		API_UserData.flush();
		System.gc();
	}

	public enum SearchMode {
		FURKSEARCH, METASEARCH
	}

	public static final SearchMode FURKSEARCH = SearchMode.FURKSEARCH,
			METASEARCH = SearchMode.METASEARCH;

	public static ArrayList<APIObject> search(String text, SearchMode mode) {
		String jsonResult = null;
		if (mode == FURKSEARCH)
			jsonResult = FurkBridge.searchFurk(text, true);
		else if (mode == METASEARCH)
			jsonResult = FurkBridge.searchWeb(text, true);
		else {
		}
		if (jsonResult == null)
			return null;

		JSONObject jo = new JSONObject(jsonResult);
		if (jo.getString("status").equals("error"))
			return null;

		JSONArray files = jo.getJSONArray("files");

		ArrayList<APIObject> ffarray = API_File.jsonFiles(files);
		return ffarray;
	}

	public static APIMessagePanel getMessagePanel() {
		return msgPanel;
	}

	public static ArrayList<APIMessage> getMessages() {
		return msgCache;
	}

	public static void clear() {
		flushAll();
		FurkBridge.userLogout();
	}

}
