package rickelectric.media;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class DefaultParams {
	
	public static final String DEF_MEDIA_OPTIONS = "network-caching=5000";

	private static MediaPlayerFactory factory = null;
//
//	public static final String VLC_PATH = 
//			//vlcPath() != null ? vlcReg: 
//				".\\libvlc\\"
//					+ ((System.getProperty("os.arch").contains("64")) ? "64"
//							: ".\\32");
//
//	public static String vlcPath() {
//		String path;
//		if (vlcReg == null) {
//			path = "SOFTWARE\\VideoLAN\\VLC";
//			try {
//				vlcReg = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
//						path, "InstallDir");
//				if (vlcReg == null) {
//					path = "SOFTWARE\\Wow6432Node\\VideoLAN\\VLC";
//					vlcReg = WinRegistry.readString(
//							WinRegistry.HKEY_LOCAL_MACHINE, path, "InstallDir");
//				}
//			} catch (Exception e) {
//				System.out.println("VLC Path Not Found In Registry");
//			}
//		}
//		return vlcReg;
//	}

	public static synchronized boolean init() {
		//Turn Off Apache Commons Logging
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		
		try{
			factory = new MediaPlayerFactory();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public static MediaPlayerFactory getMediaPlayerFactory() {
		return factory;
	}
}
