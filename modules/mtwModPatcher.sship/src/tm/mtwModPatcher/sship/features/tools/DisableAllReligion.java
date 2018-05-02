package tm.mtwModPatcher.sship.features.tools;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.FactionsSection;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.managers.SettlementManager;

import java.util.Arrays;
import java.util.UUID;

public class DisableAllReligion extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		dStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		dRegions = getFileRegisterForUpdated(DescrRegions.class);

		factions = dStrat.Factions;
		settlementManager = new SettlementManager(dStrat, dRegions);

		factions.removeAgentType(FactionsSection.AGENT_PRIEST);
		edb.removeAgentRecruitment(ExportDescrBuilding.AGENT_PRIEST);
		//edb.getLines().removeAllRegexLines("^\\s*religion_level\\s+bonus\\s+");

		edb.getLines().replaceInAllLines("^\\s*religion_level\\s+bonus\\s+.5" , "religion_level bonus 1");

		/* religion_level bonus  < 1.0 nie dziala !!!!! Pojedynczo lub sumowany 0.5 + 0.5 -> nie dziala
				<priest_conversion_rate_modifier float="0.001"/> - zmiana ~ 1% na rok, wydaje sie OK
				<priest_conversion_rate_offset float="0.005"/>
		 */
		//edb.addToCityCastleWallsCapabilities("religion_level bonus -2");

		edb.addToCityCastleWallsCapabilities("agent_limit "+ExportDescrBuilding.AGENT_PRIEST+" 1 ");
		edb.addToCityCastleWallsCapabilities("agent "+ExportDescrBuilding.AGENT_PRIEST+" 0 requires factions { aragon, }");

		val templeFilePath = ConfigurationSettings.VariousDataPath() + "\\TempleTemplate-edb.txt";
		val templeLines = LinesProcessor.load(templeFilePath);

		val religions = Arrays.asList( "heretic", "pagan");
		//val religions = Arrays.asList( "catholic" , "orthodox" , "islam" ,"heretic", "pagan");
		val settlements = settlementManager.getAllSettlements();

		for(val religion : religions) {
			val templeReligionLines = templeLines.subsetCopy(0);

			val buildingName = "hinterland_temple"+religion;
			val levelName = "dievas_altar";

			templeReligionLines.replaceInAllLines("\\{buildingName\\}" , buildingName);
			templeReligionLines.replaceInAllLines("\\{religion\\}" , religion);
			templeReligionLines.replaceInAllLines("\\{levelName\\}" , levelName);

			edb.getLines().insertAtEnd(templeReligionLines.getLines());

			edb.addCapabilities(buildingName, levelName, nullStr, "religion_level bonus 1");

			settlements.forEach( settl -> factions.insertSettlementBuilding( settl.ProvinceName, buildingName, levelName ));
		}
	}

	private DescrStratSectioned dStrat;
	private FactionsSection factions;
	private DescrRegions dRegions;
	private SettlementManager settlementManager;
	private ExportDescrBuilding edb;
	private static final String nullStr = null;

	public DisableAllReligion() {
		super("Disable All Religion");

		addCategory(CATEGORY_TOOLS);
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("5dd5c49a-e60c-4be7-85cb-01f5f4c55e71");
}
