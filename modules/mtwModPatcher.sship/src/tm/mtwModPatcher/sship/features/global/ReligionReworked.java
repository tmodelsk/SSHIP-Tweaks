package tm.mtwModPatcher.sship.features.global;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingTree;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.FactionsSection;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.managers.SettlementManager;
import tm.mtwModPatcher.sship.features.buildings.TemplesTweaks;
import tm.mtwModPatcher.sship.lib.Buildings;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReligionReworked extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		initFileEntities();

		cleanUp();
		//addNativeTemples();
		templeLevels();

		edb.addToCityCastleWallsCapabilities("religion_level bonus 1");
	}

	private void templeLevels() {
		addTempleCastleLevelsFromCity(Buildings.TempleCathCastle , Buildings.TempleCathCity);
		addTempleCastleLevelsFromCity(Buildings.TempleMuslimCastle , Buildings.TempleMuslimCity);
		addTempleCastleLevelsFromCity(Buildings.TempleOrthodoxCastle , Buildings.TempleOrthodoxCity);
	}

	private void addTempleCastleLevelsFromCity(BuildingTree templeCastle, BuildingTree templeCity) {
		val templeCityLevels = templeCity.levels();

		templeCastle.addLevel(templeCityLevels.get(2).levelName);
		templeCastle.addLevel(templeCityLevels.get(3).levelName);
	}

	@SuppressWarnings("unused")
	private void addNativeTemples() throws IOException {
		val templeFilePath = ConfigurationSettings.VariousDataPath() + "\\TempleTemplate-edb.txt";
		val templeLines = LinesProcessor.load(templeFilePath);

		//val religions = Arrays.asList( "heretic", "pagan");
		val religions = Arrays.asList( "catholic" , "orthodox" , "islam" ,"heretic", "pagan");
		val settlements = settlementManager.getAllSettlements();

		val reqFactions = "requires factions { northern_european, middle_eastern, eastern_european, greek, southern_european, }";

		for(val religion : religions) {
			val templeReligionLines = templeLines.subsetCopy(0);

			val buildingName = "hinterland_temple"+religion;
			val levelName = "dievas_altar";

			templeReligionLines.replaceInAllLines("\\{buildingName\\}" , buildingName);
			templeReligionLines.replaceInAllLines("\\{religion\\}" , religion);
			templeReligionLines.replaceInAllLines("\\{levelName\\}" , levelName);

			edb.getLines().insertAtEnd(templeReligionLines.getLines());

			edb.addCapabilities(buildingName, levelName, nullStr, "religion_level bonus "+nativeReligionFactor+" "+ reqFactions +" and region_religion "+religion+" 1");
			for(int n=1;n<=30; n++) {
				val req = "religion_level bonus "+nativeReligionFactor+" "+ reqFactions +" and region_religion "+religion+" "+((int)(n*3.334));
				edb.addCapabilities(buildingName, levelName, nullStr, req);
			}

			settlements.forEach( settl -> factions.insertSettlementBuilding( settl.ProvinceName, buildingName, levelName ));
		}
	}

	private void cleanUp() {
		factions.removeAgentType(FactionsSection.AGENT_PRIEST);
		edb.removeAgentRecruitment(ExportDescrBuilding.AGENT_PRIEST);
		edb.getLines().removeAllRegexLines("^\\s*religion_level\\s+bonus\\s+");

		//edb.getLines().replaceInAllLines("^\\s*religion_level\\s+bonus\\s+.5" , "religion_level bonus 1");

		/* religion_level bonus  < 1.0 nie dziala !!!!! Pojedynczo lub sumowany 0.5 + 0.5 -> nie dziala
				<priest_conversion_rate_modifier float="0.001"/> - zmiana ~ 1% na rok, wydaje sie OK
				<priest_conversion_rate_offset float="0.005"/>
		 */

		edb.addToCityCastleWallsCapabilities("agent_limit "+ExportDescrBuilding.AGENT_PRIEST+" 1 ");
		edb.addToCityCastleWallsCapabilities("agent "+ExportDescrBuilding.AGENT_PRIEST+" 0 requires factions { aragon, }");
	}

	private int nativeReligionFactor = 1;	// 2 dziala zle dla 33 stopni

	private DescrStratSectioned dStrat;
	private FactionsSection factions;
	private DescrRegions dRegions;
	private SettlementManager settlementManager;
	private ExportDescrBuilding edb;
	private static final String nullStr = null;

	private void initFileEntities() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		dStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		dRegions = getFileRegisterForUpdated(DescrRegions.class);

		factions = dStrat.Factions;
		settlementManager = new SettlementManager(dStrat, dRegions);
	}

	public ReligionReworked() {
		super("Religion reworked");

		addCategory(CATEGORY_CAMPAIGN);
		addCategory(CATEGORY_AGENTS);

	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val res = new HashSet<UUID>();
		res.add(TemplesTweaks.Id);
		return res;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b8578518-e77e-4ecc-8579-37cfdeadbcbd");
}
