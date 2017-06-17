package tm.mtwModPatcher.sship.features.ai;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;

import java.util.UUID;

/** Faction Standings from Quieter AI */
public class QuieterAiFactionStanding extends Feature {
	@Override
	public void executeUpdates() throws Exception { }

	public QuieterAiFactionStanding() {
		super("Quieter AI Faction Standing [kaiser29]");

		addCategory("Campaign");
		addCategory("AI");

		setDescriptionShort("Faction Standing (& reputation?) from Quieter AI by [kaiser29]");
		setDescriptionUrl("http://tmsship.wikidot.com/quieter-ai-faction-standing");

		addOverrideTask(new OverrideCopyTask("QuieterAIFactionStanding"));
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
