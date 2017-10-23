package tm.mtwModPatcher.sship.features.global;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;

import java.util.UUID;

/**
 * Authentic Faction Names & Character Titles [Eldgrimr]
 */
public class AuthenticFactionNamesCharacterTitles extends Feature {
	@Override
	public void executeUpdates() throws Exception { }


	public AuthenticFactionNamesCharacterTitles() {
		super("Authentic Faction Names & Character Titles [Eldgrimr]");

		addCategory("Campaign");

		setDescriptionShort("[Eldgrimr] Changes the faction names and character titles to make them a bit more historically accurate, v. 2017-06-01");
		setDescriptionUrl("http://www.twcenter.net/forums/showthread.php?703183-Authentic-Faction-Names-amp-Character-Titles&p=15333140");

		addOverrideTask(new OverrideCopyTask("AuthenticFactionNamesCharacterTitles"));
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("");
}
