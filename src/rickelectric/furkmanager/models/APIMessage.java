package rickelectric.furkmanager.models;

public class APIMessage {

	private String text;// : "Access denied.",
	private String type;// " : "error",
	private String key;// " : "access_denied",
	private int code;// code" : "403"
	
	public APIMessage(String type, String key) {
		this.type = type;
		this.key = key;
		text = "";
		code = 200;
	}

	@Override
	public String toString() {
		return "APIMessage [" + (text != null ? "text=" + text + ", " : "")
				+ (type != null ? "type=" + type + ", " : "")
				+ (key != null ? "key=" + key + ", " : "") + "code=" + code
				+ "]";
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
