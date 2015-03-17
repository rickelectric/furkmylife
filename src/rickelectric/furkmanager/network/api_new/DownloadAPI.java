package rickelectric.furkmanager.network.api_new;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.exception.APIException;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkDownload;
import rickelectric.furkmanager.models.enums.DLAddState;
import rickelectric.furkmanager.models.enums.DLAddType;
import rickelectric.furkmanager.models.enums.DLStatus;

public class DownloadAPI implements APIModule {

	private String apiKey;

	public DownloadAPI(String apiKey) {
		this.apiKey = apiKey;
	}

	public DLAddState add(DLAddType type, Object arg)
			throws IllegalArgumentException {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			switch (type) {
			case FILE:
				if (!(arg instanceof File))
					throw new IllegalArgumentException(
							"Expected A java.io.File");
				File f = (File) arg;
				parts.addPart("file", new FileBody(f));
				break;
			case INFO_HASH:
				parts.addPart("info_hash", new StringBody(arg.toString()));
				break;
			case URL:
				parts.addPart("url", new StringBody(arg.toString()));
				break;
			default:
				break;
			}
			String url = "/dl/add";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("error")) {
				return DLAddState.FAILED;
			}
			try {
				JSONObject f = obj.getJSONObject("file");
				APIObject ff = APIParser.parseFile(f);
				DLAddState ds = DLAddState.FILE;
				ds.setArg(ff);
				return ds;
			} catch (Exception e) {
			}
			try {
				JSONObject d = obj.getJSONObject("dl");
				FurkDownload fd = APIParser.parseDownload(d);
				DLAddState ds = DLAddState.DOWNLOADING;
				ds.setArg(fd);
				return ds;
			} catch (Exception e) {
			}
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DLAddState.FAILED;
	}

	public ArrayList<FurkDownload> get(String[] id, DLStatus dl_status,
			int offset, int limit) {
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		try {
			parts.addPart("api_key", new StringBody(apiKey));
			if (id != null) {
				for (String did : id) {
					parts.addPart("id", new StringBody(did));
				}
			} else {
				if (dl_status!=null && dl_status != DLStatus.ALL) {
					parts.addPart("dl_status",
							new StringBody(dl_status.toString()));
				}
				if (limit > 0) {
					parts.addPart("limit", new StringBody("" + limit));
				}
				if (offset >= 0) {
					parts.addPart("offset", new StringBody("" + offset));
				}
			}
			String url = "/dl/get";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("error")) {
				throw new APIException("DL API Error.");
			}
			JSONArray dls = obj.getJSONArray("dls");
			return APIParser.parseDownloadList(dls);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean unlink(String[] id) {
		if(id==null || id.length==0) return false;
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			for(String did:id){
				parts.addPart("id", new StringBody(did));
			}
			String url = "/dl/unlink";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if(obj.getString("status").equals("ok")){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {

	}

	@Override
	public void flush() {

	}

}
