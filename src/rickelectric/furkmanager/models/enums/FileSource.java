package rickelectric.furkmanager.models.enums;

public enum FileSource {
	
	DEFAULT,UPLOAD,TORRENT,HTTP,ED2K;
	
	public String getValue(){
		switch(this){
		case ED2K:
			return "ed2k";
		case HTTP:
			return "http";
		case TORRENT:
			return "torrent";
		case UPLOAD:
			return "upload";
		default:
			return "";
		}
	}
	
}
