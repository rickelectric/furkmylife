package rickelectric.furkmanager.models.enums;

public enum UnlinkParam {
	ID,FEED,LABEL,ALL;
	
	public String toString(){
		switch(this){
		case ALL:
			return "unlink_all";
		case FEED:
			return "unlink_by_feed";
		case ID:
			return "";
		case LABEL:
			return "unlink_by_label";
		default:
			return "";
		}
	}

}
