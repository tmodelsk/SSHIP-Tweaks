package tm.mtwModPatcher.sship.features.ai;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.ResourcesProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** */
public class BeeMugCarlCampaignAi extends CampaignAiConfigurator {

	@Override
	public void executeUpdates() throws Exception {
		super.executeUpdates();

		descrSMFactions.updateFactionAtttribute("egypt", "prefers_naval_invasions", "no");

		descrSMFactions.updateFactionAtttribute("poland", "prefers_naval_invasions", "yes");
		descrSMFactions.updateFactionAtttribute("hungary", "prefers_naval_invasions", "yes");
		descrSMFactions.updateFactionAtttribute("teutonic_order", "prefers_naval_invasions", "yes");
	}

	@Override
	protected void initializeDefaults() {
		for (val fi : factionInfos) {
			if(fi.IsPapacy())
				factionAiLabelsMap.put(fi.Symbol, CampaignAiType.BEEMUGCARL_PAPACY);
			else
				factionAiLabelsMap.put(fi.Symbol, CampaignAiType.BEEMUGCARL_DEFAULT);
		}

		factionAiLabelsMap.put("default", CampaignAiType.BEEMUGCARL_DEFAULT);
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val res = new HashSet<UUID>();

		res.add(BeeMugOldCampaignAI.Id);
		res.add(SkynetCampaignAi.Id);
		res.add(QuieterAi.Id);

		return res;
	}

	public BeeMugCarlCampaignAi(ResourcesProvider resourcesProvider) {
		super("BeeMugCarl Campaign AI", resourcesProvider);

		addCategory("Campaign");

		setDescriptionShort("Very good alternative campaign AI created by BeeMugCarl");
		setDescriptionUrl("http://tmsship.wikidot.com/beemugcarl-campaign-ai");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
