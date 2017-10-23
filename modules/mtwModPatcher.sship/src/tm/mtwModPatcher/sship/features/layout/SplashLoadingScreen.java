package tm.mtwModPatcher.sship.features.layout;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;

import java.util.UUID;

/**
 * Created by Tomek on 2016-07-18.
 */
public class SplashLoadingScreen extends Feature {

	@Override
	public UUID getId() {
		return Id;
	}

	@Override
	public void executeUpdates() throws Exception {	}

	public static UUID Id = UUID.fromString("53b90a0e-e919-4bfd-8c7e-a3a85766689c");

	public SplashLoadingScreen() {
		super("New splash loading screen");

		addCategory("Various");

		setDescriptionShort("New alternative splash loading screen");
		setDescriptionUrl("http://tmsship.wikidot.com/splash-loading-screen");

		addOverrideTask(new OverrideCopyTask("MenuSplash"));
	}
}
