package tm.m2twModPatcher.sship.layout;

import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.OverrideCopyTask;

import java.util.UUID;

/**
 * Created by Tomek on 2016-07-07.
 */
public class LoadingScreens extends Feature {

	@Override
	public UUID getId() {
		return Id;
	}

	@Override
	public void executeUpdates() throws Exception {	}

	public static UUID Id = UUID.randomUUID();

	public LoadingScreens() {
		super("A few loading screens for replace");

		addOverrideTask(new OverrideCopyTask("LoadingScreens"));
	}
}
