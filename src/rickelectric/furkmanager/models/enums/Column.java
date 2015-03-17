package rickelectric.furkmanager.models.enums;

public enum Column {

	CTIME, SIZE, NAME;

	public String getValue() {
		switch (this) {
		case CTIME:
			return "ctime";
		case NAME:
			return "name";
		case SIZE:
			return "size";
		default:
			return null;
		}
	}
}
