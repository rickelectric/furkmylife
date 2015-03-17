package rickelectric.furkmanager.network.api_new;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.AccountInfo;

public class UserAccountAPI implements APIModule {

	private String apiKey;
	private AccountInfo account;

	public UserAccountAPI(String apiKey) {
		this.apiKey = apiKey;
	}

	public synchronized AccountInfo info() {
		if(account!=null) return account; 
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
			String url = "/account/info";
			String json = APIConnector.getInstance().jsonPost(url, parts);
			JSONObject obj = new JSONObject(json);
			if (obj.getString("status").equals("error"))
				return null;
			JSONObject user = obj.getJSONObject("user");
			JSONObject premium = obj.getJSONObject("premium");
			JSONArray bw = obj.getJSONArray("bw_stats");
			JSONArray net = obj.getJSONArray("net_stats");
			account=new AccountInfo();
			account.user().load(user);
			account.premium().load(premium);
			account.bw_stats().load(bw,net);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

}
