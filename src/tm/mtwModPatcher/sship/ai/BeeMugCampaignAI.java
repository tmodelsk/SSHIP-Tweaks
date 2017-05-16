package tm.mtwModPatcher.sship.ai;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.fileEntities.data.DescrSMFactions;

import java.util.UUID;

/** http://www.twcenter.net/forums/showthread.php?594662-%2805-04-2016%29-My-new-and-improved-AI-errr-3-0  */
public class BeeMugCampaignAI extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		_DescrSMFactions = getFileRegisterForUpdated(DescrSMFactions.class);

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
		_DescrSMFactions.updateFactionAtttribute("egypt", "prefers_naval_invasions" , "no");

		_DescrSMFactions.updateFactionAtttribute("poland", "prefers_naval_invasions" , "yes");
		_DescrSMFactions.updateFactionAtttribute("hungary", "prefers_naval_invasions" , "yes");
		_DescrSMFactions.updateFactionAtttribute("teutonic_order", "prefers_naval_invasions" , "yes");
	}

	protected DescrSMFactions _DescrSMFactions;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public BeeMugCampaignAI() {
		super("BeeMugCarl campaign AI");

		addCategory("Campaign");

		setDescriptionShort("Very good alternative campaign AI created by BeeMugCarl");
		setDescriptionUrl("http://tmsship.wikidot.com/beemugcarl-campaign-ai");

		addOverrideTask(new OverrideCopyTask("BeeMugCarlAICampaign"));
	}
}
