package rickelectric.furkmanager.models;

import java.io.Serializable;

public class LoginModel implements Serializable {
	private static final long serialVersionUID = 10314L;

	public enum LoginMode {
		APIKEY, CREDENTIALS, BLANK
	}

	public static final LoginMode APIKEY = LoginMode.APIKEY,
			CREDENTIALS = LoginMode.CREDENTIALS, BLANK = LoginMode.BLANK;

	private String apiKey, username;
	private char[] password;

	private LoginMode mode;
	private boolean save, autoLogin, valid;

	public LoginModel(String apiKey) {
		this(APIKEY);
		setApiKey(apiKey);
	}

	public LoginModel(String username, char[] password) {
		this(CREDENTIALS);
		setLogin(username, password);
	}

	public LoginModel(LoginMode mode) {
		if (mode != APIKEY && mode != CREDENTIALS && mode != BLANK)
			return;
		this.setMode(mode);
		apiKey = null;
		username = null;
		password = null;
		valid = false;
		autoLogin = false;
		save = false;
	}

	public boolean isValid() {
		return valid;
	}

	public String apiKey() {
		return apiKey;
	}

	public boolean setApiKey(String apiKey) {
		if (apiKey == null)
			return false;
		this.apiKey = apiKey;
		valid = true;
		return true;
	}

	public String username() {
		return username;
	}

	public char[] password() {
		return password;
	}

	public boolean setLogin(String username, char[] password) {
		if (username == null || password == null)
			return false;
		if (username.length() == 0 || username.contains(" ")
				|| password.length < 4)
			return false;
		this.username = username;
		this.password = password;
		valid = true;
		return true;
	}

	public LoginMode mode() {
		return mode;
	}

	public void setMode(LoginMode mode) {
		this.mode = mode;
	}

	public boolean autoLogin() {
		return autoLogin;
	}

	public void autoLogin(boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

	public boolean save() {
		return save;
	}

	public void save(boolean save) {
		this.save = save;
	}
}
