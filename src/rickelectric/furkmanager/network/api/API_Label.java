package rickelectric.furkmanager.network.api;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.network.FurkBridge;
import rickelectric.furkmanager.utils.SettingsManager;

/**
 * Responsible For The Placing Of Files In Labeled Folders
 * 
 * @author Rick Lewis (Ionicle)
 * 
 */
public class API_Label extends API {

	private static ArrayList<FurkLabel> allLabels;

	public static ArrayList<FurkLabel> jsonLabels(JSONArray labels) {

		ArrayList<FurkLabel> fl = new ArrayList<FurkLabel>();

		for (int i = 0; i < labels.length(); i++) {
			JSONObject x = labels.getJSONObject(i);
			Object tid = x.get("id");
			if (tid != null && tid != JSONObject.NULL && !tid.equals("")) {
				String id = tid.toString();
				String name = x.getString("name");
				String parent = x.getString("id_labels");
				String color = x.get("color") == JSONObject.NULL ? null : x
						.getString("color");
				String bg = x.get("bg") == JSONObject.NULL ? null : x
						.getString("bg");
				int sorder = x.getInt("sorder");
				FurkLabel l = new FurkLabel(id, name);
				l.setParentID(parent);
				l.setColor(color);
				l.setBackground(bg);
				l.setSortOrder(sorder);

				fl.add(l);
			}
		}

		return fl;
	}

	public static ArrayList<FurkLabel> getAllCached() {
		return allLabels;
	}

	public static ArrayList<FurkLabel> getAll() {
		String json = FurkBridge.labelGet(false);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return null;

		allLabels = jsonLabels(re.getJSONArray("labels"));
		return allLabels;
	}

	public static FurkLabel add(FurkLabel l) {
		try {
			String url = FurkBridge.API_BASE()
					+ (SettingsManager.getInstance().useTunnel() ? "/?object=label&function=upsert&"
							: "/label/upsert?") + FurkBridge.key() + "&name="
					+ URLEncoder.encode(l.getName(), "utf-8");
			url += "&sorder=" + l.getSortOrder();
			if (l.getBackground() != null && !l.getBackground().equals(""))
				url += "&bg=" + URLEncoder.encode(l.getBackground(), "utf-8");

			if (l.getColor() != null && !l.getColor().equals(""))
				url += "&color=" + URLEncoder.encode(l.getColor(), "utf-8");

			if (l.getParentID() != null && !l.getParentID().equals(""))
				url += "&id_labels="
						+ URLEncoder.encode(l.getParentID(), "utf-8");

			String json = FurkBridge.jsonPost(url, false, false);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return null;
			l.setID(re.getJSONObject("label").getString("id"));
			if (allLabels != null)
				allLabels.add(l);
			return l;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean update(FurkLabel l) {
		if (l.getID() == null || l.getID().equals("")) {
			System.err
					.println("FurkLabel supplied has no id. Cannot update an IDless Label");
			return false;
		}
		try {
			String url = FurkBridge.API_BASE()
					+ (SettingsManager.getInstance().useTunnel() ? "/?object=label&function=upsert&"
							: "/label/upsert?") + FurkBridge.key() + "&id="
					+ URLEncoder.encode(l.getID(), "utf-8") + "&name="
					+ URLEncoder.encode(l.getName(), "utf-8");
			url += "&sorder=" + l.getSortOrder();

			if (l.getBackground() != null && !l.getBackground().equals(""))
				url += "&bg=" + URLEncoder.encode(l.getBackground(), "utf-8");

			if (l.getColor() != null && !l.getColor().equals(""))
				url += "&color=" + URLEncoder.encode(l.getColor(), "utf-8");

			if (l.getParentID() != null && !l.getParentID().equals(""))
				url += "&id_labels="
						+ URLEncoder.encode(l.getParentID(), "utf-8");

			String json = FurkBridge.jsonPost(url, false, false);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean linkToFiles(String labelID, String[] fileIDs) {
		try {
			if (labelID == null || labelID.length() == 0 || fileIDs == null
					|| fileIDs.length == 0)
				return false;
			String url = FurkBridge.API_BASE()
					+ (SettingsManager.getInstance().useTunnel() ? "/?object=label&function=link&"
							: "/label/link?") + FurkBridge.key()
					+ "&id_labels=" + labelID;
			for (String fid : fileIDs) {
				url += "&id_files=" + fid;
			}
			String json = FurkBridge.jsonPost(url, false, false);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean unlinkFromFiles(String labelID, String[] fileIDs) {
		try {
			if (labelID == null || labelID.length() == 0 || fileIDs == null
					|| fileIDs.length == 0)
				return false;
			String url = FurkBridge.API_BASE()
					+ (SettingsManager.getInstance().useTunnel() ? "/?object=label&function=unlink&"
							: "/label/unlink?") + FurkBridge.key()
					+ "&id_labels=" + labelID;
			for (String fid : fileIDs) {
				url += "&id_files=" + fid;
			}
			String json = FurkBridge.jsonPost(url, false, false);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean delete(FurkLabel l) {
		try {
			String url = FurkBridge.API_BASE()
					+ (SettingsManager.getInstance().useTunnel() ? "/?object=label&function=delete&"
							: "/label/delete?") + FurkBridge.key() + "&id="
					+ URLEncoder.encode(l.getID(), "utf-8");
			String json = FurkBridge.jsonPost(url, false, false);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static ArrayList<FurkLabel> childrenOf(FurkLabel parent) {
		String fsync = parent.getID();
		ArrayList<FurkLabel> labels = new ArrayList<FurkLabel>();
		for (FurkLabel l : getAllCached()) {
			if (l.getParentID() == null || l.getParentID().equals("0")) {
				if (parent.equals(root()))
					labels.add(l);
			} else if (l.getParentID().equals(fsync))
				labels.add(l);
		}
		return labels;
	}

	public static FurkLabel root() {
		for (FurkLabel l : getAllCached()) {
			if (l.getName().equals("0-FurkManagerRoot"))
				return l;
		}
		FurkLabel root = new FurkLabel(null, "0-FurkManagerRoot");
		root = add(root);
		return root;
	}

	public static void flush() {
		try {
			allLabels.removeAll(allLabels);
		} catch (Exception e) {
		}
		allLabels = null;
	}

}
