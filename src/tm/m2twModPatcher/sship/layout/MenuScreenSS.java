package tm.m2twModPatcher.sship.layout;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;

import java.util.UUID;

/**
 * Created by Tomek on 2016-07-18.
 */
public class MenuScreenSS extends Feature {

	@Override
	public UUID getId() {
		return Id;
	}

	@Override
	public void executeUpdates() throws Exception {	}

	public static UUID Id = UUID.randomUUID();

	public MenuScreenSS() {
		super("Stainless Steel menu picture");

		addCategory("Various");

		setDescriptionShort("Restores beautiful Stainless Steel menu picture");
		setDescriptionUrl("http://tmsship.wikidot.com/stainless-steel-menu-picture");

		addOverrideTask(new OverrideCopyTask("MenuScreenSS"));
	}
}
