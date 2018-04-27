package tm.mtwModPatcher.sship.features.ai;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.ResourcesProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by tomek on 23.05.2017.
 */
public class SkynetCampaignAi extends CampaignAiConfigurator {

	@Override
	public void setParamsCustomValues() {
		initializeDefaults();
	}

	@Override
	public void executeUpdates() throws Exception {
		super.executeUpdates();

		updateNavalInvasionForAll(true);
	}

	@Override
	protected void initializeDefaults() {
		for(val factionInfo : factionInfos) {
			factionAiLabelsMap.put(factionInfo.symbol, CampaignAiType.SKYNET);
		}

		factionAiLabelsMap.put("default", CampaignAiType.SKYNET);
	}

	public SkynetCampaignAi(ResourcesProvider resourcesProvider) {
		super("Skynet Campaign AI [z3n]", resourcesProvider);

		addCategory(CATEGORY_CAMPAIGN);
		addCategory(CATEGORY_AI);

		setDescriptionShort("Skynet Campaign AI [z3n]");
		setDescriptionUrl("http://tmsship.wikidot.com/campaign-ai-s-compilation");
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(BeeMugOldCampaignAI.Id);
		conflicts.add(QuieterAi.Id);

		return conflicts;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("e2a3bced-4525-4360-afd9-b7b6e78e41d8");
}
