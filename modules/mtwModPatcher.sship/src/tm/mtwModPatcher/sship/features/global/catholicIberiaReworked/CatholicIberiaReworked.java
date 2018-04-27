package tm.mtwModPatcher.sship.features.global.catholicIberiaReworked;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.text.ExportUnits;
import tm.mtwModPatcher.lib.data.unitModels.BattleModels;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.FactionsSection;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.sship.features.global.CatholicIberiaUnitsRecruitmentIncreased;
import tm.mtwModPatcher.sship.features.overrideTasks.OverrideUnitInfosTask;
import tm.mtwModPatcher.sship.lib.HiddenResources;
import tm.mtwModPatcher.sship.lib.Provinces;

import java.util.*;
import java.util.regex.Pattern;

import static tm.mtwModPatcher.lib.managers.FactionsDefs.*;
import static tm.mtwModPatcher.sship.lib.Buildings.*;
import static tm.mtwModPatcher.sship.lib.Units.*;

public class CatholicIberiaReworked extends Feature {

	@Override
	public void setParamsCustomValues() {
		version = "0.5";
	}

	@Override
	public void executeUpdates() throws Exception {
		initFileEntitiesAndSubModules();

		infantryReworked.execute();
		archersIberiaReworked();
		lightCavalryReworked();
		almughavarsReworked.execute();

		replaceInitialTroops();

		zaragozaToFortress();
		barcelonaUpgrade();
		pamplonaUpgrade();
		rebelPamplona();

		descrStrat.Factions.insertSettlementBuilding(Provinces.Bordeaux, RoadCity, RoadCityLevels.get(0));
	}

	private void archersIberiaReworked() {
		edb.removeUnitRecruitment(PEASANT_ARCHERS , iberiaChristianFactions);

		// ## Prussian Archers are removed from recuitment from all Iberia
		proffArcherRemoveAllFromIberia();
		// Now: add them into Aragon only !!
		proffArchersAddToAragonRegion();
		edu.addOwnershipAll(PRUSSIAN_ARCHERS, SLAVE.symbol);

		edb.removeUnitRecruitment(CROSSBOW_MILITIA, ARAGON);

		edb.addToUnitRecruitment(CROSSBOW_MILITIA, ARAGON, MissileCastle, SPAIN);
		edb.addToUnitRecruitment(URBAN_CROSSBOW_MILITIA, ARAGON, VENICE);

		/*
		TODO: Archers :
		   Dodac Basque Archers tylko w Aragonii.
		   Aragon : dodac Urban Crossbow Militia, usunac Crossbow Militia
		*/
	}
	private void proffArchersAddToAragonRegion() {
		val unit = PRUSSIAN_ARCHERS;
		val levels = MissileCastleLevels;
		val requireHr = " and not event_counter HEAVY_MAIL_ARMOR 1 and hidden_resource "+HiddenResources.Aragon;

		val reqHigh = "factions { england, aragon, spain, portugal, }" + requireHr;
		val reqMedium = "factions { russia, kievan_rus, slave, }" + requireHr;
		val reqLow = "factions { jerusalem, pisa, venice, papal_states, hre, france, }" + requireHr;

		val building = new BuildingLevel(MissileCastle, levels.get(1), SettlType.Castle);
		edb.addRecuitment(building, unit, 1, 0.25, 1, 0, reqHigh);
		edb.addRecuitment(building, unit, 0, 0.17, 1, 0, reqMedium);
		edb.addRecuitment(building, unit, 0, 0.13, 1, 0, reqLow);

		building.LevelName = levels.get(2);
		edb.addRecuitment(building, unit, 1, 0.34, 2, 0, reqHigh);
		edb.addRecuitment(building, unit, 1, 0.25, 2, 0, reqMedium);
		edb.addRecuitment(building, unit, 0, 0.17, 1, 0, reqLow);

		building.LevelName = levels.get(3);
		edb.addRecuitment(building, unit, 1, 0.5, 3, 0, reqHigh);
		edb.addRecuitment(building, unit, 1, 0.34, 3, 0, reqMedium);
		edb.addRecuitment(building, unit, 1, 0.25, 3, 0, reqLow);
	}
	private void proffArcherRemoveAllFromIberia() {
		// ### Remove all Prussian Archer recruitment from Iberia provinces
		val paRegex = Pattern.compile("^\\s*recruit_pool\\s+\"" +PRUSSIAN_ARCHERS + "\"\\s+");
		val paRecruitmentList = edb.findRecruitmentsByRegex(paRegex);

		for(val paRecr : paRecruitmentList) {
			val unitRequire = paRecr.getUnitRequireSimple();

			val reqStr = unitRequire.RestConditions;

			// ## Positive 'iberia' recruitment is on: or hidden_resource aragon / spain / portugal /
			val orHiddenResRegex = Pattern.compile("or\\s+hidden_resource");
			if(orHiddenResRegex.matcher(reqStr).find()) {
				// let's remove or hidden_resource spain ...
				val newRect = paRecr.clone();
				val newUnitRequire = paRecr.getUnitRequireSimple();

				String newRestContidions = newUnitRequire.RestConditions;
				for (val hrToRemove : allSpainHiddenResList) {
					newRestContidions = newRestContidions.replaceAll("or\\s+hidden_resource\\s+"+hrToRemove , "");
				}
				newUnitRequire.RestConditions = newRestContidions;

				newRect.setRequirementStr(newUnitRequire);
				val newLine = newRect.toRecruitmentPoolLine();
				edb.getLines().replaceLine(newRect.lineNumber, newLine);
			}
		}
	}

	private void lightCavalryReworked() {
		// add Mounted Sergeants
		edu.addOwnershipAll(MOUNTED_SERGEANTS, ARAGON.symbol);
		edu.addOwnershipAll(MOUNTED_SERGEANTS, SPAIN.symbol);
		edu.addOwnershipAll(MOUNTED_SERGEANTS, PORTUGAL.symbol);

		edb.addToUnitRecruitment(MOUNTED_SERGEANTS, ARAGON, allSpainHiddenResList, PISA);
		edb.addToUnitRecruitment(MOUNTED_SERGEANTS, SPAIN, PISA);
		edb.addToUnitRecruitment(MOUNTED_SERGEANTS, PORTUGAL, PISA);

		/*
		 TODO: dodac battleModels : dodac dla Mounted Seargent .
		*/

		edb.removeUnitRecruitment(ALFORRATS);
	}

	private void zaragozaToFortress() {
		val prov = Provinces.Zaragoza;
		descrStrat.removeAllBuildings(prov);

		descrStrat.setToCastle(prov);
		descrStrat.setFactionCreator(prov, FactionsDefs.ARAGON.symbol);
		descrStrat.insertSettlementBuilding(prov, WallsCastleSpec.Name, WallsCastleSpec.L4_Fortress);
		descrStrat.insertSettlementBuilding(prov, BarracksCastle, BarracksCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, StablesCastle, StablesCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, MissileCastle, MissileCastleLevels.get(0));

		descrStrat.insertSettlementBuilding(prov, MarketCastle, MarketCastleLevels.get(2));
		descrStrat.insertSettlementBuilding(prov, PortCastle, PortCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SeaTradeCastle, SeaTradeCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, RiverAccess, RiverAccessLevel);
		descrStrat.insertSettlementBuilding(prov, TempleCatholicCastle, TempleCatholicCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, MonasteryCatholicCastle, MonasteryCatholicCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, TavernCastle);
		descrStrat.insertSettlementBuilding(prov, WaterSupply, WaterSupplyLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, Health, HealthLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, StoneMason, StoneMasonLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, LoggingCamp, LoggingCampLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SmithCastle, SmithCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, BakeryCastle, BakeryCastleLevel);

		descrStrat.insertSettlementBuilding(prov, RoadCastle, RoadCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, Farms, FarmsLevels.get(2));

		descrStrat.insertSettlementBuilding(prov, MinesCastle, MinesCastleLevels.get(0));

		// Order House
		descrRegions.addResource(prov, DescrRegions.KnightsOfSantiago, DescrRegions.StJohnKnights);
		descrStrat.insertSettlementBuilding(prov, HospitallersCastle, HospitallersCastleLevels.get(0));

		//resource	iron,	73, 152
		descrStrat.Resources.addResource(prov, "iron", 73, 152);
		requestForMapRemoval();

		// Merchant - move to top spot :  x162 y158 kolo Florencji
		val lines = descrStrat.Factions.content().lines();
		val merchantIndex = lines.findExpFirstRegexLine("character\\s+Berenguel\\s+Cano\\s*,\\s*merchant.+x\\s+67,\\s*y\\s+118");
		lines.replaceLine(merchantIndex, "character\tBerenguel Cano, merchant, male, age 38, x 162, y 158 ; near Florence");
		//character	Berenguel Cano, merchant, male, age 38, x 67, y 118
	}
	private void barcelonaUpgrade() {
		val prov = Provinces.Barcelona;

		descrStrat.insertSettlementBuilding(prov, WaterSupply, WaterSupplyLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SeaTradeCity, SeaTradeCityLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, MerchantGuild, MerchantGuildLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, ItalianTraders, ItalianTradersLevels.get(0));

		descrStrat.removeSettlementBuilding(prov, PortCity);
		descrStrat.insertSettlementBuilding(prov, PortCity, PortCityLevels.get(1));

		descrStrat.setSettlementBuilding(prov, CouncilCity.level(2));

	}
	private void pamplonaUpgrade() {
		val prov = Provinces.Pamplona;

		descrStrat.insertSettlementBuilding(prov, RoadCastle, RoadCastleLevels.get(0));
		descrRegions.addResource(prov, DescrRegions.KnightsOfSantiago );
	}
	private void rebelPamplona() {
		val prov = Provinces.Pamplona;
		val factionsSect = descrStrat.Factions;
		val lines = factionsSect.content().lines();

		// remove Garcia Ramirez general + cog
		val startIndex = lines.findExpFirstRegexLine("^\\s*character\\s+Garcia\\s+Ramirez,\\s+named\\s+character");
		val endIndex = lines.findExpFirstRegexLine("^\\s*unit\\s+cog\\s+", startIndex+1);
		lines.removeRange(startIndex, endIndex);

		factionsSect.moveSettlementBeforeSettl(prov, Provinces.Bordeaux);

		factionsSect.setSetlementLevel(prov, SettlementLevel.L3_LargeTown);
		factionsSect.removeSettlementBuilding(prov, WallsCastleSpec.Name);
		factionsSect.insertSettlementBuildingOnStart(prov, WallsCastleSpec.Name, WallsCastleSpec.L3_Castle);

		factionsSect.removeSettlementBuilding(prov, MissileCastle);
		factionsSect.insertSettlementBuilding(prov, MissileCastle, MissileCastleLevels.get(1));

		factionsSect.removeSettlementBuilding(prov, StablesCastle);
		factionsSect.insertSettlementBuilding(prov, StablesCastle, StablesCastleLevels.get(2));

		factionsSect.insertSettlementBuilding(prov, TempleCatholicCastle, TempleCatholicCastleLevels.get(0));


		// put Garcia Ramirez in Zaragoza //  x 71, y 145 ;Zaragoza

		String GarciaRamirez = "";
		GarciaRamirez += "character	Garcia Ramirez, named character, male, age 20, x 69, y 139 ; near Zaragoza" +nl;	// x 71, y 144
		GarciaRamirez += "traits LoyaltyStarter 1 , Intelligent 1 , GoodBuilder 1, MilitaryInclination 1 , Military_Edu 1 , GoodCommander 1 , Royal_Blood_Aragonese 1 , ReligionStarter 1"+nl;
		GarciaRamirez += "army" +nl;
		GarciaRamirez += "unit\t\tSE Bodyguard\t\t\t\texp 1 armour 0 weapon_lvl 0" +nl;

		String AlfonsoRamirezRegex = "^character\\s+Alfonso\\s+Ramirez";
		val generalAlfonsoIndex = lines.findExpFirstRegexLine(AlfonsoRamirezRegex);
		lines.insertAt(generalAlfonsoIndex, GarciaRamirez);

		String garrison = ""; // Felipe Tejedor , Wilfredo Lucio Alejandro
		garrison += "\n" +
				"character	sub_faction aragon, Garcia Ramirez, named character, male, age 29, x 66, y 153 ;Pamplona\n" +
				"traits NaturalMilitarySkill 1 , GoodCommander 1 , ReligiousActivity 4\n" +
				"army	\n" +
				"unit		SE Bodyguard					exp 2 armour 0 weapon_lvl 0\n" +
				"unit		Mailed Knights					exp 2 armour 0 weapon_lvl 0  \n" +
				"unit		Mailed Knights					exp 1 armour 0 weapon_lvl 0  \n" +
				"unit		"+ CABALLEROS_VILLANOS +"		exp 2 armour 0 weapon_lvl 0  \n" +
				"unit		"+ CABALLEROS_VILLANOS +"		exp 1 armour 0 weapon_lvl 0  \n" +
				"unit		"+ CABALLEROS_VILLANOS +"		exp 1 armour 0 weapon_lvl 0  \n" +
				"unit		Dismounted Sword Mailed Knights	exp 2 armour 0 weapon_lvl 0\n" +
				"unit		Dismounted Sword Mailed Knights	exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ MERCENARY_SPEARMEN +"		exp 2 armour 0 weapon_lvl 0\n" +
				"unit		"+ MERCENARY_SPEARMEN +"		exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 2 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 3 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 2 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ PRUSSIAN_ARCHERS +"			exp 2 armour 0 weapon_lvl 0\n" +
				"unit		"+ PRUSSIAN_ARCHERS +"			exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ CROSSBOW_MILITIA +"			exp 1 armour 0 weapon_lvl 0";

		val rebelSettlementsEndIndex = factionsSect.loadSettlemenBlockEndIndex("Tabriz_Province");
		lines.insertAt(rebelSettlementsEndIndex+1, garrison);
	}

	private void replaceInitialTroops() throws PatcherLibBaseEx {
		LinesProcessor lines = descrStrat.Factions.content().lines();
		val fs = factionsSection;

		int aragonIndex = lines.findExpFirstRegexLine("^;## ARAGON ##\\s*");
		// Zaragoza
		fs.replaceInitialUnit(PEASANT_ARCHERS, PRUSSIAN_ARCHERS , aragonIndex);
		fs.replaceInitialUnit(PEASANT_ARCHERS, CROSSBOW_MILITIA , aragonIndex);
		fs.replaceInitialUnit(PEASANT_ARCHERS,null , aragonIndex);

		fs.replaceInitialUnit(JAVELINMEN, null , aragonIndex);
		fs.replaceInitialUnit(JAVELINMEN, null , aragonIndex);

		fs.replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , aragonIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, ALMUGHAVARS , aragonIndex , 1);
		fs.replaceInitialUnit(SPEAR_MILITIA, ALMUGHAVARS , aragonIndex);

		fs.replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		fs.replaceInitialUnit(ALFORRATS, null , aragonIndex);

		// Barcelona
		fs.replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, URBAN_CROSSBOW_MILITIA , aragonIndex);
		fs.replaceInitialUnit(PEASANT_ARCHERS, null , aragonIndex);

		// Pamlpona
		fs.replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);

		int spainIndex = lines.findExpFirstRegexLine(";## CASTILE & LEON ##\\s*");
		fs.replaceInitialUnit(JAVELINMEN, PRUSSIAN_ARCHERS , spainIndex);
		fs.replaceInitialUnit(JAVELINMEN, CROSSBOW_MILITIA , spainIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , spainIndex , 1);
		fs.replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , spainIndex , 1);
		fs.replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , spainIndex , 1);

		int portugalIndex = lines.findExpFirstRegexLine(";## PORTUGAL ##\\s*");
		fs.replaceInitialUnit(PEASANT_ARCHERS, PRUSSIAN_ARCHERS , portugalIndex);
		fs.replaceInitialUnit(LUSITANIAN_JAVELINMEN, CROSSBOW_MILITIA , portugalIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , portugalIndex , 1);
	}

	private void initFileEntitiesAndSubModules() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		eUnits = getFileRegisterForUpdated(ExportUnits.class);
		descrMercenaries = getFileRegisterForUpdated(DescrMercenaries.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		factionsSection = descrStrat.Factions;
		battleModels = getFileRegisterForUpdated(BattleModels.class);
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);

		val sharedCtx = new SharedContext();
		sharedCtx.edb = edb;
		sharedCtx.edu = edu;
		sharedCtx.eUnits = eUnits;
		sharedCtx.descrMercenaries = descrMercenaries;
		sharedCtx.descrStrat = descrStrat;
		sharedCtx.factionsSection = factionsSection;
		sharedCtx.battleModels = battleModels;

		sharedCtx.iberiaChristianFactions = iberiaChristianFactions;
		sharedCtx.otherChristianAndSlaveFactions = otherChristianAndSlaveFactions;
		sharedCtx.allSpainHiddenResList = allSpainHiddenResList;

		almughavarsReworked = new AlmughavarsReworked(sharedCtx);
		infantryReworked = new InfantryReworked(sharedCtx);

	}
	@Override
	public Set<UUID> getConflictingFeatures() {
		return new HashSet<>(Collections.singletonList(CatholicIberiaUnitsRecruitmentIncreased.Id));
	}
	public CatholicIberiaReworked() {
		super("Catholic Iberia reworked");
		allSpainHiddenResList = Arrays.asList(HiddenResources.Aragon, HiddenResources.Spain, HiddenResources.Portugal, HiddenResources.Andalusia);

		addCategory(CATEGORY_CAMPAIGN);
		addCategory(CATEGORY_UNITS);

		addOverrideTask(new OverrideUnitInfosTask());

		iberiaChristianFactions = FactionsDefs.toFactionInfos(Arrays.asList("aragon", "spain" , "portugal"));

		otherChristianAndSlaveFactions = FactionsDefs.christianFactionInfos();
		otherChristianAndSlaveFactions.removeAll(iberiaChristianFactions);
		otherChristianAndSlaveFactions.add(SLAVE);
	}

	private final List<FactionInfo> iberiaChristianFactions;
	private final List<FactionInfo> otherChristianAndSlaveFactions;
	private final List<String> allSpainHiddenResList;

	private ExportDescrBuilding edb;
	private ExportDescrUnitTyped edu;
	private ExportUnits eUnits;
	private DescrMercenaries descrMercenaries;
	private DescrStratSectioned descrStrat;
	private FactionsSection factionsSection;
	private BattleModels battleModels;
	private DescrRegions descrRegions;

	private AlmughavarsReworked almughavarsReworked;
	private InfantryReworked infantryReworked;

	private final String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b59df073-1de3-4358-b2bf-a88d781343e1");
}
