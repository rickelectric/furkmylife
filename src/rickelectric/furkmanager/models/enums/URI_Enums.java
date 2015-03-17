package rickelectric.furkmanager.models.enums;

public class URI_Enums {

	public interface EURI {

	}

	public enum URI_Scheme implements EURI {
		DEFAULT, HTTP, HTTPS;

		public static String getName(URI_Scheme i) {
			if (i == null)
				return null;
			if (i == DEFAULT)
				return "Default";
			if (i == HTTP)
				return "http (Standard)";
			if (i == HTTPS)
				return "https (Secure)";
			return null;
		}

		public static String getValue(URI_Scheme i) {
			if (i == null)
				return null;
			if (i == DEFAULT)
				return "";
			if (i == HTTP)
				return "http";
			if (i == HTTPS)
				return "https";
			return null;
		}

		public static URI_Scheme eval(String s) {
			if (s == null)
				return null;
			if (s.equals("") || s.equals("Default"))
				return DEFAULT;
			if (s.equals("http") || s.equals("http (Standard)"))
				return HTTP;
			if (s.equals("https") || s.equals("https (Secure)"))
				return HTTPS;
			return null;
		}

		public static String[] stringValues() {
			URI_Scheme[] vals = URI_Scheme.values();
			String[] sVal = new String[vals.length];
			for (int i = 0; i < sVal.length; i++) {
				sVal[i] = getName(vals[i]);
			}
			return sVal;
		}

	}

	public enum URI_Host implements EURI {
		DEFAULT, GCDN;

		public static String getName(URI_Host i) {
			if (i == null)
				return null;
			if (i == DEFAULT)
				return "Default";
			if (i == GCDN)
				return "gcdn.biz";
			return null;
		}

		public static String getValue(URI_Host i) {
			if (i == null)
				return null;
			if (i == DEFAULT)
				return "";
			if (i == GCDN)
				return "gcdn.biz";
			return null;
		}

		public static URI_Host eval(String s) {
			if (s == null)
				return null;
			if (s.equals("") || s.equals("Default"))
				return DEFAULT;
			if (s.equals("gcdn.biz"))
				return GCDN;
			return null;
		}

		public static String[] stringValues() {
			URI_Host[] vals = URI_Host.values();
			String[] sVal = new String[vals.length];
			for (int i = 0; i < sVal.length; i++) {
				sVal[i] = getName(vals[i]);
			}
			return sVal;
		}

	}

	public enum URI_Port implements EURI {
		DEFAULT, P_30084, P_30082;

		public static String getName(URI_Port i) {
			if (i == null)
				return null;
			if (i == DEFAULT)
				return "Default";
			if (i == P_30082)
				return "30082 (https only)";
			if (i == P_30084)
				return "30084 (http only)";
			return null;
		}

		public static String getValue(URI_Port i) {
			if (i == null)
				return null;
			if (i == DEFAULT)
				return "";
			if (i == P_30082)
				return "30082";
			if (i == P_30084)
				return "30084";
			return null;
		}

		public static URI_Port eval(String val) {
			if (val == null)
				return null;
			if (val.equals("") || val.equals("Default"))
				return DEFAULT;
			if (val.contains("30082"))
				return P_30082;
			if (val.contains("30084"))
				return P_30084;
			return null;
		}

		public static String[] stringValues() {
			URI_Port[] vals = URI_Port.values();
			String[] sVal = new String[vals.length];
			for (int i = 0; i < sVal.length; i++) {
				sVal[i] = getName(vals[i]);
			}
			return sVal;
		}
	}

	public enum Prefs_Flags {
		NONE, MODERATED_FULL;

		public static Prefs_Flags eval(String s) {
			if (s == null)
				return null;
			if (s.equals("moderated_full")
					|| s.equals("Fully Moderated Search"))
				return MODERATED_FULL;
			if (s.equals("") || s.equals("None"))
				return NONE;
			return null;
		}

		public static String getName(Prefs_Flags i) {
			if (i == null)
				return null;
			if (i == NONE)
				return "None";
			if (i == MODERATED_FULL)
				return "Fully Moderated Search";
			return null;
		}

		public static String getValue(Prefs_Flags i) {
			if (i == null)
				return null;
			if (i == NONE)
				return "";
			if (i == MODERATED_FULL)
				return "moderated_full";
			return null;
		}

		public static String[] stringValues() {
			Prefs_Flags[] vals = Prefs_Flags.values();
			String[] sVal = new String[vals.length];
			for (int i = 0; i < sVal.length; i++) {
				sVal[i] = getName(vals[i]);
			}
			return sVal;
		}

	}

	public enum Search_Mod {
		FULL, YES, NO;

		public static Search_Mod eval(String s) {

			if (s == null)
				return null;
			if (s.equals("full"))
				return FULL;
			if (s.equals("yes"))
				return YES;
			if (s.equals("no"))
				return NO;
			return null;
		}

		public static String getName(Search_Mod i) {
			if (i == null)
				return null;
			if (i == YES)
				return "Yes (On)";
			if (i == NO)
				return "No (Off)";
			if (i == FULL)
				return "Fully Moderated";
			return null;
		}

		public static String getValue(Search_Mod i) {
			if (i == null)
				return null;
			if (i == YES)
				return "yes";
			if (i == NO)
				return "no";
			if (i == FULL)
				return "full";
			return null;
		}

	}

}
