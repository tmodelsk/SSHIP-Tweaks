package tm.mtwModPatcher.sship.global.factionFate;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.entities.RegionOwnershipInfo;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.managers.FateScriptManager;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by tomek on 26.04.2017.
 */
public class CrusaderStatesFate extends Feature {
	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);
		FateScriptManager fateScriptManager = new FateScriptManager(_CampaignScript);

		List<String> islamF = FactionsDefs.islamFactionsList();

		// Crusader Stated : 6 starting regions,
		fateScriptManager.writeRegionRangeScript("jerusalem" , 1132 , 1144 , 2 , 5 , 3000,
				"bonus on ANY Region lose until 1144" );

		// ### Fall of Jerusalem no earlier than 1160 ###
		fateScriptManager.writeRegionNegativeOwnershipScript("jerusalem" , 1132 , 1160,
				new RegionOwnershipInfo("Jerusalem" , Arrays.asList("jerusalem")) , 3000,
				"Fall of Jerusalem no earlier than 1160");
	}

	protected CampaignScript _CampaignScript;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public CrusaderStatesFate() {
		setName("Crusader States Fate");

		setDescriptionUrl("http://tmsship.wikidot.com/factions-fate");
	}
}
