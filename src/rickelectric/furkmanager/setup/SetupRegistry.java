package rickelectric.furkmanager.setup;

import java.lang.reflect.InvocationTargetException;

import rickelectric.WinRegistry;

public class SetupRegistry {

	private static void runSetup(int s) {

	}

	private static void registerProtocol(String executable) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk");

		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\", "AppUserModelID", "");
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\", "URL Protocol", "");
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\", "FriendlyTypeName",
				"Rickelectric Furk Manager Protocol");

		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk");

		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\", "AppUserModelID", "");
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\", "URL Protocol", "");
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\", "FriendlyTypeName",
				"Rickelectric Furk Manager Protocol");

		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\DefaultIcon");
		WinRegistry
				.writeStringValue(
						WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Classes\\furk\\DefaultIcon",
						"",
						executable+",0");

		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\shell");
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\shell", "", "open");

		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\shell\\open");
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\shell\\open", "CommandId",
				"Furk.Protocol");

		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\furk\\shell\\open\\command");
		WinRegistry
				.writeStringValue(
						WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Classes\\furk\\shell\\open\\command",
						"",
						executable+" %1");
	}

	public static void installChromeExt(String installDir)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		WinRegistry
				.createKey(WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Google\\Chrome\\Extensions\\eecjgaddalinecocgjigcjbihajfjfah");
		WinRegistry
				.createKey(
						WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Wow6432Node\\Google\\Chrome\\Extensions\\eecjgaddalinecocgjigcjbihajfjfah");
		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
				"SOFTWARE\\Classes\\.torrent");
	}

	public static void checkRegistry(String executable) {
		// If Furk Registry Keys Exist, Return.
		// Else : Run First TimeSetup

		try {
			String complete = WinRegistry.readString(
					WinRegistry.HKEY_CURRENT_USER,
					"SOFTWARE\\Rickelectric\\FurkManager", "SetupComplete");
			System.out.println(complete);
			if (complete == null) {
				// Setup Progress...
				WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Rickelectric\\FurkManager");
				WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Rickelectric\\FurkManager", "SetupComplete",
						"0");

				runSetup(0);
			} else if (complete == "COMPLETE") {
				return;
			} else {
				int s = Integer.parseInt(complete);
				runSetup(s);
			}

			registerProtocol(executable);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
