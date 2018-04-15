package tm.mtwModPatcher.sship.features.global.catholicIberiaReworked;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.unitModels.BattleModels;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.FactionsSection;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.sship.features.global.CatholicIberiaUnitsRecruitmentIncreased;
import tm.mtwModPatcher.sship.lib.HiddenResources;
import tm.mtwModPatcher.sship.lib.Provinces;

import java.util.*;
import java.util.regex.Pattern;

import static tm.mtwModPatcher.lib.common.entities.UnitReplenishRate.*;
import static tm.mtwModPatcher.lib.managers.FactionsDefs.*;
import static tm.mtwModPatcher.sship.lib.Buildings.*;
import static tm.mtwModPatcher.sship.lib.Units.*;


public class CatholicIberiaReworked extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		initFileEntities();

		/*
		 TODO: dodac battleModels : Spearmen i Armored Spearmen skopiowac z CatholicIberiaUnitsRecruitmentIncreased,
		 			dodac dla Mounted Seargent .
		*/

		aragonRemoveSpearMilitiaAddUrbanMilitia();
		spearmenForIberians();
		armoredSpearmen();
		archersIberiaReworked();
		lightCavalryReworked();
		almughavarsReworked();

		replaceInitialTroops();

		zaragozaToFortress();
		barcelonaUpgrade();
		pamplonaUpgrade();
		rebelPamplona();

		descrStrat.Factions.insertSettlementBuilding(Provinces.Bordeaux, RoadCity, RoadCityLevels.get(0));
	}

	private void aragonRemoveSpearMilitiaAddUrbanMilitia() {
		edb.removeUnitRecruitment(JAVELINMEN , ARAGON);
		edb.removeUnitRecruitment(LUSITANIAN_JAVELINMEN , ARAGON);
		edb.removeUnitRecruitment(PEASANTS , ARAGON);
		edb.removeUnitRecruitment(SPEAR_MILITIA , ARAGON);

		edb.removeUnitRecruitment(URBAN_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(URBAN_SPEAR_MILITIA, ARAGON, BarracksCity, PISA);

		edu.addOwnershipAll(NE_URBAN_MILITIA, ARAGON.symbol);
		edb.removeUnitRecruitment(NE_URBAN_MILITIA , ARAGON);
		edb.addToUnitRecruitment(NE_URBAN_MILITIA, ARAGON, BarracksCity, PISA);

		edu.addOwnershipAll(PAVISE_SPEAR_MILITIA, ARAGON.symbol);
		edb.removeUnitRecruitment(PAVISE_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(PAVISE_SPEAR_MILITIA, ARAGON, BarracksCity, PISA);
	}
	private void almughavarsReworked() {
		edu.addOwnershipAll(ALMUGHAVARS, SLAVE.symbol);

		// # Adjust recuit cost
		val footKnights = edu.loadUnit(DISMOUNTED_SWORD_MAILED_KNIGHTS);
		//val spearMilitia = edu.loadUnit(SPEAR_MILITIA);
		val almughavars = edu.loadUnit(ALMUGHAVARS);

		almughavars.StatCost.Cost = footKnights.StatCost.Cost;
		almughavars.StatCost.Upkeep = almughavars.Soldier.NumberOfMen * 5;

		// ## Add Almughavars recuitment ##
		edb.removeUnitRecruitment(ALMUGHAVARS);
		val requireSuffix = " and hidden_resource aragon";

		val reqHigh = "factions { "+ FactionsDefs.toCsv(iberiaChristianFactions) +" }" + requireSuffix;
		val reqLow = "factions { "+ FactionsDefs.toCsv(otherChristianAndSlaveFactions) +" }" + requireSuffix;

		val building = new BuildingLevel(BarracksCastle, BarracksCastleLevels, SettlType.Castle);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R8,1, 0 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 0, R10,1, 0 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R7,2, 0 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 0, R9,1, 0 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 2, R6,3, 1 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R8,2, 0 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 2, R5,4, 2 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R7,3, 1 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 3, R4,5, 2 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R6,3, 1 , reqLow);
	}
	private void spearmenForIberians() {
		// ## SERGEANT_SPEARMEN recuitment ##
		edu.addOwnershipAll(SERGEANT_SPEARMEN, ARAGON.symbol);
		edu.addOwnershipAll(SERGEANT_SPEARMEN, SPAIN.symbol);
		edu.addOwnershipAll(SERGEANT_SPEARMEN, PORTUGAL.symbol);

		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , ARAGON);
		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , SPAIN);
		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , PORTUGAL);

		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, ARAGON, BarracksCastle, allSpainHiddenResList, FRANCE);
		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, SPAIN, BarracksCastle, FRANCE);
		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, PORTUGAL, BarracksCastle, FRANCE);

		// add Spearmen recruitment for 1st level barracks only for Aragon and in aragon, replenish 50% as in 2nd level.
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(0), CastleType, SERGEANT_SPEARMEN,
				1, 0.08,1, 0 , "factions { aragon, } and not event_counter FULL_PLATE_ARMOR 1 and hidden_resource aragon");
	}
	private void armoredSpearmen() {
		/* Armored Sergeants
			(Castle) Castille: Armoured Serjeants
			At 1245, make Armoured Serjeants available for Aragón and Portugal,

			a wiec : dla Spain : Armoured Serjeants jak pisa / sicily . /  - dostepne od poczatku

			Aragon i Portugal, np: Armoured Serjeants jak england -> od HEAVY_MAIL_ARMOR
		 */

		edu.addOwnershipAll(ARMORED_SERGEANTS, ARAGON.symbol);
		edu.addOwnershipAll(ARMORED_SERGEANTS, SPAIN.symbol);
		edu.addOwnershipAll(ARMORED_SERGEANTS, PORTUGAL.symbol);

		edb.addToUnitRecruitment(ARMORED_SERGEANTS, SPAIN, BarracksCastle, allSpainHiddenResList, PISA);

		edb.addToUnitRecruitment(ARMORED_SERGEANTS, ARAGON, BarracksCastle, allSpainHiddenResList, ENGLAND);
		edb.addToUnitRecruitment(ARMORED_SERGEANTS, PORTUGAL, BarracksCastle, ENGLAND);
	}

	private void archersIberiaReworked() {
		edb.removeUnitRecruitment(PEASANT_ARCHERS , ARAGON);
		edb.removeUnitRecruitment(PEASANT_ARCHERS , SPAIN);
		edb.removeUnitRecruitment(PEASANT_ARCHERS , PORTUGAL);

		// ## Prussian Archers are removed from recuitment from all Iberia
		proffArcherRemoveAllFromIberia();
		// Now: add them into Aragon only !!
		proffArchersAddToAragonRegion();
		edu.addOwnershipAll(PRUSSIAN_ARCHERS, SLAVE.symbol);

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
		edb.insertRecruitmentBuildingCapabilities(building, unit, 1, 0.25, 1, 0, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 0, 0.17, 1, 0, reqMedium);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 0, 0.13, 1, 0, reqLow);

		building.LevelName = levels.get(2);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 1, 0.34, 2, 0, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 1, 0.25, 2, 0, reqMedium);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 0, 0.17, 1, 0, reqLow);

		building.LevelName = levels.get(3);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 1, 0.5, 3, 0, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 1, 0.34, 3, 0, reqMedium);
		edb.insertRecruitmentBuildingCapabilities(building, unit, 1, 0.25, 3, 0, reqLow);
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

		caballerosVillanosReworked();
		removeAlforrats();
	}
	private void caballerosVillanosReworked() throws PatcherLibBaseEx {
		String cabVillanos = CABALLEROS_VILLANOS;

		edb.removeUnitRecruitment(cabVillanos);
		edu.addOwnershipAll(cabVillanos, ARAGON.symbol);
		edu.addOwnershipAll(cabVillanos, SLAVE.symbol);

		val requireSuffix = " and hidden_resource aragon";

		val reqHigh = "factions { "+ FactionsDefs.toCsv(iberiaChristianFactions) +" }" + requireSuffix;
		val reqLow = "factions { "+ FactionsDefs.toCsv(otherChristianAndSlaveFactions) +" }" + requireSuffix;

		val building = new BuildingLevel(StablesCastle , StablesCastleLevels , SettlType.Castle);
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 1, R11, 1, 0, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 0, R13, 1, 0, reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 1, R10, 1, 0, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 0, R12, 1, 0, reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 1, R9, 2, 1, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 0, R11, 1, 0, reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 1, R8, 2, 1, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 1, R10, 2, 0, reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 2, R7, 3, 2, reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building , cabVillanos , 1, R9, 2, 1, reqLow);
	}
	private void removeAlforrats() {
		edb.removeUnitRecruitment(ALFORRATS);
	}

	private void zaragozaToFortress() {
		val prov = Provinces.Zaragoza;
		descrStrat.removeAllBuildings(prov);

		descrStrat.setToCastle(prov);
		descrStrat.setFactionCreator(prov, FactionsDefs.ARAGON.symbol);
		descrStrat.insertSettlementBuilding(prov, WallsCastle.Name, WallsCastle.L4_Fortress);
		descrStrat.insertSettlementBuilding(prov, BarracksCastle, BarracksCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, StablesCastle, StablesCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, MissileCastle, MissileCastleLevels.get(0));

		descrStrat.insertSettlementBuilding(prov, MarketCastle, MarketCastleLevels.get(2));
		descrStrat.insertSettlementBuilding(prov, PortCastle, PortCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SeaTradeCastle, SeaTradeCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, RiverAccess, RiverAccessLevel);
		descrStrat.insertSettlementBuilding(prov, TempleCatholicCastle, TempleCatholicCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, MonasteryCatholicCastle, MonasteryCatholicCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, TavernCastle, TavernCastleLevels.get(0));
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

		factionsSect.removeSettlementBuilding(prov, WallsCastle.Name);
		factionsSect.insertSettlementBuilding(prov, WallsCastle.Name, WallsCastle.L3_Castle);

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
				"unit		SE Bodyguard				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Mailed Knights				exp 0 armour 0 weapon_lvl 0  \n" +
				"unit		Mailed Knights				exp 0 armour 0 weapon_lvl 0  \n" +
				"unit		"+ CABALLEROS_VILLANOS +"				exp 1 armour 0 weapon_lvl 0  \n" +
				"unit		"+ CABALLEROS_VILLANOS +"				exp 1 armour 0 weapon_lvl 0  \n" +
				"unit		"+ CABALLEROS_VILLANOS +"				exp 1 armour 0 weapon_lvl 0  \n" +
				"unit		Dismounted Sword Mailed Knights				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		Dismounted Sword Mailed Knights				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		"+ URBAN_SPEAR_MILITIA +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ ALMUGHAVARS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ PRUSSIAN_ARCHERS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ PRUSSIAN_ARCHERS +"				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		"+ CROSSBOW_MILITIA +"				exp 1 armour 0 weapon_lvl 0    \n" +
				"unit		"+ CROSSBOW_MILITIA +"				exp 0 armour 0 weapon_lvl 0";

		val rebelSettlementsEndIndex = factionsSect.loadSettlemenBlockEndIndex("Tabriz_Province");
		lines.insertAt(rebelSettlementsEndIndex+1, garrison);
	}

	private void replaceInitialTroops() throws PatcherLibBaseEx {
		LinesProcessor lines = descrStrat.Factions.content().lines();
		val fs = factionsSection;

		int aragonIndex = lines.findExpFirstRegexLine("^;## ARAGON ##\\s*");
		// Zaragoza
		fs.replaceInitialUnit(PEASANT_ARCHERS,PRUSSIAN_ARCHERS , aragonIndex);
		fs.replaceInitialUnit(PEASANT_ARCHERS,CROSSBOW_MILITIA , aragonIndex);
		fs.replaceInitialUnit(PEASANT_ARCHERS,null , aragonIndex);

		fs.replaceInitialUnit(JAVELINMEN,null , aragonIndex);
		fs.replaceInitialUnit(JAVELINMEN,null , aragonIndex);

		fs.replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , aragonIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, ALMUGHAVARS , aragonIndex , 1);
		fs.replaceInitialUnit(SPEAR_MILITIA, ALMUGHAVARS , aragonIndex);

		fs.replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		fs.replaceInitialUnit(ALFORRATS, null , aragonIndex);

		// Barcelona
		fs.replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex);
		fs.replaceInitialUnit(SPEAR_MILITIA, CROSSBOW_MILITIA , aragonIndex);
		fs.replaceInitialUnit(PEASANT_ARCHERS, ALMUGHAVARS , aragonIndex);

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

	private void initFileEntities() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		descrMercenaries = getFileRegisterForUpdated(DescrMercenaries.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		factionsSection = descrStrat.Factions;
		battleModels = getFileRegisterForUpdated(BattleModels.class);
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
	}
	@Override
	public Set<UUID> getConflictingFeatures() {
		return new HashSet<>(Arrays.asList(CatholicIberiaUnitsRecruitmentIncreased.Id));
	}
	public CatholicIberiaReworked() {
		super("Catholic Iberia reworked");
		allSpainHiddenResList = Arrays.asList(HiddenResources.Aragon, HiddenResources.Spain, HiddenResources.Portugal, HiddenResources.Andalusia);

		addCategory("Global");
		addCategory("Units");

		addOverrideTask(new OverrideCopyTask("UnitInfos"));

		iberiaChristianFactions = FactionsDefs.toFactionInfos(Arrays.asList("aragon", "spain" , "portugal"));
		otherChristianAndSlaveFactions = FactionsDefs.christianFactionInfos();
		otherChristianAndSlaveFactions.removeAll(otherChristianAndSlaveFactions);
		otherChristianAndSlaveFactions.add(SLAVE);
	}

	private final List<FactionInfo> iberiaChristianFactions;
	private final List<FactionInfo> otherChristianAndSlaveFactions;
	private final List<String> allSpainHiddenResList;

	private ExportDescrBuilding edb;
	private ExportDescrUnitTyped edu;
	private DescrMercenaries descrMercenaries;
	private DescrStratSectioned descrStrat;
	private FactionsSection factionsSection;
	private BattleModels battleModels;
	private DescrRegions descrRegions;

	private final String nl = System.lineSeparator();
	private final String nullStr = null;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b59df073-1de3-4358-b2bf-a88d781343e1");
}
