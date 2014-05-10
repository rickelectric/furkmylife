package rickelectric.furkmanager.network;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.APIMessage;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkDownload;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.models.FurkUserData;
import rickelectric.furkmanager.models.URI_Enums;
import rickelectric.furkmanager.models.URI_Enums.Prefs_Flags;

public class API {

	private static ArrayList<String> msgCache;

	public static void init(String key) {
		APIBridge.initialize(key);
		msgCache = new ArrayList<String>();
	}

	public static String key() {
		return APIBridge.api_key;
	}

	public Iterator<String> messages() {
		if (msgCache == null)
			return null;
		return msgCache.iterator();
	}

	private static void setMessages(JSONArray j) {
		if (j == null)
			return;
		msgCache = new ArrayList<String>();
		for (int i = 0; i < j.length(); i++) {
			msgCache.add(j.getString(i));
		}
	}

	/**
	 * Access Furk Files
	 * 
	 * @author Rick Lewis (Ionicle)
	 * 
	 */
	public static class File {

		public enum GET_TYPE {
			GET_DELETED, GET_FINISHED
		};

		public static final GET_TYPE GET_DELETED = GET_TYPE.GET_DELETED,
				GET_FINISHED = GET_TYPE.GET_FINISHED;

		private static ArrayList<APIObject> fileList;

		public static ArrayList<APIObject> jsonFiles(JSONArray files) {

			ArrayList<APIObject> fl = new ArrayList<APIObject>();

			for (int i = 0; i < files.length(); i++) {
				JSONObject x = (JSONObject) files.get(i);
				Object tid = x.get("name");
				if (tid != JSONObject.NULL) {

					try {
						Object fid = x.get("id");
						if (fid == null || fid == JSONObject.NULL
								|| fid.equals(""))
							throw new Exception("No ID");
						String id = null, name = "", info_hash = "", type = null, url_dl = "", url_pls = "", url_page = "", fstatus = "", del_reason = null;
						String[] thumbs = null, ss = null;
						String[] id_labels = null;
						int is_linked = 0, is_ready = 0;
						long size = 0;
						@SuppressWarnings("unchecked")
						Iterator<String> keys = x.keys();
						while (keys.hasNext()) {
							try {
								String t = keys.next();
								if (t.equals("id"))
									id = (String) x.get(t);
								if (t.equals("name"))
									name = (String) x.get(t);
								if (t.equals("info_hash"))
									info_hash = (String) x.get(t);
								if (t.equals("type"))
									type = (String) x.get(t);
								if (t.equals("size"))
									size = x.getLong(t);
								if (t.equals("is_linked"))
									is_linked = x.getInt(t);
								if (t.equals("del_reason"))
									del_reason = x.getString(t);
								if (t.equals("url_dl"))
									url_dl = x.getString(t);
								if (t.equals("url_pls"))
									url_pls = x.getString(t);
								if (t.equals("url_page"))
									url_page = x.getString(t);
								if (t.equals("status"))
									fstatus = x.getString(t);
								if (t.equals("is_ready"))
									is_ready = x.getInt(t);
								if (t.equals("id_labels")) {
									JSONArray ja = x.getJSONArray("id_labels");
									id_labels = new String[ja.length()];
									for (int q = 0; q < ja.length(); q++) {
										id_labels[q] = ja.getString(q);
									}
								}
								if (t.equals("ss_urls_tn")) {
									JSONArray tn = x.getJSONArray(t);
									JSONArray ssh = x.getJSONArray("ss_urls");
									thumbs = new String[tn.length()];
									ss = new String[ssh.length()];
									for (int q = 0; q < tn.length(); q++) {
										thumbs[q] = tn.getString(q);
										ss[q] = ssh.getString(q);
									}

								}
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
						FurkFile fi = new FurkFile(name, info_hash, size, id,
								url_dl, url_pls, type, fstatus, url_page,
								is_linked, is_ready);
						fi.setDeletedReason(del_reason);
						fi.setThumbs(thumbs);
						fi.setScreenshots(ss);
						fi.setIdLabels(id_labels);
						fl.add(fi);
					} catch (Exception e) {
						String name = x.getString("name"), hash = x
								.getString("info_hash");
						long size = x.getLong("size");
						APIObject a = new APIObject(name, hash, size);
						fl.add(a);
					}
				}
			}

			return fl;
		}

		public static ArrayList<APIObject> getAllCached() {
			return fileList;
		}

		public static ArrayList<APIObject> getAllFinished() {
			fileList = get(GET_FINISHED, -1, -1);
			return fileList;
		}

		public static ArrayList<APIObject> getAllDeleted() {
			return get(GET_DELETED, -1, -1);
		}

		public static ArrayList<APIObject> get(GET_TYPE type, int limit,
				int offset) {
			int[] limoffs = null;
			if (limit > 0 && offset >= 0) {
				limoffs = new int[2];
				limoffs[0] = limit;
				limoffs[1] = offset;
			}
			String json = null;
			if (type == GET_FINISHED)
				json = APIBridge.fileGet(APIBridge.GET_ALL, null, limoffs,
						null, true);
			else if (type == GET_DELETED)
				json = APIBridge.fileGet(APIBridge.GET_TRASH, null, limoffs,
						null, true);
			else
				throw new IllegalArgumentException(
						"Invalid GET Type Passed as argument 1");
			if (json == null)
				return null;
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return null;
			Object m = re.get("messages");
			if (m != null && m instanceof JSONArray)
				setMessages((JSONArray) m);
			return jsonFiles(re.getJSONArray("files"));
		}

		public static APIObject get(String fileID) {
			if (fileID == null || fileID.equals(""))
				return null;
			String json = APIBridge.fileGet(APIBridge.GET_ID, fileID, null,
					null, true);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return null;
			return jsonFiles(re.getJSONArray("files")).get(0);
		}

		public static APIObject info(String hash) {
			String json = APIBridge.fileInfo(hash);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return null;
			ArrayList<APIObject> al = jsonFiles(re.getJSONArray("files"));
			if (al.size() == 0)
				return null;
			return al.get(0);
		}

		public static boolean link(String[] fileIDs) {
			String json = APIBridge.fileLinkstate(APIBridge.LINK_LINK, fileIDs);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return false;
			for (String id : fileIDs)
				fileList.add(info(id));
			return true;
		}

		public static boolean unlink(String[] fileIDs) {
			String json = APIBridge.fileLinkstate(APIBridge.LINK_UNLINK,
					fileIDs);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return false;
			for (String fid : fileIDs) {
				Iterator<APIObject> i = fileList.iterator();
				while (i.hasNext()) {
					APIObject f = i.next();
					if (f instanceof FurkFile) {
						String id = ((FurkFile) f).getID();
						if (id.equals(fid))
							i.remove();
					}
				}
			}
			return true;
		}

		public static boolean clear(String[] fileIDs) {
			String json = APIBridge
					.fileLinkstate(APIBridge.LINK_CLEAR, fileIDs);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return false;
			return true;
		}

		public static ArrayList<APIObject> find(String s) {
			String jsonResult = APIBridge.searchFiles(s, true);
			if (jsonResult == null)
				return null;

			JSONObject re = new JSONObject(jsonResult);
			if (re.getString("status").equals("error"))
				throw new RuntimeException("Status Error");
			if (re.getInt("found_files") == 0)
				return null;

			JSONArray files = re.getJSONArray("files");
			return jsonFiles(files);
		}

		public static List<FurkFile> childrenOf(FurkLabel fsync) {
			String id = fsync.getID();
			if (fsync.getName().equals("0-FurkManagerRoot"))
				id = "0";
			ArrayList<FurkFile> files = new ArrayList<FurkFile>();
			for (APIObject f : getAllCached()) {
				if (f instanceof FurkFile) {
					String[] labels = ((FurkFile) f).getIdLabels();
					if (labels == null || labels.length == 0) {
						if (id.equals("0")) {
							files.add((FurkFile) f);
						}
					} else {
						for (String s : labels) {
							if (id.equals(s)) {
								files.add((FurkFile) f);
								break;
							}
						}
					}
				}
			}
			return files;
		}

	}

	/**
	 * Access Files Within Furk Files
	 * 
	 * @author Rick Lewis (Ionicle)
	 * 
	 */
	public static class TFile {

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

	/**
	 * Access Furk Downloads
	 * 
	 * @author Rick Lewis (Ionicle)
	 * 
	 */
	public static class Download {

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
				json = APIBridge.dlGet(APIBridge.GET_STATUS, "active", limoffs,
						true);
			else if (status == STATUS_FAILED)
				json = APIBridge.dlGet(APIBridge.GET_STATUS, "failed", limoffs,
						true);
			else
				json = APIBridge.dlGet(APIBridge.GET_ALL, null, limoffs, true);
			if (json == null)
				return null;
			JSONObject j = new JSONObject(json);
			if (j.getString("status").equals("error"))
				return null;
			JSONArray ja = j.getJSONArray("dls");
			return jsonDownloads(ja);
		}

		public static FurkDownload get(String downloadID) throws Exception {
			String json = APIBridge.dlGet(APIBridge.GET_ID, downloadID, null,
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
			String json = APIBridge.dlAdd(APIBridge.DL_ADD_INFO_HASH, hash);
			JSONObject j = new JSONObject(json);
			if (j.getString("status").equals("error")) {
				lastError = j.getString("error");
				return false;
			}
			getAll();
			return true;
		}

		public static boolean addURL(String url) {
			String json = APIBridge.dlAdd(APIBridge.DL_ADD_URL, url);
			JSONObject j = new JSONObject(json);
			if (j.getString("status").equals("error")) {
				lastError = j.getString("error");
				return false;
			}
			getAll();
			return true;
		}

		public static boolean addTorrentFile(String filename) {
			String json = APIBridge.dlAdd(APIBridge.DL_ADD_TORRENT, filename);
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

	}

	/**
	 * Responsible For The Placing Of Files In Labeled Folders
	 * 
	 * @author Rick Lewis (Ionicle)
	 * 
	 */
	public static class Label {

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
			String json = APIBridge.labelGet(false);
			JSONObject re = new JSONObject(json);
			if (re.get("status").equals("error"))
				return null;

			allLabels = jsonLabels(re.getJSONArray("labels"));
			return allLabels;
		}

		public static FurkLabel add(FurkLabel l){
			try {
				String url = APIBridge.API_BASE + "/label/upsert?"
						+ APIBridge.key() + "&name="
						+ URLEncoder.encode(l.getName(), "utf-8");
				url += "&sorder=" + l.getSortOrder();
				if (l.getBackground() != null && !l.getBackground().equals(""))
					url += "&bg="
							+ URLEncoder.encode(l.getBackground(), "utf-8");

				if (l.getColor() != null && !l.getColor().equals(""))
					url += "&color=" + URLEncoder.encode(l.getColor(), "utf-8");

				if (l.getParentID() != null && !l.getParentID().equals(""))
					url += "&id_labels="
							+ URLEncoder.encode(l.getParentID(), "utf-8");

				String json = APIBridge.jsonPost(url, false, false);
				JSONObject re = new JSONObject(json);
				if (re.get("status").equals("error"))
					return null;
				l.setID(re.getJSONObject("label").getString("id"));
				if(allLabels!=null) allLabels.add(l);
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
				String url = APIBridge.API_BASE + "/label/upsert?"
						+ APIBridge.key() + "&id="
						+ URLEncoder.encode(l.getID(), "utf-8") + "&name="
						+ URLEncoder.encode(l.getName(), "utf-8");
				url += "&sorder=" + l.getSortOrder();

				if (l.getBackground() != null && !l.getBackground().equals(""))
					url += "&bg="
							+ URLEncoder.encode(l.getBackground(), "utf-8");

				if (l.getColor() != null && !l.getColor().equals(""))
					url += "&color=" + URLEncoder.encode(l.getColor(), "utf-8");

				if (l.getParentID() != null && !l.getParentID().equals(""))
					url += "&id_labels="
							+ URLEncoder.encode(l.getParentID(), "utf-8");

				String json = APIBridge.jsonPost(url, false, false);
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
				String url = APIBridge.API_BASE + "/label/link?"
						+ APIBridge.key() + "&id_labels=" + labelID;
				for (String fid : fileIDs) {
					url += "&id_files=" + fid;
				}
				String json = APIBridge.jsonPost(url, false, false);
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
				String url = APIBridge.API_BASE + "/label/unlink?"
						+ APIBridge.key() + "&id_labels=" + labelID;
				for (String fid : fileIDs) {
					url += "&id_files=" + fid;
				}
				String json = APIBridge.jsonPost(url, false, false);
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
				String url = APIBridge.API_BASE + "/label/delete?"
						+ APIBridge.key() + "&id="
						+ URLEncoder.encode(l.getID(), "utf-8");
				String json = APIBridge.jsonPost(url, false, false);
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
			for (FurkLabel l : getAllCached()){
				if(l.getParentID()==null || l.getParentID().equals("0")){
					if(parent.equals(root()))
						labels.add(l);
				}
				else if (l.getParentID().equals(fsync))
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
			root=add(root);
			return root;
		}

	}

	public static class Message {

		public static ArrayList<APIMessage> jsonMessages(JSONArray a) {
			if (a == null)
				return null;
			ArrayList<APIMessage> msgs = new ArrayList<APIMessage>();
			for (int x = 0; x < a.length(); x++) {
				JSONObject curr = a.getJSONObject(x);
				String type = curr.getString("type");
				String key = curr.getString("key");
				APIMessage m = new APIMessage(type, key);
				try {
					String text = curr.getString("text");
					if (text != JSONObject.NULL)
						m.setText(text);
				} catch (Exception e) {
				}
				try {
					int code = curr.getInt("code");
					m.setCode(code);
				} catch (Exception e) {
				}
				msgs.add(m);
			}

			return msgs;
		}

		public static ArrayList<APIMessage> getAll() {
			String re = APIBridge.msgGet(false);
			JSONObject o = new JSONObject(re);
			JSONArray mlist = o.getJSONArray("messages");
			if (mlist == null || mlist == JSONObject.NULL)
				return null;
			ArrayList<APIMessage> msgs = API.Message.jsonMessages(mlist);
			return msgs;
		}

	}

	public static class UserData {
		
		private static boolean isLoaded=false;

		public static void loadUserData(){
			String json = null;
			int numFails = 0;
			while (json == null)
				try {
					json = APIBridge.userLoad();
				} catch (RuntimeException e) {
					numFails++;
					if (numFails > 5)
						throw e;
				}

			JSONObject user = new JSONObject(json);
			if (user.getString("status").equals("error"))
				throw new RuntimeException("Error Obtaining User Data");

			Object usage = user.get("dls_usage");
			if (usage != null && usage != JSONObject.NULL) {
				FurkUserData.DownloadsUsage.load((JSONObject) usage);
			}

			JSONObject juser = user.getJSONObject("user");
			FurkUserData.User.load(juser);

			JSONArray bwStats = user.getJSONArray("bw_stats");
			JSONArray subnet = user.getJSONArray("net_stats");
			FurkUserData.BandwidthStats.load(bwStats, subnet);
			
			isLoaded=true;
		}

		/**
		 * Should Only Be Called From Within The <i>FurkUserData</i> class, and
		 * when all enum values are updated.
		 */
		public static void saveUserPrefs() {
			Prefs_Flags flag = FurkUserData.User.flags;
			String prefs_flags = "prefs_flags="
					+ URI_Enums.Prefs_Flags.getValue(flag);

			String prefs_json = "prefs_json="
					+ FurkUserData.User.jsonPreferences;
			String dl_uri_scheme = "dl_uri_scheme="
					+ URI_Enums.URI_Scheme.getValue(FurkUserData.User.dlScheme);
			String dl_uri_host = "dl_uri_host="
					+ URI_Enums.URI_Host.getValue(FurkUserData.User.dlHost);
			String dl_uri_port = "dl_uri_port="
					+ URI_Enums.URI_Port.getValue(FurkUserData.User.dlPort);
			String dl_uri_key = "dl_uri_key=" + FurkUserData.User.uriKey;

			String saveString = APIBridge.API_BASE + "/account/save_prefs?"
					+ APIBridge.key() + "&pretty=1";
			saveString += "&" + prefs_flags;
			saveString += "&" + prefs_json;
			saveString += "&" + dl_uri_scheme;
			saveString += "&" + dl_uri_host;
			saveString += "&" + dl_uri_port;
			saveString += "&" + dl_uri_key;

			APIBridge.jsonPost(saveString, false, false);
		}

		public static boolean isLoaded() {
			return isLoaded;
		}

	}

	public enum SearchMode {
		FURKSEARCH, METASEARCH
	}

	public static final SearchMode FURKSEARCH = SearchMode.FURKSEARCH,
			METASEARCH = SearchMode.METASEARCH;

	public static ArrayList<APIObject> search(String text, SearchMode mode) {
		String jsonResult = null;
		if (mode == FURKSEARCH)
			jsonResult = APIBridge.searchFurk(text, true);
		else if (mode == METASEARCH)
			jsonResult = APIBridge.searchWeb(text, true);
		else {
		}
		if (jsonResult == null)
			return null;

		JSONObject jo = new JSONObject(jsonResult);
		if (jo.getString("status").equals("error"))
			return null;

		JSONArray files = jo.getJSONArray("files");

		ArrayList<APIObject> ffarray = API.File.jsonFiles(files);
		return ffarray;
	}

}
