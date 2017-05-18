package tm.mtwModPatcher.sship.global.factionFate;

import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.managers.FateScriptManager;
import tm.mtwModPatcher.lib.common.entities.RegionOwnershipInfo;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tomek on 2016-07-08.
 */
public class EgyptFate extends Feature {
	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);

		FateScriptManager fateScriptManager = new FateScriptManager(_CampaignScript);
		List<String> islamF = FactionsDefs.islamFactionsList();

		// Egypt : 7 starting regions, 2 nearby rebels
		fateScriptManager.writeRegionRangeScript("egypt" , 1132 , 1500 , 2 , 6 , 3000,
				"bonus on ANY Region lose" );

		// ### 1174 - Damascus captured
		fateScriptManager.writeRegionNegativeOwnershipScript("egypt" , 1160 , 1176,
				new RegionOwnershipInfo("Damascus" , Arrays.asList("egypt")) , 2000,
				"1174 - Damascus captured");

		// ### Fall of Jerusalem 1187 ###
		// Attacks on Akaba
		fateScriptManager.writeRegionNegativeOwnershipScript("egypt" , 1145 , 1230,
				new RegionOwnershipInfo("Al_Aqaba" , Arrays.asList("egypt")) , 2500,
				"Fall of Jerusalem 1187: Aqaba");

		fateScriptManager.writeRegionNegativeOwnershipScript("egypt" , 1170 , 1230,
				new RegionOwnershipInfo("Jerusalem" , islamF) , 3000,
				"Fall of Jerusalem 1187: Jerusalem");

		//fateScriptManager.writeRegionRangeScript("egypt" , 1180 , 1600 , 3 , 11 , 5000 );
	}

	protected CampaignScript _CampaignScript;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public EgyptFate() {
		super("Egypt Fate");

		setDescriptionUrl("http://tmsship.wikidot.com/factions-fate");
	}
}
