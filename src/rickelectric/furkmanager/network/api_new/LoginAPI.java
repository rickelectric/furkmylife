package rickelectric.furkmanager.network.api_new;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import rickelectric.UtilBox;
import rickelectric.furkmanager.exception.LoginException;
import rickelectric.furkmanager.models.LoginModel;

public class LoginAPI {

	private LoginModel currentLoginModel;

	public LoginAPI() {
	}

	public boolean login(LoginModel loginModel) throws LoginException{
		boolean loggedIn = false;
		switch (loginModel.mode()) {
		case APIKEY:
			loggedIn = apiLogin(loginModel.apiKey());
			System.out.println("LoginAPI.login(): APILogin Call: "+loggedIn);
			break;
		case BLANK:
			break;
		case CREDENTIALS:
			String key = userLogin(loginModel.username(), loginModel.password());
			if (key != null) {
				loginModel.setApiKey(key);
				loggedIn = true;
			}
			System.out.println("LoginAPI.login(): UserLogin Call: "+loggedIn);
		}
		if (loggedIn)
			currentLoginModel = loginModel;
		return loggedIn;
	}

	private String userLogin(String username, char[] password) throws LoginException {
		String url = "/login/login";
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("login", new StringBody(username));
			parts.addPart("pwd", new StringBody(UtilBox.getInstance()
					.charToString(password)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String json = APIConnector.getInstance().jsonPost(url, parts);
		System.out.println("JSON:: \n"+json);
		if(json==null) return null;
		JSONObject j = new JSONObject(json);
		if (j.getString("status").equals("error")) {
			throw new LoginException(j.getString("error"),
					j.getInt("tries_left"));
		}
		return j.getString("api_key");
	}

	private boolean apiLogin(String apiKey) throws LoginException{
		String url = "/ping";
		MultipartEntity parts = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			parts.addPart("api_key", new StringBody(apiKey));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String json = APIConnector.getInstance().jsonPost(url, parts);
		System.out.println("JSON:: \n"+json);
		if(json==null) return false;
		JSONObject j = new JSONObject(json);
		if(j.getString("status").equals("ok")){
			return true;
		}
		if (j.getString("status").equals("error")) {
			throw new LoginException(j.getString("error"));
		}
		return false;
	}

	public boolean isLoggedIn() {
		return currentLoginModel != null && currentLoginModel.isValid();
	}
	
	public void logout(){
		currentLoginModel=null;
		System.gc();
	}

}
