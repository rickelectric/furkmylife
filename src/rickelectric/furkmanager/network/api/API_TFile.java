package rickelectric.furkmanager.network.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.network.APIBridge;

/**
 * Access Files Within Furk Files
 * 
 * @author Rick Lewis (Ionicle)
 * 
 */
public class API_TFile extends API {

	public static ArrayList<FurkTFile> jsonFiles(JSONArray tf) {
		ArrayList<FurkTFile> tfiles = new ArrayList<FurkTFile>();
		for (int i = 0; i < tf.length(); i++) {
			JSONObject curr = tf.getJSONObject(i);
			String name, dl, pls, path, ct;
			long size;
			try {
				name = curr.getString("name");
			} catch (Exception e) {
				name = "";
			}
			try {
				size = curr.getLong("size");
			} catch (Exception e) {
				size = 0L;
			}
			try {
				dl = curr.getString("url_dl");
			} catch (Exception e) {
				dl = "";
			}
			try {
				pls = curr.getString("url_pls");
			} catch (Exception e) {
				pls = "";
			}
			try {
				path = curr.getString("path");
			} catch (Exception e) {
				path = "";
			}
			try {
				ct = curr.getString("ct");
			} catch (Exception e) {
				ct = "default";
			}

			FurkTFile t = new FurkTFile(name, size, dl, pls, path, ct);

			if (ct.toLowerCase().contains("video")) {
				String player, thumbURL;
				int width, height, thumbHeight;
				try {
					player = curr.getString("player");
				} catch (Exception e) {
					player = "";
				}
				try {
					thumbURL = curr.getString("url_tn");
				} catch (Exception e) {
					thumbURL = "";
				}
				try {
					width = curr.getInt("width");
				} catch (Exception e) {
					width = 0;
				}
				try {
					height = curr.getInt("height");
				} catch (Exception e) {
					height = 0;
				}
				try {
					thumbHeight = curr.getInt("tn_height");
				} catch (Exception e) {
					thumbHeight = 0;
				}
				t.mediaInfo(player, thumbURL, width, height, thumbHeight);
			}

			tfiles.add(t);
		}

		return tfiles;
	}

	public static ArrayList<FurkTFile> getFrom(FurkFile f) {
		String json = APIBridge.TFileInfo(f.getID());
		JSONObject o = new JSONObject(json);
		if (o.getString("status").equals("error"))
			return null;
		JSONArray tf = o.getJSONArray("files").getJSONObject(0)
				.getJSONArray("t_files");

		return jsonFiles(tf);

	}

}