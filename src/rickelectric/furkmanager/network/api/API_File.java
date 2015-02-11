package rickelectric.furkmanager.network.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.IndexPair;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.network.FurkBridge;

/**
 * Access Furk Files
 * 
 * @author Rick Lewis (Ionicle)
 * 
 */
public class API_File extends API {

	public enum FileSection {
		DELETED, FINISHED
	}

	public static final FileSection DELETED = FileSection.DELETED,
			FINISHED = FileSection.FINISHED;

	private static ArrayList<IndexPair> index = new ArrayList<IndexPair>();
	/**
	 * Maps FurkID --> FurkFile (In MyFiles::Finished)
	 */
	private static HashMap<String, FurkFile> finished = new HashMap<String, FurkFile>();

	private static ArrayList<IndexPair> dIndex = new ArrayList<IndexPair>();
	/**
	 * Maps FurkID --> FurkFile (In MyFiles::Deleted)
	 */
	private static HashMap<String, FurkFile> deleted = new HashMap<String, FurkFile>();

	public static class FileObservable extends Observable {
		public void stateChanged(FileSection type) {
			System.out.println("API_File Observer Update: " + type);
			setChanged();
			notifyObservers(type);
		}

		public void loadingState(FileSection section) {
			setChanged();
			notifyObservers(-1);
		}
	}

	private static FileObservable fo = new FileObservable();

	public static void addObserver(Observer o) {
		fo.addObserver(o);
	}

	public static void deleteObserver(Observer o) {
		fo.deleteObserver(o);
	}

	public static void update(final FileSection section){
		update(section, true);
	}
	
	/**
	 * Updates the FurkFiles in the given Section to match new info on the
	 * server. Adds new Files from server if they don't already exist. Removes
	 * Files not present in section on server.
	 * 
	 * @param section
	 */
	public static void update(final FileSection section,boolean async) {
		Thread update = new Thread(new Runnable() {
			public void run() {
				boolean update = false;
				if (section == FINISHED) {
					ArrayList<FurkFile> fin = get(FINISHED, -1, -1);
					String[] s = finished.keySet().toArray(new String[] {});
					ArrayList<String> keys = new ArrayList<String>();
					for (String z : s)
						keys.add(z);
					for (FurkFile f : fin) {
						update = addToSection(FINISHED, f);
						keys.remove(f.getID());
					}
					if (keys.size() > 0) {
						update = true;
						for (String z : keys) {
							finished.remove(z);
						}
						keys.removeAll(keys);
					}

				}
				if (section == DELETED) {
					ArrayList<FurkFile> del = get(DELETED, -1, -1);
					String[] s = deleted.keySet().toArray(new String[] {});
					ArrayList<String> keys = new ArrayList<String>();
					for (String z : s)
						keys.add(z);
					for (FurkFile f : del) {
						update = addToSection(DELETED, f);
						keys.remove(f.getID());
					}
					if (keys.size() > 0) {
						update = true;
						for (String z : keys) {
							deleted.remove(z);
						}
						keys.removeAll(keys);
					}
				}
				if (update) {
					fo.stateChanged(section);
				}
			}
		});
		update.setDaemon(true);
		if(async) update.start();
		else update.run();
	}

	public static IndexPair[] getFileIDs(FileSection section) {
		return (section == FINISHED ? index : dIndex)
				.toArray(new IndexPair[] {});
	}

	public static FurkFile getFile(FileSection section, String fileID) {
		return section == FINISHED ? finished.get(fileID) : deleted.get(fileID);
	}

	private static boolean addToSection(FileSection fs, FurkFile f) {
		boolean added = false;
		String fid = f.getID();
		FurkFile curr = (fs == FINISHED ? finished : deleted).get(fid);
		if (curr == null) {
			added = true;
			(fs == FINISHED ? finished : deleted).put(fid, f);
			(fs == FINISHED ? index : dIndex).add(new IndexPair(fid, f
					.getCtime()));
			Collections.sort(fs == FINISHED ? index : dIndex);
		} else {
			curr.overwrite(f);
		}
		return added;
	}

	private static FurkFile removeFromSection(FileSection section, String fid) {
		FurkFile r = null;
		if (section == FINISHED) {
			r = finished.remove(fid);
			if (r != null)
				index.remove(fid);
		} else {
			r = deleted.remove(fid);
			if (r != null)
				dIndex.remove(fid);
		}
		return r;
	}

	/**
	 * Parse <b>JSONArray</b> of Furk API Objects to a list of <b>APIObject</b>s
	 * 
	 * @param files
	 *            JSON Array Of Furk API Objects
	 * @return ArrayList of APIObjects matching contents of <b>files</b>.
	 */
	public static ArrayList<APIObject> jsonFiles(JSONArray files) {
		ArrayList<APIObject> fl = new ArrayList<APIObject>();

		for (int i = 0; i < files.length(); i++) {
			JSONObject x = (JSONObject) files.get(i);
			String tid = x.getString("name");
			if (!tid.equals(JSONObject.NULL)) {
				try {
					Object fid = x.get("id");
					if (fid == null || fid == JSONObject.NULL || fid.equals(""))
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
								info_hash = x.getString(t);
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

	private static ArrayList<FurkFile> get(FileSection type, int limit,
			int offset) {
		int[] limoffs = null;
		if (limit > 0 && offset >= 0) {
			limoffs = new int[2];
			limoffs[0] = limit;
			limoffs[1] = offset;
		}
		String json = null;
		if (type == FINISHED)
			json = FurkBridge.fileGet(FurkBridge.GET_ALL, null, limoffs, null,
					true);
		else if (type == DELETED)
			json = FurkBridge.fileGet(FurkBridge.GET_TRASH, null, limoffs,
					null, true);
		else
			throw new IllegalArgumentException(
					"Invalid GET Type Passed as argument 1");
		if (json == null)
			return null;
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return null;
		try {
			JSONArray m = re.getJSONArray("messages");
			setMessages(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray filesArr = re.getJSONArray("files");
		ArrayList<APIObject> f = jsonFiles(filesArr);
		ArrayList<FurkFile> ff = new ArrayList<FurkFile>();

		for (APIObject o : f) {
			ff.add((FurkFile) o);
		}
		return ff;
	}

	public static APIObject get(String fileID) {
		if (fileID == null || fileID.equals(""))
			return null;
		String json = FurkBridge.fileGet(FurkBridge.GET_ID, fileID, null, null,
				true);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error")
				|| re.getJSONArray("files").length() == 0)
			return null;
		return jsonFiles(re.getJSONArray("files")).get(0);
	}

	public static APIObject info(String hash) {
		String json = FurkBridge.fileInfo(hash);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error")
				|| re.getJSONArray("files").length() == 0)
			return null;
		return jsonFiles(re.getJSONArray("files")).get(0);
	}

	public static boolean link(String[] fileIDs) {
		String json = FurkBridge.fileLinkstate(FurkBridge.LINK_LINK, fileIDs);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return false;
		for (String id : fileIDs)
			addToSection(FINISHED, (FurkFile) info(id));
		fo.stateChanged(FINISHED);
		return true;
	}

	public static boolean unlink(String[] fileIDs) {
		String json = FurkBridge.fileLinkstate(FurkBridge.LINK_UNLINK, fileIDs);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return false;
		for (String fid : fileIDs) {
			FurkFile fr = removeFromSection(FINISHED, fid);
			addToSection(DELETED, fr);
		}
		fo.stateChanged(FINISHED);
		fo.stateChanged(DELETED);
		return true;
	}

	public static boolean clear(String[] fileIDs) {
		String json = FurkBridge.fileLinkstate(FurkBridge.LINK_CLEAR, fileIDs);
		JSONObject re = new JSONObject(json);
		if (re.get("status").equals("error"))
			return false;
		for (String fid : fileIDs)
			removeFromSection(DELETED, fid);
		fo.stateChanged(DELETED);
		return true;
	}

	public static ArrayList<APIObject> find(String s) {
		String jsonResult = FurkBridge.searchFiles(s, true);
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
		for (String key : finished.keySet()) {
			FurkFile ff = getFile(FINISHED, key);
			String[] labels = ff.getIdLabels();
			if (labels == null || labels.length == 0) {
				if (id.equals("0")) {
					files.add(ff);
				}
			} else {
				for (String s : labels) {
					if (id.equals(s)) {
						files.add(ff);
						break;
					}
				}
			}
		}
		return files;
	}

	public static void flush() {
		try {
			finished.clear();
			deleted.clear();
			fo.deleteObservers();
		} catch (Exception e) {
		}
	}

	public static int size(FileSection section) {
		return section == FINISHED ? finished.size() : deleted.size();
	}

	public static FurkFile getByNumber(FileSection section, int inc) {
		return section(section).get(index.get(inc).getKey());
	}

	private static HashMap<String, FurkFile> section(FileSection section) {
		return section == FINISHED ? finished : deleted;
	}

}
