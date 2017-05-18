package tm.mtwModPatcher.sship.global.factionFate;

import tm.mtwModPatcher.lib.managers.FateScriptManager;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;

import java.util.UUID;

/**
 * Created by Tomek on 2016-07-08. rum
 */
public class TurksFate extends Feature {
	// rum
	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);

		FateScriptManager fateScriptManager = new FateScriptManager(_CampaignScript);

		// Sultanate of Rum - Seljuk Turks : 2 starting regions
		fateScriptManager.writeRegionRangeScript("rum" , 1132 , 1430 , 1 , 10 , 3000 ,
				"Sultanate of Rum expansion");

		// Fall of Jerusalem 1187
		fateScriptManager.writeRegionRangeScript("rum" , 1430 , 1600 , 2 , 15 , 5000,
				"Sultanate of Rum second phase of expansion" );

	}

	protected CampaignScript _CampaignScript;


	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public TurksFate() {
		super("Turks Fate");

		setDescriptionUrl("http://tmsship.wikidot.com/factions-fate");
	}
}
