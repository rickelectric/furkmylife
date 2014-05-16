package rickelectric.furkmanager.models;

public class APIRequest implements Comparable<Object>{

	private String url, json;
	private long time;

	public APIRequest(String url, String json, long time) {
		this.url = url;
		this.json = json;
		this.time = time;
	}

	public String getJSON() {
		return json;
	}

	public void setJSON(String json) {
		this.json = json;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}
	
	public int compareTo(Object o) {
		if(o==null) return -1;
		if(o instanceof APIRequest){
			if(url==null) throw new RuntimeException("Null URL Identifier in APIRequest");
			return url.compareTo(((APIRequest) o).getUrl());
		}
		else if(o instanceof String){
			return url.compareTo((String)o);
		}
		throw new IllegalArgumentException("Expected Either a String or an APIRequest object, not a "+o.getClass().getName());
	}
	
	@Override
	public boolean equals(Object o){
		if(url==null) return false;
		if(o instanceof APIRequest){
			return url.equals(((APIRequest) o).getUrl());
		}
		else if(o instanceof String){
			return url.equals((String)o);
		}
		throw new IllegalArgumentException("Expected Either a String or an APIRequest object, not a "+o.getClass().getName());
	}
	
}
