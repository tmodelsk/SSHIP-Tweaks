package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.simpleframework.xml.core.Persister;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by tomek on 17.10.2017.
 */
public class SettingsRepository {

	public void saveSettings(UserSettings userSettings) throws Exception {

		val filePath = rootPath + "\\" + userSettings.getName() + ".xml";

		val serializer = new Persister();
		val file = new File(filePath);
		serializer.write(userSettings, file);
	}

	public UserSettings loadSettings(String profileName) throws Exception {
		val filePath = rootPath + "\\" + profileName + ".xml";

		val serializer = new Persister();
		val file = new File(filePath);

		try {
			val us = serializer.read(UserSettings.class, file);
			return us;
		} catch (FileNotFoundException fileEx) {
			return null;
		}
	}


	@Getter @Setter
	private String rootPath;
}
