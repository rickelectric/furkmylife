package rickelectric.furkmanager.models.enums;

public enum DLStatus {
	ACTIVE,FAILED,ALL;
	
	public String toString(){
		switch(this){
		case ACTIVE:
			return "active";
		case FAILED:
			return "failed";
		default:
			return "";
		}
	}
	
	public static DLStatus fromString(String s){
		if(s!=null){
			if(s.equals("active")) return ACTIVE;
			if(s.equals("failed")) return FAILED;
		}
		return ALL;
	}
}
