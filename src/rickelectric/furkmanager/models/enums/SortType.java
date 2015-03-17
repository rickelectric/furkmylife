package rickelectric.furkmanager.models.enums;

public enum SortType {
	ASCENDING,DESCENDING;
	
	public String getValue() {
		switch (this) {
		case ASCENDING:
			return "asc";
		case DESCENDING:
			return "desc";
		default:
			return null;
		}
	}

}
