package tm.mtwModPatcher.sship.features.global.factionFate;

import tm.mtwModPatcher.lib.managers.FateScriptManager;
import tm.mtwModPatcher.lib.common.entities.RegionOwnershipInfo;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Tomek on 2016-07-08.
 */
public class MoorsFate extends Feature {

	@Override
	public void setParamsCustomValues() {}

	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);

		FateScriptManager fateScriptManager = new FateScriptManager(_CampaignScript);

		// MOORS : 12 starting regions, 2 nearby rebels

		fateScriptManager.writeRegionRangeScript("moors" , 1132 , 1500 , 3 , 11 , 3000,
				"No any region loose" );	// bonus on ANY Region losse

		// ## Offensive on Toledo :
		fateScriptManager.writeRegionNegativeOwnershipScript("moors" , 1150 , 1190,
				new RegionOwnershipInfo("Toledo" , Arrays.asList("moors")) , 3000 , "Offensive on Toledo");

		// #### Offensive before Battle of Las Navas Del Torras
		fateScriptManager.writeRegionRangeScript("moors" , 1195 , 1220 , 1 , 18 , 3000 ,
				"Offensive before Battle of Las Navas de Tolosa (org 1212)");

}

	protected CampaignScript _CampaignScript;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("3e68aab7-9235-47c9-a7e2-56aca55df7e1");

	public MoorsFate() {
		super("Moors Fate");

		setDescriptionUrl("http://tmsship.wikidot.com/factions-fate");
	}
}
