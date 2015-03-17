package rickelectric.furkmanager.network.api_new;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.exception.APIException;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkDate;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.models.enums.Column;
import rickelectric.furkmanager.models.enums.FileSource;
import rickelectric.furkmanager.models.enums.FileType;
import rickelectric.furkmanager.models.enums.SortType;
import rickelectric.furkmanager.models.enums.UnlinkParam;

public class FileAPI implements APIModule {

	private String apiKey;
	private long lastUpdatedFin, lastUpdatedDel;
	private ArrayList<FurkFile> filesFinished, filesDeleted;

	public FileAPI(String apiKey) throws APIException {
		if (apiKey == null)
			throw new APIException("API Key must not be null");
		if (apiKey.length() < 40)
			throw new APIException("API Key must be >=40 characters in length");
		this.apiKey = apiKey;
		lastUpdatedFin = lastUpdatedDel = 0;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	public APIObject info(String infoHash) {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			parts.addPart("info_hash", new StringBody(infoHash));

			String url = "/file/info";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			if (json == null)
				return null;
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("error"))
				return null;
			JSONArray arr = obj.getJSONArray("files");
			if (arr.length() == 0)
				return null;
			return APIParser.parseFile(arr.getJSONObject(0));
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public boolean link(String[] id) {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			for (String idp : id) {
				parts.addPart("id", new StringBody(idp));
			}

			String url = "/file/link";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("ok"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean unlinkFiles(String[] ids) {
		boolean u = unlink(ids, UnlinkParam.ID);
		if (u) {
			Iterator<FurkFile> iterator = filesFinished.iterator();
			while (iterator.hasNext()) {
				FurkFile f = iterator.next();
				for (String id : ids) {
					if (f.getID().equals(id)) {
						filesDeleted.add(f);
						iterator.remove();
						break;
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean unlinkFilesByFeed(String[] id_feeds) {
		return unlink(id_feeds, UnlinkParam.FEED);
	}

	public boolean unlinkFilesByLabel(String[] id_labels) {
		return unlink(id_labels, UnlinkParam.LABEL);
	}

	private boolean unlink(String[] params, UnlinkParam u_do) {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			if (u_do == UnlinkParam.ID) {
				for (String idp : params) {
					parts.addPart("id", new StringBody(idp));
				}
			} else {
				parts.addPart("do", new StringBody(u_do.toString()));
				if (u_do == UnlinkParam.FEED) {
					for (String idp : params) {
						parts.addPart("id_feeds", new StringBody(idp));
					}
				} else if (u_do == UnlinkParam.LABEL) {
					for (String idp : params) {
						parts.addPart("id_labels", new StringBody(idp));
					}
				}
			}

			String url = "/file/unlink";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("ok"))
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public FurkFile get(String id) {
		ArrayList<FurkFile> file = get(new String[] { id }, null, null, null,
				0, -1, Column.CTIME, SortType.DESCENDING, null, -1);
		if (file == null || file.size() == 0)
			return null;
		return file.get(0);
	}

	public ArrayList<FurkFile> getAllFinished() {
		if (System.currentTimeMillis() - lastUpdatedFin > 30)
			for (int i = 0; i < 3; i++) {
				if (updateFinished())
					break;
			}
		return filesFinished;
	}

	protected boolean updateFinished() {
		ArrayList<FurkFile> finished = get(null, null, null, null, -1, -1,
				Column.CTIME, SortType.DESCENDING, null, 0);
		if (finished != null) {
			filesFinished = finished;
			return true;
		}
		return false;
	}

	public ArrayList<FurkFile> find(String name_like) {
		if (name_like == null || name_like.length() == 0)
			return null;
		return get(null, null, null, null, -1, -1, Column.CTIME,
				SortType.DESCENDING, name_like, 0);
	}

	public ArrayList<FurkFile> getByType(FileType type, int limit, int offset) {
		if (type == null)
			return null;
		return get(null, null, type, null, limit, offset, Column.CTIME,
				SortType.DESCENDING, null, 0);
	}

	public ArrayList<FurkFile> getFinished(int limit, int offset) {
		if (limit < 1)
			return null;
		if (offset < 0)
			return null;
		return get(null, null, null, null, limit, offset, Column.CTIME,
				SortType.DESCENDING, null, 0);
	}

	public ArrayList<FurkFile> getAllDeleted() {
		if (System.currentTimeMillis() - lastUpdatedDel > 30)
			for (int i = 0; i < 3; i++) {
				if (updateDeleted())
					break;
			}
		return filesDeleted;
	}

	protected boolean updateDeleted() {
		ArrayList<FurkFile> deleted = get(null, null, null, null, -1, -1,
				Column.CTIME, SortType.DESCENDING, null, -1);
		if (deleted != null) {
			filesDeleted = deleted;
			return true;
		}
		return false;
	}

	public ArrayList<FurkFile> get(String[] id, FurkDate link_dt_gt,
			FileType type, FileSource src, int limit, int offset,
			Column sort_column, SortType sort_type, String name_like,
			int unlinked) {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			if (id != null) {
				for (String fid : id) {
					parts.addPart("id", new StringBody(fid));
				}
			} else {
				if (link_dt_gt != null) {
					parts.addPart("link_dt_gt",
							new StringBody(link_dt_gt.toMySQLString()));
				}
				if (type != null) {
					parts.addPart("type", new StringBody(type.toString()));
				}
				if (src != null) {
					parts.addPart("src", new StringBody(src.getValue()));
				}
				if (limit > 0) {
					parts.addPart("limit", new StringBody("" + limit));
				}
				if (offset >= 0) {
					parts.addPart("offset", new StringBody("" + offset));
				}
				if (sort_column != null) {
					parts.addPart("sort_column",
							new StringBody(sort_column.getValue()));
				}
				if (sort_type != null) {
					parts.addPart("sort_type",
							new StringBody(sort_type.getValue()));
				}
				if (name_like != null) {
					parts.addPart("name_like", new StringBody(name_like));
				}
				if (unlinked == 0 || unlinked == 1)
					parts.addPart("unlinked", new StringBody("" + unlinked));
			}
			String url = "/file/get";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			if (json == null)
				return null;
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("error"))
				return null;
			JSONArray arr = obj.getJSONArray("files");
			ArrayList<APIObject> files = APIParser.parseFileList(arr);
			ArrayList<FurkFile> ff = new ArrayList<FurkFile>();
			while (files.size() > 0) {
				APIObject o = files.remove(0);
				if (o instanceof FurkFile) {
					ff.add((FurkFile) o);
				}
			}
			return ff;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<FurkTFile> getTFiles(String fileId) {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			parts.addPart("t_files", new StringBody("1"));
			parts.addPart("id", new StringBody(fileId));

			String url = "/file/get";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject o = new JSONObject(json);
			if (o.getString("status").equals("error"))
				return null;
			JSONArray tf = o.getJSONArray("files").getJSONObject(0)
					.getJSONArray("t_files");
			return APIParser.parseTFileList(tf);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void stop() {

	}

	@Override
	public void flush() {

	}

}
