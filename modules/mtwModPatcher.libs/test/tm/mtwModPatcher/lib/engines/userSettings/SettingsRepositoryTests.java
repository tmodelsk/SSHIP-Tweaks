package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.val;
import org.junit.Test;
import tm.common.Ctm;

import java.io.File;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 22.10.2017.
 */
public class SettingsRepositoryTests {

	@Test
	public void saveSettingsBasicTest() throws Exception {
		val settingsRepository = new SettingsRepository();
		settingsRepository.setRootPath(Ctm.getWorkingDirectory());
		val profileName = "myProfile";

		val userSett = new UserSettings();
		userSett.setName(profileName);
		userSett.setVersion("1.3.1");

		val fSett = new FeatureSettings();
		fSett.setId(UUID.randomUUID());
		fSett.setName("Some Feature");
		fSett.setVersion("1.2.1");

		FeatureParam fp;

		fp = new FeatureParam();
		fp.setSymbol("ParamStr");
		fp.setValue("someString");
		fSett.add(fp);

		fp = new FeatureParam();
		fp.setSymbol("ParamInteger");
		fp.setValue(5);
		fSett.add(fp);

		userSett.add(fSett);

		settingsRepository.saveSettings(userSett);

		// Check
		val file = new File(Ctm.getWorkingDirectory() + "\\" + profileName + ".xml");

		assertThat(file.exists()).isTrue();
	}

	@Test
	public void saveSettingsLoadSettingsBasicTest() throws Exception {
		val settingsRepository = new SettingsRepository();
		settingsRepository.setRootPath(Ctm.getWorkingDirectory());
		val profileName = "myProfile";

		val userSett = new UserSettings();
		userSett.setName(profileName);
		userSett.setVersion("1.3.1");

		val fSett = new FeatureSettings();
		fSett.setId(UUID.randomUUID());
		fSett.setName("Some Feature");
		fSett.setVersion("1.2.1");

		FeatureParam fp;

		fp = new FeatureParam();
		fp.setSymbol("ParamStr");
		fp.setValue("someString");
		fSett.add(fp);

		fp = new FeatureParam();
		fp.setSymbol("ParamInteger");
		fp.setValue(5);
		fSett.add(fp);

		userSett.add(fSett);

		settingsRepository.saveSettings(userSett);

		// Check
		val us = settingsRepository.loadSettings(profileName);

		assertThat(us).isNotNull();
		assertThat(us.getName()).isEqualTo(userSett.getName());
	}
}
