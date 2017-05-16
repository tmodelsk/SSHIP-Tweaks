package tm.m2twModPatcher.sship.layout;

import tm.mtwModPatcher.lib.common.core.features.Feature;

import java.util.UUID;

/**
 * Created by tomek on 21.04.2017.
 */
public class NoRegionBordersOnMiniMap extends Feature {

	@Override
	public void executeUpdates() throws Exception {

	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public NoRegionBordersOnMiniMap() {

		setName("No region borders on mini map [jurcek1987]");

		addCategory("Various");

		setDescriptionShort("No region borders on mini map [jurcek1987]");
		setDescriptionUrl("http://www.twcenter.net/forums/showthread.php?707318-SUBMODS-Music-Overhaul-New-Byzantine-settlement-textures-and-no-region-borders-on-mini-map");
	}
}
