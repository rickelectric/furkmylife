package rickelectric.furkmanager.network.api_new;

import java.util.ArrayList;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.UtilBox;
import rickelectric.furkmanager.models.APIObject;

public class PluginAPI implements APIModule {

	private String apiKey;

	public PluginAPI(String apiKey) {
		this.apiKey = apiKey;
	}

	// TODO Add other params (prcontrols,permissions) from /search page
	public ArrayList<APIObject> metasearch(String q, int offset, int limit) {
		if (q == null || UtilBox.getInstance().alphanum(q).length() == 0)
			throw new IllegalArgumentException(
					"Search String (q) Must Not Be Empty");
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			parts.addPart("q", new StringBody(q));
			if (limit > 0) {
				parts.addPart("limit", new StringBody("" + limit));
			}
			if (offset >= 0) {
				parts.addPart("offset", new StringBody("" + offset));
			}
			
			String url = "/plugins/metasearch";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if(obj.getString("status").equals("error")) 
				return null;
			JSONArray results = obj.getJSONArray("files");
			return APIParser.parseFileList(results);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
