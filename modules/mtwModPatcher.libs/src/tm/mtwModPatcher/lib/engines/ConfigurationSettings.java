package tm.mtwModPatcher.lib.engines;

import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.EnvitonmentTm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.net.UnknownHostException;

/**  */
public class ConfigurationSettings {

	public static String OverrideRootPath() {
		if (isDevEnvironment()) return devOverrideRootPath;

		return relOverrideRootPath;
	}

	public static String BackupRootPath() {
		if (isDevEnvironment()) return devBackupRootPath;

		return relBackupRootPath;
	}

	public static String DestinationRootPath() {
		if (isDevEnvironment()) return devDestinationRootPath;

		return relDestinationRootPath;
	}


	@Setter
	private static String devOverrideRootPath = "c:\\Tomek\\Projects\\SSHIP-Tweaks\\SSHIP-Tweaks-src\\Overrides";
	//"c:\\Tomek\\Projects\\SSHIP-Tweaks\\SSHIP-Tweaks-Res\\overrides";
	private static String devBackupRootPath = "c:\\Tomek\\Projects\\SSHIP-Tweaks\\SSHIP-Tweaks-Res\\Backup";
	private static String devDestinationRootPath = "c:\\Gry\\Steam\\steamapps\\common\\Medieval II Total War\\mods\\SSHIP-TM";

	private static String relOverrideRootPath = ".\\Overrides";
	private static String relBackupRootPath = ".\\Backup";
	private static String relDestinationRootPath = ".\\..";

	public static String VariousDataPath() {
		return OverrideRootPath() + "\\VariousData";
	}

	public static boolean isDevEnvironment() {
		String hostName = "???", workingDir = "???";


		try {
			//hostName = EnvitonmentTm.getHostName();
			workingDir = Ctm.getWorkingDirectory();
		} catch (Exception e) {
			// ### WE ARE 'EATING' exception, on failure - do nothing, but it won't be dev environment
			//throw new PatcherLibBaseEx("Unknown Exception: "+e.getMessage(), e);
		}

		val isDev = !workingDir.contains("\\mods\\");

		return isDev;
		//return hostName.toUpperCase().equals("TOMEK-LAP");
	}

}
