package tm.mtwModPatcher.sship.features.ai;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.data._root.DescrSMFactions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * http://www.twcenter.net/forums/showthread.php?594662-%2805-04-2016%29-My-new-and-improved-AI-errr-3-0
 */
public class BeeMugOldCampaignAI extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		descrSMFactions = getFileRegisterForUpdated(DescrSMFactions.class);

		setFactionsAttributes();
	}

	protected void setFactionsAttributes() throws PatcherLibBaseEx {

		// ### Set all ai_label to default (exept papal states)
		//LinesProcessor factionLines = _DescrStrat.Factions.getContent().getLines();

//		factionLines.updateAllRegexLines("^ai_label\\s+catholic","ai_label\t\tdefault");
//		factionLines.updateAllRegexLines("^ai_label\\s+islam","ai_label\t\tdefault");
//		factionLines.updateAllRegexLines("^ai_label\\s+mongols","ai_label\t\tdefault");
//		factionLines.updateAllRegexLines("^ai_label\\s+orthodox","ai_label\t\tdefault");
//		factionLines.updateAllRegexLines("^ai_label\\s+slave_faction","ai_label\t\tdefault");

		// ## set naval invasions true for everybody except Egypt and ???
		descrSMFactions.updateFactionAtttribute("egypt", "prefers_naval_invasions", "no");

		descrSMFactions.updateFactionAtttribute("poland", "prefers_naval_invasions", "yes");
		descrSMFactions.updateFactionAtttribute("hungary", "prefers_naval_invasions", "yes");
		descrSMFactions.updateFactionAtttribute("teutonic_order", "prefers_naval_invasions", "yes");
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(SkynetCampaignAi.Id);
		conflicts.add(QuieterAi.Id);

		return conflicts;
	}

	protected DescrSMFactions descrSMFactions;

	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.fromString("21a8b2f7-0306-4e83-9a78-27063ae35c35");

	public BeeMugOldCampaignAI() {
		super("BeeMugCarl campaign AI");

		addCategory("Campaign");

		setDescriptionShort("Very good alternative campaign AI created by BeeMugCarl");
		setDescriptionUrl("http://tmsship.wikidot.com/beemugcarl-campaign-ai");

		addOverrideTask(new OverrideCopyTask("BeeMugCarlAICampaign"));
	}
}
