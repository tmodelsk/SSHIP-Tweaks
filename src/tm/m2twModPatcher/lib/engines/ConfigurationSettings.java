package tm.m2twModPatcher.lib.engines;

import lombok.Setter;
import tm.common.EnvitonmentTm;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.net.UnknownHostException;

/**  */
public class ConfigurationSettings {

	public static String OverrideRootPath() {
		if(isDevMachine()) return devOverrideRootPath;

		return relOverrideRootPath;
	}
	public static String BackupRootPath() {
		if(isDevMachine()) return devBackupRootPath;

		return relBackupRootPath;
	}
	public static String DestinationRootPath() {
		if(isDevMachine()) return devDestinationRootPath;

		return relDestinationRootPath;
	}


	@Setter
	private static String devOverrideRootPath = "c:\\Tomek\\Projects\\SSHIP-Tweaks\\SSHIP-Tweaks-Res\\overrides";
	private static String devBackupRootPath = "c:\\Tomek\\Projects\\SSHIP-Tweaks\\SSHIP-Tweaks-Res\\Backup";
	private static String devDestinationRootPath = "c:\\Gry\\Steam\\steamapps\\common\\Medieval II Total War\\mods\\SSHIP-TM";

	private static String relOverrideRootPath = ".\\Overrides";
	private static String relBackupRootPath = ".\\Backup";
	private static String relDestinationRootPath  = ".\\..";

	public static String VariousDataPath() {
		return OverrideRootPath() + "\\VariousData";
	}

	public static boolean isDevMachine() {
		String hostName="???";


		try {
			hostName = EnvitonmentTm.getHostName();
		} catch (UnknownHostException e) {
			throw new PatcherLibBaseEx("UnknownHostException: "+e.getMessage(), e);
		}

		//return false;
		return hostName.toUpperCase().equals("TOMEK-LAP");
	}

}
