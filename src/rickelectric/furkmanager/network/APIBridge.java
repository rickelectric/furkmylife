package rickelectric.furkmanager.network;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONObject;

public class APIBridge extends FurkBridge {

	private static String api_key = null;

	public static String key() {
		if (api_key == null)
			return null;
		return "api_key=" + api_key;
	}

	public static final String API_BASE = "https://www.furk.net/api";

	public static void initialize(String api_key) {
		APIBridge.api_key = api_key;
	}

	private static boolean overrideCache = false;
	public static boolean dummy = false;

	public static boolean overrideCache() {
		return overrideCache;
	}

	public static void overrideCache(boolean o) {
		overrideCache = o;
	}

	public static String dlAdd(int type, String link) {
		if (api_key == null)
			return null;
		String dest = API_BASE + "/dl/add?" + key();
		if (type == DL_ADD_URL)
			dest += "&url=";
		else if (type == DL_ADD_INFO_HASH)
			dest += "&info_hash=";
		else if (type == DL_ADD_TORRENT) {
			try {
				Part[] parts = new Part[] { new StringPart("api_key", api_key),
						new FilePart("file", new File(link)) };
				String stream = StreamDownloader.postDataPartStream(API_BASE
						+ "/dl/add", parts);
				return stream;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		} else
			throw new RuntimeException("Invalid Type Parameter");
		dest += link;
		return jsonPost(dest, false, false);

	}

	/**
	 * 
	 * @param get
	 *            Get a file by ID(<b>API_Access.GET_ID</b>),
	 *            Status(<b>API_Access.GET_STATUS</b>) or Neither of the
	 *            Aforementioned(<b>API_Access.GET_ALL</b>)
	 * @param param
	 *            Pass the ID of the file (if <b>GET_ID</b> selected), Status
	 *            "active" or "failed" (if <b>GET_STATUS</b> selected) or null
	 *            (if <b>GET_ALL</b> is selected)
	 * @param lim_ofs
	 *            An array with the limit and offset as the 1st and 2nd items
	 *            respectively (For Multi-page results). Leave null if all items
	 *            are to be returned.
	 * @return A JSON String of results returned by Furk.net
	 */
	public static String dlGet(int get, String param, int[] lim_ofs,
			boolean checkCache) {
		if (api_key == null)
			return null;
		if (dummy) {
			try {
				return StreamDownloader
						.fileToString("./JSON_Samples_And_Docs/JSON-FurkDl.txt");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		String dest = API_BASE + "/dl/get?" + key();
		if (get == GET_ID)
			dest += "&id=" + param;
		else if (get == GET_STATUS)
			dest += "&dl_status=" + param;
		else if (get == GET_ALL)
			;
		else
			throw new RuntimeException("Invalid GET Method");

		if (lim_ofs != null && lim_ofs.length == 2) {
			if (lim_ofs[0] < 1)
				throw new RuntimeException("Invalid Limit");
			if (lim_ofs[1] < 0)
				throw new RuntimeException("Invalid Offset");
			dest += "&limit=" + lim_ofs[0] + "&offset=" + lim_ofs[1];
		}
		return jsonGet(dest, checkCache, false);
	}

	public static String fileInfo(String info_hash) {
		if (api_key == null)
			return null;
		if (info_hash == null || info_hash.length() == 0)
			return null;
		String dest = API_BASE + "/file/info?" + key() + "&info_hash="
				+ info_hash;

		return jsonPost(dest, true, true);
	}

	public static String TFileInfo(String fileID) {
		if (api_key == null)
			return null;
		String dest = API_BASE + "/file/get?" + key() + "&id=" + fileID
				+ "&t_files=1";
		return jsonPost(dest, true, true);
	}

	public static String fileGet(int get, String param, int[] lim_ofs,
			String[] sort, boolean checkCache) {
		if (api_key == null)
			return null;
		if (dummy) {
			try {
				return StreamDownloader
						.fileToString("./JSON_Samples_And_Docs/JSON-FurkFile.txt");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		String dest = API_BASE + "/file/get?" + key();
		if (get == GET_ID)
			dest += "&id=" + param;
		else if (get == GET_TYPE)
			dest += "&type=" + param;
		else if (get == GET_TRASH)
			dest += "&unlinked=1";
		else if (get != GET_ALL)
			throw new RuntimeException("Invalid File GET Type in parameter 1");

		if (lim_ofs != null && lim_ofs.length == 2) {
			if (lim_ofs[0] < 1)
				throw new RuntimeException("Invalid Limit <-- >=1 -->");
			if (lim_ofs[1] < 0)
				throw new RuntimeException("Invalid Offset <-- >=0 -->");
			dest += "&limit=" + lim_ofs[0] + "&offset=" + lim_ofs[1];
		}
		if (sort != null && sort.length == 2) {
			if (!(sort[0].equals("name") || sort[0].equals("size") || sort[0]
					.equals("ctime")))
				throw new RuntimeException(
						"Invalid Column <-- name / size / ctime -->");
			if (!(sort[1].equals("asc") || sort[1].equals("desc")))
				throw new RuntimeException(
						"Invalid Sort Method <-- asc / desc -->");
			dest += "&sort_col=" + sort[0] + "&sort_type=" + sort[1];
		}
		return jsonPost(dest, checkCache, true);
	}

	public static String dlUnlink(String[] ids) {
		if (api_key == null)
			return null;
		if (ids.length == 0)
			return null;
		String destdl = API_BASE + "/dl/unlink";
		String jsil = "";
		for (int i = 0; i < ids.length; i++) {
			jsil += "id=" + ids[i];
			if (i != ids.length - 1)
				jsil += "&";
		}
		destdl += "?" + key() + "&" + jsil;
		return jsonGet(destdl, false, false);
	}

	public static String fileLinkstate(int action, String[] ids) {
		if (api_key == null)
			return null;
		String destfile = API_BASE + "/file/";
		switch (action) {
		case LINK_LINK:
			destfile += "link";
			break;
		case LINK_UNLINK:
			destfile += "unlink";
			break;
		case LINK_CLEAR:
			destfile += "clear";
			break;
		default:
			throw new RuntimeException("Invalid Link Method");
		}
		String jsil = "";
		for (int i = 0; i < ids.length; i++) {
			jsil += "id=" + ids[i];
			if (i != ids.length - 1)
				jsil += "&";
		}
		destfile += "?" + key() + "&" + jsil;
		return jsonGet(destfile, false, false);
	}

	public static String msgGet(boolean checkCache) {
		if (key() == null)
			return null;
		String dest = API_BASE + "/msg/get?" + key();
		return jsonGet(dest, checkCache, false);
	}

	public static String labelGet(boolean checkCache) {
		if (api_key == null)
			return null;
		if (dummy) {
			try {
				return StreamDownloader
						.fileToString("./JSON_Samples_And_Docs/JSON-FurkLabel.txt");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		String dest = API_BASE + "/label/get?" + key();
		return jsonGet(dest, checkCache, false);
	}

	public static String labelAdd(String name) {
		String dest = API_BASE + "/label/upsert?" + key();
		try {
			name = URLEncoder.encode(name, "utf-8");
		} catch (Exception e) {
		}
		dest += "&name=" + name;
		return jsonPost(dest, false, false);
	}

	public static String labelSave(String id, String name, String color,
			String sorder, String id_labels) {
		if (id == null)
			return null;
		if (name == null && color == null && sorder == null
				&& id_labels == null)
			return null;
		String dest = API_BASE + "/label/upsert?" + key();
		dest += "&id=" + id;
		if (name != null) {
			try {
				name = URLEncoder.encode(name, "utf-8");
			} catch (Exception e) {
			}
			dest += "&name=" + name;
		}
		if (color != null) {
			try {
				color = URLEncoder.encode(color, "utf-8");
			} catch (Exception e) {
			}
			dest += "&color=" + color;
		}
		if (sorder != null) {
			try {
				sorder = URLEncoder.encode(sorder, "utf-8");
			} catch (Exception e) {
			}
			dest += "&sorder=" + sorder;
		}
		if (id_labels != null) {
			try {
				id_labels = URLEncoder.encode(id_labels, "utf-8");
			} catch (Exception e) {
			}
			dest += "&id_labels=" + id_labels;
		}
		return jsonPost(dest, false, false);
	}

	// Sets the api key and returns if logged in sucessfully
	public static String userLogin(String username, String password) {
		if (api_key != null)
			throw new RuntimeException("Already Logged In. Log Out First.");
		if (username == null || password == null)
			throw new RuntimeException(
					"Neither 'username' nor 'password' should be null.");
		try {
			username = URLEncoder.encode(username, "utf-8");
		} catch (Exception e) {
		}
		try {
			password = URLEncoder.encode(password, "utf-8");
		} catch (Exception e) {
		}
		Part[] params = new Part[] { new StringPart("login", username),
				new StringPart("pwd", password) };

		String url = API_BASE + "/login/login";// ?login="+username+"&pwd="+password;
		String json = null;
		try {
			json = StreamDownloader.postDataPartStream(url, params);
		} catch (Exception e) {
			throw new RuntimeException("Connection Error");
		}
		JSONObject j = new JSONObject(json);
		if (j.getString("status").equals("error")) {
			throw new RuntimeException(j.getString("error") + " Tries Left: "
					+ j.getString("tries_left"));
		}
		return j.getString("api_key");
	}

	public static String userLoad() {
		if (api_key == null)
			return null;
		if (dummy) {
			try {
				return StreamDownloader
						.fileToString("./JSON_Samples_And_Docs/JSON-FurkUser.txt");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		String dest = API_BASE + "/account/info?" + key();
		return jsonPost(dest, false, false);
	}

	public static boolean userLogout() {
		if (api_key == null)
			return true;
		String url = API_BASE + "/login/logout?" + key();
		// String json =
		jsonGet(url, false, false);
		// JSONObject j = new JSONObject(json);
		// if (j.getString("status").equals("error"))
		// return false;
		api_key = null;
		return true;
	}

	public static String searchFiles(String txt, boolean checkCache) {
		return furkSearch(0, txt, checkCache);
	}

	public static String searchFurk(String txt, boolean checkCache) {
		return furkSearch(1, txt, checkCache);
	}

	public static String searchWeb(String txt, boolean checkCache) {
		return furkSearch(2, txt, checkCache);
	}

	protected static String furkSearch(int sMode, String txt, boolean checkCache) {
		if (api_key == null)
			return null;
		if (txt.length() == 0)
			return null;
		String frag = "";
		try {
			txt = URLEncoder.encode(txt, "utf-8");
		} catch (Exception e) {
		}
		if (sMode == 0) {
			frag = "file/get";
			txt = "&name_like=" + txt;
		}
		if (sMode == 1) {
			frag = "search";
			txt = "&q=" + txt;
		} else if (sMode == 2) {
			frag = "plugins/metasearch";
			txt = "&q=" + txt;
		}
		String url = API_BASE + "/" + frag + "?" + key();
		url += txt + "&limit=50";
		return jsonPost(url, checkCache, true);
	}

	public static boolean ping() {
		return ping(api_key);
	}

	public static boolean ping(String apiKey) {
		if (apiKey == null)
			return false;
		String url = API_BASE + "/ping?api_key=" + apiKey;
		try {
			String stat = StreamDownloader.getStringStream(url, 8);
			JSONObject j = new JSONObject(stat);
			String s = j.getString("status");
			if (s.equals("ok"))
				return true;
			else
				return false;
		} catch (Exception e) {
			throw new RuntimeException("Connection Error");
		}
	}

	public static synchronized String jsonGet(String dest, boolean cacheCheck,
			boolean perm) {
		if (api_key == null)
			return null;
		if (!overrideCache && cacheCheck) {
			String r = RequestCache.APIR.find(dest);
			if (r != null)
				return r;
		}
		try {
			String s = StreamDownloader.getStringStream(dest, 4);
			if (s != null)
				RequestCache.APIR.add(dest, s, perm);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Connection Error");
		}
	}

	public static synchronized String jsonPost(String dest, boolean cacheCheck,
			boolean perm) {
		if (api_key == null)
			return null;
		if (!overrideCache && cacheCheck) {
			String r = RequestCache.APIR.find(dest);
			if (r != null)
				return r;
		}
		try {
			String s = StreamDownloader.postStringStream(dest, 4);
			if (s != null)
				RequestCache.APIR.add(dest, s, perm);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Connection Error");
		}
	}

	public static String jsonPost(String url, Part[] parts) {
		if (api_key == null)
			return null;
		try {
			String s = StreamDownloader.postDataPartStream(url, parts);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Connection Error");
		}
	}

	public static String getKey() {
		return api_key;
	}

}
