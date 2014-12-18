package rickelectric.furkmanager.installer;

import java.lang.reflect.InvocationTargetException;

import rickelectric.WinRegistry;

public class FurkInstaller {

	public static void main(String[] args) throws Exception {
		installChromeExt(args[1]);
		
	}
	
	private static void installChromeExt(String installDir) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Google\\Chrome\\Extensions\\eecjgaddalinecocgjigcjbihajfjfah");
		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,"SOFTWARE\\Wow6432Node\\Google\\Chrome\\Extensions\\eecjgaddalinecocgjigcjbihajfjfah");
		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\Classes\\.torrent");
	}

}
