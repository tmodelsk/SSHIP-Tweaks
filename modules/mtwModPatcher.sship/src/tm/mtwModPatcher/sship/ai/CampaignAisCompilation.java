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
	public void executeUpdates() throws Exception {
		super.executeUpdates();

		descrSMFactions.updateFactionAtttribute("egypt", "prefers_naval_invasions", "no");

		descrSMFactions.updateFactionAtttribute("poland", "prefers_naval_invasions", "yes");
		descrSMFactions.updateFactionAtttribute("hungary", "prefers_naval_invasions", "yes");
		descrSMFactions.updateFactionAtttribute("teutonic_order", "prefers_naval_invasions", "yes");
	}

	@Override
	protected void initializeDefaults() {
		// ## Bee Mug Carl AI as base, for everyone
		for (val fi : factionInfos) factionAiLabelsMap.put(fi.Symbol, CampaignAiType.BEEMUGCARL_DEFAULT);
		factionAiLabelsMap.put("default", CampaignAiType.BEEMUGCARL_DEFAULT);

		// ## Papacy - Quieter AI papacy : 1
		factionAiLabelsMap.replace("papal_states", CampaignAiType.QUIETER_PAPAL);

		// ## SkynetAI : 8: Serbia, Lithuania, Cumans, Anatolia Turks
		factionAiLabelsMap.replace("teutonic_order", CampaignAiType.SKYNET);	// Serbia
		factionAiLabelsMap.replace("lithuania", CampaignAiType.SKYNET);
		factionAiLabelsMap.replace("cumans", CampaignAiType.SKYNET);
		//factionAiLabelsMap.replace("rum", CampaignAiType.SKYNET);				// Anatolia Seljuks
		factionAiLabelsMap.replace("timurids", CampaignAiType.SKYNET);			// Georgia
		factionAiLabelsMap.replace("denmark", CampaignAiType.SKYNET);
		//factionAiLabelsMap.replace("kwarezm", CampaignAiType.SKYNET);			// ZENGIDS
		factionAiLabelsMap.replace("scotland", CampaignAiType.SKYNET);

		// ## Quiter AI : 7 : standard - SSHIP derived. Aggressive but loyal to allies - Chivalrus
		factionAiLabelsMap.replace("portugal", CampaignAiType.QUIETER_CATHOLIC);
		factionAiLabelsMap.replace("spain", CampaignAiType.QUIETER_CATHOLIC);
		factionAiLabelsMap.replace("aragon", CampaignAiType.QUIETER_CATHOLIC);
		factionAiLabelsMap.replace("sicily", CampaignAiType.QUIETER_CATHOLIC);

		factionAiLabelsMap.replace("france", CampaignAiType.QUIETER_CATHOLIC);

		factionAiLabelsMap.replace("poland", CampaignAiType.QUIETER_CATHOLIC);
		factionAiLabelsMap.replace("hungary", CampaignAiType.QUIETER_CATHOLIC);

		// ## SSHIP : 3 : HRE Germany , Byzantium
		factionAiLabelsMap.replace("hre", CampaignAiType.SSHIP_CATHOLIC);
		factionAiLabelsMap.replace("byzantium", CampaignAiType.SSHIP_ORTHODOX);
		factionAiLabelsMap.replace("milan", CampaignAiType.SSHIP_ISLAM);		// ABBASSIDS

		// BeeMugCarl AI : 30 - 19 = 11
		// Moors , Fatamids , Seljuks Easter,
		// Crusaders , England, Novogrod , Kiev , Pisa , Venetia, Norvegians
		// Slaves
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
