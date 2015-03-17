package rickelectric.furkmanager.models.enums;

public enum FileType {
	DEFAULT, ALL, AUDIO, VIDEO;

	public String toString() {
		switch (this) {
		case AUDIO:
			return "audio";
		case VIDEO:
			return "video";
		case ALL:
			return "";
		default:
			return "default";
		}
	}

	public static FileType fromString(String s) {
		if (s != null) {
			if (s.equals("audio"))
				return AUDIO;
			if (s.equals("video"))
				return VIDEO;
			if (s.equals(""))
				return ALL;
		}
		return DEFAULT;
	}

}
