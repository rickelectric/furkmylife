package rickelectric.furkmanager.network.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.network.FurkBridge;

/**
 * Access Furk Files
 * 
 * @author Rick Lewis (Ionicle)
 * 
 */
public class API_File extends API {
	
	public enum GET_TYPE {
		GET_DELETED, GET_FINISHED
	};

	public static final GET_TYPE GET_DELETED = GET_TYPE.GET_DELETED,
			GET_FINISHED = GET_TYPE.GET_FINISHED;

	private static ArrayList<FurkFile> fileList;

	public static ArrayList<APIObject> jsonFiles(JSONArray files) {
		
		ArrayList<APIObject> fl = new ArrayList<APIObject>();

		for (int i = 0; i < files.length(); i++) {
			JSONObject x = (JSONObject) files.get(i);
			String tid = x.getString("name");
			if (!tid.equals(JSONObject.NULL)) {

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
								id = x.getString(t);
							if (t.equals("name"))
								name = x.getString(t);
							if (t.equals("info_hash"))
								info_hash =x.getString(t);
							if (t.equals("type"))
								type = x.getString(t);
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

	public static ArrayList<FurkFile> getAllCached() {
		return fileList;
	}

	public static ArrayList<FurkFile> getAllFinished() {
		fileList = get(GET_FINISHED, -1, -1);
		return fileList;
	}

	public static ArrayList<FurkFile> getAllDeleted() {
		return get(GET_DELETED, -1, -1);
	}

	public static ArrayList<FurkFile> get(GET_TYPE type, int limit,
			int offset) {
		int[] limoffs = null;
		if (limit > 0 && offset >= 0) {
			limoffs = new int[2];
			limoffs[0] = limit;
			limoffs[1] = offset;
		}
		String json = null;
		if (type == GET_FINISHED)
			json = APIBridge.fileGet(FurkBridge.GET_ALL, null, limoffs,
					null, true);
		else if (type == GET_DELETED)
			json = APIBridge.fileGet(FurkBridge.GET_TRASH, null, limoffs,
					null, true);
		else
			throw new IllegalArgumentException(
					"Invalid GET Type Passed as argument 1");
		if (json == null)
			return null;
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return null;
		try{
			JSONArray m = re.getJSONArray("messages");
			setMessages(m);
		}catch(Exception e){
			e.printStackTrace();
		}
		JSONArray filesArr = re.getJSONArray("files");
		ArrayList<APIObject> f=jsonFiles(filesArr);
		ArrayList<FurkFile> ff=new ArrayList<FurkFile>();
		
		for(APIObject o:f){
			ff.add((FurkFile)o);
		}
		return ff;
	}

	public static APIObject get(String fileID) {
		if (fileID == null || fileID.equals(""))
			return null;
		String json = APIBridge.fileGet(FurkBridge.GET_ID, fileID, null,
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
		String json = APIBridge.fileLinkstate(FurkBridge.LINK_LINK, fileIDs);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return false;
		for (String id : fileIDs)
			fileList.add((FurkFile)info(id));
		return true;
	}

	public static boolean unlink(String[] fileIDs) {
		String json = APIBridge.fileLinkstate(FurkBridge.LINK_UNLINK,
				fileIDs);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return false;
		for (String fid : fileIDs) {
			for(int i=0;i<fileList.size();i++){
				FurkFile f = fileList.get(i);
				String id = f.getID();
				if (id.equals(fid)){
					fileList.remove(i);
					break;
				}
			}
		}
		return true;
	}

	public static boolean clear(String[] fileIDs) {
		String json = APIBridge
				.fileLinkstate(FurkBridge.LINK_CLEAR, fileIDs);
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

	public static void flush() {
		try{fileList.removeAll(fileList);}catch(Exception e){}
		fileList=null;
	}

}
