package rickelectric.furkmanager.network.api;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.FurkDownload;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.network.FurkBridge;

/**
 * Access Furk Downloads
 * 
 * @author Rick Lewis (Ionicle)
 * 
 */
public class API_Download extends API{

	private static ArrayList<FurkDownload> downloadList;

	public enum GET_STATUS {
		STATUS_ACTIVE, STATUS_FAILED, STATUS_ALL
	};

	public static final GET_STATUS STATUS_ACTIVE = GET_STATUS.STATUS_ACTIVE,
			STATUS_FAILED = GET_STATUS.STATUS_FAILED,
			STATUS_ALL = GET_STATUS.STATUS_ALL;

	private static ArrayList<FurkDownload> jsonDownloads(JSONArray dls) {
		int len = dls.length();
		ArrayList<FurkDownload> dlArr = new ArrayList<FurkDownload>();
		for (int i = 0; i < len; i++) {
			JSONObject dl = dls.getJSONObject(i);
			Object name = dl.get("name");
			if (name != JSONObject.NULL && !name.equals("")) {
				FurkDownload d = new FurkDownload(dl.getString("id"),
						dl.getString("name"), dl.getString("info_hash"),
						dl.getLong("size"), dl.getString("adding_dt"));
				d.setDateCompleted(dl.getString("finish_dt"));
				d.setDlStatus(dl.getString("dl_status"));
				d.setMtime(dl.getString("mtime"));
				d.bytes(dl.getLong("bytes"), dl.getLong("up_bytes"));

				if (d.getDlStatus().equals("failed"))
					d.setFailReason(dl.getString("fail_reason"));
				d.setReady(dl.get("is_ready") != JSONObject.NULL
						&& dl.getInt("is_ready") == 1);
				d.dlStatus(dl.getString("active_status"),
						dl.getInt("peers"), dl.getInt("seeders"),
						dl.getLong("up_speed"), dl.getLong("speed"),
						dl.getString("have"));
				dlArr.add(d);
			}
		}
		return dlArr;
	}

	private static String lastError;

	public static String getLastError() {
		return lastError;
	}

	public static ArrayList<FurkDownload> getAllCached() {
		return downloadList;
	}

	public static ArrayList<FurkDownload> getAll() {
		downloadList = get(STATUS_ALL, -1, -1);
		return downloadList;
	}

	public static ArrayList<FurkDownload> getAll(GET_STATUS status) {
		return get(status, -1, -1);
	}

	public static ArrayList<FurkDownload> get(GET_STATUS status, int limit,
			int offset) {
		int[] limoffs = null;
		if (limit > 0 && offset >= 0) {
			limoffs = new int[2];
			limoffs[0] = limit;
			limoffs[1] = offset;
		}
		String json = null;
		if (status == STATUS_ACTIVE)
			json = APIBridge.dlGet(FurkBridge.GET_STATUS, "active", limoffs,
					true);
		else if (status == STATUS_FAILED)
			json = APIBridge.dlGet(FurkBridge.GET_STATUS, "failed", limoffs,
					true);
		else
			json = APIBridge.dlGet(FurkBridge.GET_ALL, null, limoffs, true);
		if (json == null)
			return null;
		JSONObject j = new JSONObject(json);
		if (j.getString("status").equals("error"))
			return null;
		JSONArray ja = j.getJSONArray("dls");
		return jsonDownloads(ja);
	}

	public static FurkDownload get(String downloadID) throws Exception {
		String json = APIBridge.dlGet(FurkBridge.GET_ID, downloadID, null,
				false);
		JSONObject j = new JSONObject(json);
		if (j.getString("status").equals("error"))
			return null;
		try {
			if (j.getInt("found_dls") == 0)
				throw new Exception("No Downloads Found");
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				return null;
			throw e;
		}
		return jsonDownloads(j.getJSONArray("dls")).get(0);
	}

	public static boolean addHash(String hash) {
		String json = APIBridge.dlAdd(FurkBridge.DL_ADD_INFO_HASH, hash);
		JSONObject j = new JSONObject(json);
		if (j.getString("status").equals("error")) {
			lastError = j.getString("error");
			return false;
		}
		getAll();
		return true;
	}

	public static boolean addURL(String url) {
		String json = APIBridge.dlAdd(FurkBridge.DL_ADD_URL, url);
		JSONObject j = new JSONObject(json);
		if (j.getString("status").equals("error")) {
			lastError = j.getString("error");
			return false;
		}
		getAll();
		return true;
	}

	public static boolean addTorrentFile(String filename) {
		String json = APIBridge.dlAdd(FurkBridge.DL_ADD_TORRENT, filename);
		JSONObject j = new JSONObject(json);
		if (j.getString("status").equals("error")) {
			lastError = j.getString("error");
			return false;
		}
		getAll();
		return true;
	}

	public static boolean unlink(String[] dlIDs) {
		String json = APIBridge.dlUnlink(dlIDs);
		JSONObject j = new JSONObject(json);
		if (j.get("status").equals("error")) {
			lastError = j.getString("error");
			return false;
		}
		for (String did : dlIDs) {
			Iterator<FurkDownload> i = downloadList.iterator();
			while (i.hasNext()) {
				FurkDownload d = i.next();
				String id = d.getId();

				if (id.equals(did))
					i.remove();
			}
		}
		return true;
	}

	public static void flush() {
		try{downloadList.removeAll(downloadList);}catch(Exception e){}
		downloadList=null;
		lastError = "";
	}

}
