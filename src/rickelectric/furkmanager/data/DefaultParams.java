package rickelectric.furkmanager.data;

import rickelectric.furkmanager.utils.WinRegistry;


public class DefaultParams {

	public static final String VLC_PATH = vlcPath() != null ? vlcPath()
			: ".\\libvlc\\"
					+ ((System.getProperty("os.arch").contains("64")) ? "64"
							: ".\\32");

	public static String vlcPath() {
		String value = null, path;

		path = "SOFTWARE\\VideoLAN\\VLC";
		try {
			value = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
					path, "InstallDir");
			System.out.println("VLC Path = " + value);
			if (value == null) {
				path = "SOFTWARE\\Wow6432Node\\VideoLAN\\VLC";
				value = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
						path, "InstallDir");

			}
		} catch (Exception e) {
			System.out.println("VLC Path Not Found In Registry");
		}
		return value;
	}
}
