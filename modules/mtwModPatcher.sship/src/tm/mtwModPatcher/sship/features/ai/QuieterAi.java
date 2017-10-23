package tm.mtwModPatcher.sship.features.ai;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.ResourcesProvider;
import tm.mtwModPatcher.lib.common.entities.Religion;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tomek on 24.05.2017.
 */
public class QuieterAi extends CampaignAiConfigurator {
	@Override
	protected void initializeDefaults() {

		val catholics = factionInfos.stream().filter( f -> f.Religion == Religion.Catholic && !f.IsPapacy()).collect(Collectors.toList());
		for(val cathF : catholics)
			factionAiLabelsMap.put(cathF.Symbol, CampaignAiType.QUIETER_CATHOLIC);

		factionAiLabelsMap.put("papal_states", CampaignAiType.QUIETER_PAPAL);

		val muslims = factionInfos.stream().filter( f -> f.Religion == Religion.Islam).collect(Collectors.toList());
		for(val islamF : muslims)
			factionAiLabelsMap.put(islamF.Symbol, CampaignAiType.QUIETER_ISLAM);

		val orthodoxs = factionInfos.stream().filter( f -> f.Religion == Religion.Orthodox).collect(Collectors.toList());
		for(val islamF : orthodoxs)
			factionAiLabelsMap.put(islamF.Symbol, CampaignAiType.QUIETER_DEFAULT);

		factionAiLabelsMap.put("mongols", CampaignAiType.QUIETER_MONGOL);

		val pagans = factionInfos.stream().filter( f -> f.Religion == Religion.Pagan && !f.IsMongols()).collect(Collectors.toList());
		for(val paganF : pagans)
			factionAiLabelsMap.put(paganF.Symbol, CampaignAiType.QUIETER_PAGAN);

		factionAiLabelsMap.put("slave", CampaignAiType.QUIETER_SLAVE);

		factionAiLabelsMap.put("default", CampaignAiType.QUIETER_DEFAULT);
	}


	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(BeeMugOldCampaignAI.Id);
		conflicts.add(SkynetCampaignAi.Id);

		return conflicts;
	}

	public QuieterAi(ResourcesProvider resourcesProvider) {
		super("Quieter AI [kaiser29]", resourcesProvider);

		setDescriptionUrl("http://tmsship.wikidot.com/campaign-ai-s-compilation");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("");
}

// kaiser29 Quieter AI change log
//<!-- CHANGELOG
//		Reworked diplomacy. Added neighbor and non-neighbor peace conditions
//		Added difficulty-based decisions to time-based terminations
//
//		Increased invade priorities for many decisions
//		Added in free strength to invade priority decisions
//
//		Reduced some neg invasion priorities or increased requirements
//		Increased invasion priorities against humans
//
//		Synced invade/defend to buildup/frontline for targets and none/fortified for non-targets (no human specific yet)
//
//		Added invade/defend decisions considering number of enemies
//		Added invade/defend decision to switch to defensive when at war and outnumbered vs. non-human
//		Added in invade/defend decision for human allies to prevent attacks with good relations
//		Increased neg invade priority for trusted allies and allies to 300/200
//
//		Added Allied>Neutral conditions for relationship based invade decision... not necessary but to clarify
//		Rearranged religious invade information and added religious defend information to match
//
//		Increase number of turns forced ceasefire vs. computer so that enemies aren't so set and game dynamic changes
//
//		Changed invade_priority_fs_modifier to 500 from 400
//
//		Reworked offer protect decisions
//		Slightly changed alliance_pts and alliance_against decisions
//		Added -300 invasion_priority for protectorate of trusted ally
//		Rediced invasion_priority for 0 enemies from 300 to 50
//		Added invasion_priority for num_enemies=1 at 50
//		Added special case for attacking biggest neighbour threat when vulnerable
//
//		Added tiered priorities for attacking rebels based on number of enemies
//
//		END v0.8
//
//		START v0.81
//		changed invade_priority_fs_modifier back to 400
//		changed threshold for papal invasion of non-excommunicated to -0.6
//		CHANGED ALL NON-NEIGHBOUR PEACE DEFEND TYPES TO DEFEND_MINIMAL
//		Changed name of slave_faction to slave
//		END 0.81
//
//		START v0.82
//		Reduced invade_priority toward rebels
//		Changed trusted ally enemy invade priority from 400 to 500
//		Removed neighbour requirements from invade priority for atwar and human difficulty
//		Changed allied invade priority from 200 to 100
//		END v0.82
//		-->
