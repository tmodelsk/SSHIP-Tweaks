package tm.m2twModPatcher.sship.layout;

import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.OverrideCopyTask;

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

	public static UUID Id = UUID.randomUUID();

	public SplashLoadingScreen() {
		super("New splash loading screen");

		addCategory("Various");

		setDescriptionShort("New alternative splash loading screen");
		setDescriptionUrl("http://tmsship.wikidot.com/splash-loading-screen");

		addOverrideTask(new OverrideCopyTask("MenuSplash"));
	}
}
