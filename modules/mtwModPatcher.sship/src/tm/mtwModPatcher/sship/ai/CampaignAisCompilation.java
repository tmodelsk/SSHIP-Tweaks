package tm.mtwModPatcher.sship.ai;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.ResourcesProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Various campaign AI's assigned to various factions
 */
public class CampaignAisCompilation extends CampaignAiConfigurator {

	@Override
	protected void initializeDefaults() {
		// ## Bee Mug Carl AI as base, for everyone
		for (val fi : factionInfos) factionAiLabelsMap.put(fi.Symbol, CampaignAiType.BEEMUGCARL_DEFAULT);
		factionAiLabelsMap.put("default", CampaignAiType.BEEMUGCARL_DEFAULT);

		// ## Papacy - Quieter AI papacy
		factionAiLabelsMap.replace("papal_states", CampaignAiType.QUIETER_PAPAL);

		// ## SkynetAI - Serbia, Lithuania, Cumans, Anatolia Turks
		factionAiLabelsMap.replace("teutonic_order", CampaignAiType.SKYNET);	// serbia
		factionAiLabelsMap.replace("lithuania", CampaignAiType.SKYNET);
		factionAiLabelsMap.replace("cumans", CampaignAiType.SKYNET);
		factionAiLabelsMap.replace("rum", CampaignAiType.SKYNET);	// Anatolia Seljuks

		// ## Quiter AI standard - SSHIP derived. Aggressive but loyal to allies
		factionAiLabelsMap.replace("portugal", CampaignAiType.QUIETER_CATHOLIC);
		factionAiLabelsMap.replace("spain", CampaignAiType.QUIETER_CATHOLIC);
		factionAiLabelsMap.replace("aragon", CampaignAiType.QUIETER_CATHOLIC);

		factionAiLabelsMap.replace("france", CampaignAiType.QUIETER_CATHOLIC);

		factionAiLabelsMap.replace("poland", CampaignAiType.QUIETER_CATHOLIC);
		factionAiLabelsMap.replace("hungary", CampaignAiType.QUIETER_CATHOLIC);
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val res = new HashSet<UUID>();

		res.add(BeeMugOldCampaignAI.Id);
		res.add(BeeMugCarlCampaignAi.Id);
		res.add(SkynetCampaignAi.Id);
		res.add(QuieterAi.Id);

		return res;
	}

	public CampaignAisCompilation(ResourcesProvider resourcesProvider) {
		super("Campaign AI's compilation", resourcesProvider);

		setDescriptionShort("Various campaign AI's assigned to various factions. BeeMugCarl AI , Quieter AI [kaiser29] , Skynet AI [z3n]");
		setDescriptionUrl("http://tmsship.wikidot.com/campaign-ai-s-compilation");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
