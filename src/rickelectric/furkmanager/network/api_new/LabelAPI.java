package rickelectric.furkmanager.network.api_new;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.network.StreamDownloader;

public class LabelAPI implements APIModule {

	public static boolean dummy = false;
	
	private String apiKey;
	private ArrayList<FurkLabel> labels;
	private long lastUpdated;

	public LabelAPI(String apiKey) {
		this.apiKey = apiKey;
		labels = new ArrayList<FurkLabel>();
		lastUpdated = 0;
	}

	public synchronized boolean update() {
		if (System.currentTimeMillis() - lastUpdated < 10)
			return false;
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			String url = "/label/get";
			String json = dummy?StreamDownloader.getInstance().fileToString("./dummy/label.json")://TODO Remove
				APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("error"))
				return false;
			JSONArray labels = obj.getJSONArray("labels");
			this.labels = APIParser.parseLabelList(labels);
			lastUpdated = System.currentTimeMillis();
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	public ArrayList<FurkLabel> get() {
		if (System.currentTimeMillis() - lastUpdated > 30)
			for (int i = 0; i < 3; i++) {
				if (update())
					break;
			}
		if (labels == null)
			return null;
		ArrayList<FurkLabel> clone = new ArrayList<FurkLabel>();
		Iterator<FurkLabel> iter = labels.iterator();
		while (iter.hasNext()) {
			clone.add(iter.next());
		}
		return clone;
	}

	public FurkLabel insert(String name, String id_labels, int sorder,
			String color, String bg) {
		// TODO Check uniqueness of name before inserting (when cache Map is
		// implemented)
		FurkLabel l = upsert(null, name, id_labels, sorder, color, bg);
		if (l != null) {
			labels.add(l);
		}
		return l;
	}

	public boolean update(String id, String name, String id_labels, int sorder,
			String color, String bg) {
		if (id == null)
			return false;
		Object o = upsert(id, name, id_labels, sorder, color, bg);
		return o != null;
	}

	private FurkLabel upsert(String id, String name, String id_labels,
			int sorder, String color, String bg) {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			parts.addPart("name", new StringBody(name));
			
			if(id!=null)
				parts.addPart("id", new StringBody(id));
			if (id_labels == null || id_labels.length() == 0) {
				parts.addPart("id_labels", new StringBody("0"));
			} else {
				parts.addPart("id_labels", new StringBody(id_labels));
			}
			if (sorder >= 0) {
				parts.addPart("sorder", new StringBody("" + sorder));
			}
			if (color != null) {
				parts.addPart("color", new StringBody(color));
			}
			if (bg != null) {
				parts.addPart("bg", new StringBody(bg));
			}
			String url = "/label/upsert";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("error"))
				return null;
			JSONObject l = obj.getJSONObject("label");
			return APIParser.parseLabel(l);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return null;
	}

	public boolean link(String id_labels, String[] id_files) {
		if (id_labels == null || id_labels.equals("") || id_files == null
				|| id_files.length == 0)
			return false;
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			parts.addPart("id_labels", new StringBody(id_labels));
			for (String idf : id_files) {
				parts.addPart("id_files", new StringBody(idf));
			}
			String url = "/label/link";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("ok"))
				return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	public boolean unlink(String id_labels, String[] id_files) {
		if (id_labels == null || id_labels.equals("") || id_files == null
				|| id_files.length == 0)
			return false;
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			parts.addPart("id_labels", new StringBody(id_labels));
			for (String idf : id_files) {
				parts.addPart("id_files", new StringBody(idf));
			}
			String url = "/label/unlink";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("ok"))
				return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	public boolean delete(String id) {
		if (id == null || id.equals(""))
			return false;
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			parts.addPart("id", new StringBody(id));
			String url = "/label/delete";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("ok")){
				for (Iterator<FurkLabel> iterator = labels.iterator(); iterator
						.hasNext();) {
					FurkLabel f = iterator.next();
					if(f.getID().equals(id)){
						iterator.remove();
						break;
					}
				}
				return true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

}
