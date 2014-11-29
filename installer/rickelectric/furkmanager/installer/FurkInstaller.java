package rickelectric.furkmanager.installer;

import rickelectric.furkmanager.utils.WinRegistry;

public class FurkInstaller {

	public static void main(String[] args) throws Exception {
		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Google\\Chrome\\Extensions\\eecjgaddalinecocgjigcjbihajfjfah");
		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\Classes\\.torrent");
	}

}
