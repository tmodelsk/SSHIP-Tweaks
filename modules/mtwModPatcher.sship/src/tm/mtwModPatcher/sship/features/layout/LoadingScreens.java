package tm.mtwModPatcher.sship.features.layout;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;

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

	public static UUID Id = UUID.fromString("f6556ff8-5ac6-48e1-bece-974a639d4001");

	public LoadingScreens() {
		super("A few loading screens for replace");

		addOverrideTask(new OverrideCopyTask("LoadingScreens"));
	}
}
