package tm.mtwModPatcher.sship.features.Obsolete;

import org.xml.sax.SAXException;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.DescrSettlementMechanics;
import tm.mtwModPatcher.lib.managers.UnitsManager;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.sship.features.global.FactionsSpecifics;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Not used for a long time.
 */
@Deprecated
public class Civilizations extends Feature {

	protected double WorldMultiplier = 1;

	@Override
	public void executeUpdates() throws Exception {
		_ExportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		_ExportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		_DescrSettlementMechanics = getFileRegisterForUpdated(DescrSettlementMechanics.class);

		FarmsDifferences();

		// ### ORDER MODIFICATIONS - moved to SettlementUnrestLowered ###

		// ## Boost Taxes income ##
		_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TAXES']/pip_modifier", "value", 1.0 * WorldMultiplier);

		// ## Boost Farming income ##
		_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_FARMS']/pip_modifier", "value", 1.0 * WorldMultiplier);

		// ## Boost MINING income ##
		_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_MINING']/pip_modifier", "value", 0.75 * WorldMultiplier);

		// ## Boost TRADE income - to equalize removed mwechants ##
		_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TRADE']/pip_modifier", "value", 1.0 * WorldMultiplier);

		// ## Boost MINING income ##
		//_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_MINING']/pip_modifier", "value", 2);

		_ExportDescrUnit.setAttributeForAllUnits("free_upkeep_unit");

		Orthodox();

		Islam();

		RomanCatholic();

		Turanian();

	}

	public void FarmsDifferences() throws PatcherLibBaseEx {

		// ### Remove existing entries ###

		// levels farms farms+1 farms+2 farms+3 farms+4
		_ExportDescrBuilding.removeBuildingCapabilitiesByRegex("farms", "farms" , null , "^\\s*farming_level\\s");
		_ExportDescrBuilding.removeBuildingCapabilitiesByRegex("farms", "farms\\+1" , null , "^\\s*farming_level\\s");
		_ExportDescrBuilding.removeBuildingCapabilitiesByRegex("farms", "farms\\+2" , null , "^\\s*farming_level\\s");
		_ExportDescrBuilding.removeBuildingCapabilitiesByRegex("farms", "farms\\+3" , null , "^\\s*farming_level\\s");
		_ExportDescrBuilding.removeBuildingCapabilitiesByRegex("farms", "farms\\+4" , "city" , "^\\s*farming_level\\s");

		// ### Create new ###
		String catholicFactions = "factions { " + FactionsDefs.christianFactionsCsv()+ " }";
		String islamFactions = "factions { " + FactionsDefs.islamFactionsCsv() + " }";
		String orthodoxFactions = "factions { " + FactionsDefs.ortodoxFactionsCsv() + " }";
		// # 0
		// farming_level 1 requires building_present_min_level core_building wooden_pallisade
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms" , "        farming_level 1");

		// # 1
		//farming_level 2 requires building_present_min_level core_building wooden_pallisade
		//farming_level 1 requires building_present_min_level core_castle_building wooden_castle

		// Catholic +1
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+1" , "        farming_level 3 requires building_present_min_level core_building wooden_pallisade and "+catholicFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+1" , "        farming_level 2 requires building_present_min_level core_castle_building wooden_castle and "+catholicFactions);

		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+2" , "        farming_level 2 requires building_present_min_level core_building wooden_pallisade and "+orthodoxFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+1" , "        farming_level 2 requires building_present_min_level core_castle_building wooden_castle and "+orthodoxFactions);

		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+1" , "        farming_level 1 requires "+islamFactions);

		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+1" , "        farming_level 1 requires building_present_min_level core_building wooden_pallisade and factions { mongols, "+FactionsDefs.turanianFactionsCsv()+" }");


		// # 2
		//farming_level 3 requires factions { northern_european, moors, egypt, kwarezm, turks, rum, milan, eastern_european, greek, southern_european, }
		//farming_level 2 requires factions { mongols, }
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+2" , "        farming_level 4 requires "+catholicFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+2" , "        farming_level 3 requires "+orthodoxFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+2" , "        farming_level 2 requires "+islamFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+2" , "        farming_level 2 requires factions { mongols, }");

		// # 3
		//farming_level 8 requires factions { northern_european, moors, egypt, kwarezm, turks, rum, milan, eastern_european, greek, southern_european, }
		//farming_level 4 requires factions { mongols, }
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+3" , "        farming_level 9 requires "+catholicFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+3" , "        farming_level 8 requires "+orthodoxFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+3" , "        farming_level 7 requires "+islamFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+3" , "        farming_level 4 requires factions { mongols, }");

		// # 4
		//farming_level 12 requires factions { northern_european, moors, egypt, kwarezm, turks, rum, milan, eastern_european, greek, southern_european, }
		//farming_level 6 requires factions { mongols, }
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+4" , "city" , "        farming_level 13 requires "+catholicFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+4" , "city" , "        farming_level 12 requires "+orthodoxFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+4" , "city" , "        farming_level 10 requires "+islamFactions);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("farms" , "farms\\+4" , "city" , "        farming_level 6 requires factions { mongols, }");
	}

	protected void RomanCatholic() throws PatcherLibBaseEx {
		// #### Feudal System ####
		String catholics = FactionsDefs.christianFactionsCsv();

		_ExportDescrBuilding.addFlatCityCastleIncome(" requires factions { "+ catholics +" }" , 0.25);

		minesBonuses(catholics , +3 );
		RomanCatholicConstructionBonuses();
		RomanCatholicTradeBaseBonuses( +1 );
		RomanCatholicReligiousConversionTempleBonus();
	}

	protected void RomanCatholicConstructionBonuses() throws PatcherLibBaseEx {
		String requires = " requires factions { "+ FactionsDefs.christianFactionsCsv()+" }";

		// #### CONSTRUCTION COSTS ######
		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_cost_bonus_other bonus 5"+requires);
		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_cost_bonus_religious bonus 5"+requires);
		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_cost_bonus_defensive bonus 5"+requires);

		// #### CONSTRUCTION TIMES ######
		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_time_bonus_other bonus 21"+requires);
		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_time_bonus_religious bonus 21"+requires);
		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_time_bonus_defensive bonus 21"+requires);

	}

	protected void RomanCatholicTradeBaseBonuses(int bonus) throws PatcherLibBaseEx {
		// ##### PRODUCTIVITY BONUS - on trade goods low based level #####

		String requires = " requires factions { "+ FactionsDefs.christianFactionsCsv()+" }";

		// # CITIES ### :
		String attributeStr = "       trade_base_income_bonus bonus "+bonus;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + requires );
		// # Castles #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr  + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + requires );
	}

	// Copied to CatholicFactionsBoost
	protected void RomanCatholicReligiousConversionTempleBonus() throws PatcherLibBaseEx {

		// ### Religion Conversion bonus Catholic Chutches  ###

		String attribStr = "        religion_level bonus ";

		// # City # : temple_catholic : levels small_church church abbey cathedral huge_cathedral
		//exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "small_church", "city", attribStr + 0);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "church", "city", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "abbey", "city", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "cathedral", "city", attribStr + 2);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "huge_cathedral", "city", attribStr + 2);

		// # Castle # : temple_catholic_castle : levels small_chapel chapel
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic_castle", "small_chapel", "castle", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic_castle", "chapel", "castle", attribStr + 2);
	}

	protected void Orthodox() throws PatcherLibBaseEx {
		String requires;
		String ortodoxes = FactionsDefs.ortodoxFactionsCsv();

		minesBonuses(ortodoxes , +2 );

		requires = " requires factions { "+ ortodoxes +" }";

		// #### Feudal System ####
		_ExportDescrBuilding.addFlatCityCastleIncome(requires , 0.2);

		// ### L A W  BONUS to simulate bureucracy
		OrthodoxLawBureocracyBonus();

		// ##### PRODUCTIVITY MINUS - on trade goods low based level #####
		//OrthodoxTradeBaseBonuses(-1);

		// ##### Population Growth Flat minus
		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("        population_growth_bonus bonus -1"+requires);

	}

	protected void OrthodoxLawBureocracyBonus() throws PatcherLibBaseEx {
		// ### L A W  BONUS to simulate bureucracy
		String requires = " requires factions { "+ FactionsDefs.ortodoxFactionsCsv()+" }";
		int bonus = 1;
		String attributeStr = "       law_bonus bonus ";
		// # Walls #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + bonus + requires );
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + bonus + requires );
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + bonus + requires );
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + bonus + requires );

		// ### CASTLES ###
		// # Walls # - building core_castle_building  - levels motte_and_bailey wooden_castle castle fortress citadel
		bonus= 1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + bonus + requires );
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr + bonus + requires );
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + bonus + requires );
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + bonus + requires );
	}

	protected void OrthodoxTradeBaseBonuses(int bonus) throws PatcherLibBaseEx {
		// ##### PRODUCTIVITY MINUS - on trade goods low based level #####
		String requires = " requires factions { "+ FactionsDefs.ortodoxFactionsCsv()+" }";
		// # CITIES ### :
		String attributeStr = "       trade_base_income_bonus bonus " + bonus;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + requires );
		// # Castles #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr  + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + requires );
	}

	protected void Islam() throws PatcherLibBaseEx, IOException, SAXException, InstantiationException, ParserConfigurationException, IllegalAccessException {
		String requires, attributeStr;
		int bonus;

		String muslims = FactionsDefs.islamFactionsCsv();

		requires = " requires factions { "+ muslims +" }";

		// #### Feudal System ####
		_ExportDescrBuilding.addFlatCityCastleIncome(requires , 0.15);

		// ### LOWER TRADE MINUS ###
		IslamTradeBaseBonus(-1);
		//IslamBuildingsTradeMinuses();
		IslamHomelandBaseTradeBoost();
		minesBonuses(muslims, +1);


		List<Pattern> unitsToExclude = Arrays.asList(  Pattern.compile(".*[Cc]hristian.*"));

		islamUnitReplenishRatesBoost(unitsToExclude);
		islamUnitsUpkeepRecruitCostsBoost( 0.80 , 0.75 , unitsToExclude);
		//islamUnitsArmourLowered(3 , 99 , -1 , unitsToExclude);

		islamMilitaryBuildingsNoPenalties();

		// ### FREE UPKEEP on Walls ###
		IslamFreeUpkeep();

		// ### HAPPINESS / LAW BONUSES  ###
		IslamFundamentalismHappinessBonus();

		// ### RECRUITMENT SLOTS bonus Muslims Mosques  ###
		IslamRecruitmentSlots();

		IslamReligiousConversionTempleBonus();
	}

	protected void IslamHomelandBaseTradeBoost() throws PatcherLibBaseEx {
		String requirements = " requires factions { egypt, milan, kwarezm, } and hidden_resource egypt or hidden_resource milan or hidden_resource kwarezm or hidden_resource crusader";

		_ExportDescrBuilding.insertIntoCityCastleWallsCapabilities("       trade_base_income_bonus bonus 1 " + requirements);
	}

	protected void IslamFundamentalismHappinessBonus() throws PatcherLibBaseEx {
		// ### HAPPINESS / LAW BONUSES  ###
		String requires = " requires factions { "+ FactionsDefs.islamFactionsCsv()+" }";
		// ### CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wal  - Village - no bonus - no building
		int bonus = 1;
		int happinessBonus = 1;
		String happinessAtr = "       happiness_bonus bonus ";
		String lawAtr = "       law_bonus bonus -";
		int lawShift=0, happinesShift=2;
		// # Walls #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", lawAtr + (bonus + lawShift) + requires );
		happinessBonus++;
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", lawAtr + (bonus + lawShift) + requires );
		happinessBonus++;
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", lawAtr + (bonus + lawShift) + requires );
		happinessBonus++;
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", lawAtr + (bonus + lawShift) + requires );
		happinessBonus++;
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", lawAtr + (bonus + lawShift) + requires );
		happinessBonus++;
		bonus++;

		// ### CASTLES ###
		// # Walls # - building core_castle_building  - levels motte_and_bailey wooden_castle castle fortress citadel
		bonus=1;
		happinessBonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "motte_and_bailey" , "castle", lawAtr + (bonus + lawShift) + requires );
		happinessBonus++;
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_castle" , "castle", lawAtr + (bonus + lawShift) + requires );
		happinessBonus++;
		//bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "castle" , "castle", lawAtr + (bonus + lawShift) + requires );
		//bonus++;
		happinessBonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "fortress" , "castle", lawAtr + (bonus + lawShift) + requires );
		bonus++;
		happinessBonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", happinessAtr + happinessBonus +  requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "citadel" , "castle", lawAtr + (bonus + lawShift) + requires );
		bonus++;
		happinessBonus++;
	}

	protected void IslamTradeBaseBonus(int bonus) throws PatcherLibBaseEx {
		// ### LOWER TRADE MINUS ###

		String requires = " requires factions { "+ FactionsDefs.islamFactionsCsv()+" }";
		String attributeStr;

		// # CITIES ### :
		attributeStr = "       trade_base_income_bonus bonus "+bonus;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + requires );
		// # Castles #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr  + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + requires );
	}

	protected void IslamBuildingsTradeMinuses() throws PatcherLibBaseEx {
		// ### LOWER TRADE MINUS ###

		String requires = " requires factions { "+ FactionsDefs.islamFactionsCsv()+" }";
		String attributeStr;

		// # MARKETS
		// corn_exchange market fairground great_market merchants_quarter
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "corn_exchange" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "market" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "fairground" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "great_market" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "merchants_quarter" , "city", "       trade_base_income_bonus bonus -1" + requires );
		// corn_exchange market fairground
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "market" , "castle", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "fairground" , "castle", "       trade_base_income_bonus bonus -1" + requires );

		// # PORTS
		// port shipwright dockyard naval_drydock
		_ExportDescrBuilding.insertIntoBuildingCapabilities("port", "shipwright" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("port", "dockyard" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("port", "naval_drydock" , "city", "       trade_base_income_bonus bonus -1" + requires );
		// c_port c_shipwright c_dockyard c_naval_drydock
		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_dockyard" , "castle", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_naval_drydock" , "castle", "       trade_base_income_bonus bonus -1" + requires );

		// # SEA TRADES
		_ExportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "warehouse" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "docklands" , "city", "       trade_base_income_bonus bonus -1" + requires );
	}
	
	protected void IslamFreeUpkeep() throws PatcherLibBaseEx {
		 // ### FREE UPKEEP on Walls ###

		 String requires = " requires factions { "+ FactionsDefs.islamFactionsCsv()+" }";

		 // # CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wall
		 //exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", "       free_upkeep bonus "+1 + requires );
		 //exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", "       free_upkeep bonus "+1 + requires);
		 _ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", "       free_upkeep bonus "+1 + requires );
		 _ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", "       free_upkeep bonus "+1 + requires );
		 _ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", "       free_upkeep bonus "+1 + requires );
		 // # Castles #
		 //exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", "       free_upkeep bonus "+1 + requires );
		 //exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", "       free_upkeep bonus "+1  + requires);
		 _ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", "       free_upkeep bonus "+1 + requires );
		 _ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", "       free_upkeep bonus "+1 + requires );
		 _ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", "       free_upkeep bonus "+1 + requires );
	}

	protected void IslamRecruitmentSlots() throws PatcherLibBaseEx {
		// ### RECRUITMENT SLOTS bonus Muslims Mosques  ###
		// # City #
		_ExportDescrBuilding.addRecruitemntSlotBonus("temple_muslim", "small_masjid", "city", 1);
		_ExportDescrBuilding.addRecruitemntSlotBonus("temple_muslim", "masjid", "city", 2);
		_ExportDescrBuilding.addRecruitemntSlotBonus("temple_muslim", "minareted_masjid", "city", 2);
		_ExportDescrBuilding.addRecruitemntSlotBonus("temple_muslim", "jama", "city", 3);
		_ExportDescrBuilding.addRecruitemntSlotBonus("temple_muslim", "great_jama", "city", 3);
		// # Castle #
		_ExportDescrBuilding.addRecruitemntSlotBonus("temple_muslim_castle", "c_small_masjid", "castle", 2);
		_ExportDescrBuilding.addRecruitemntSlotBonus("temple_muslim_castle", "c_masjid", "castle", 3);
	}

	// Copied into MusliFactionBoost
	protected void IslamReligiousConversionTempleBonus() throws PatcherLibBaseEx {

		// ### Religion Conversion bonus Muslims Mosques  ###

		String attribStr = "        religion_level bonus ";

		// # City #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "small_masjid", "city", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "masjid", "city", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "minareted_masjid", "city", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "jama", "city", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "great_jama", "city", attribStr + 1);
		// # Castle #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim_castle", "c_small_masjid", "castle", attribStr + 1);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim_castle", "c_masjid", "castle", attribStr + 1);
	}

	protected void islamMilitaryBuildingsNoPenalties() throws PatcherLibBaseEx {

		String requirements = "factions { " + FactionsDefs.islamFactionsCsv() + " } ";

		// ### City Barracks ###
		_ExportDescrBuilding.addPopulationGrowthBonus("barracks", "town_guard", "city", 1 , requirements);				// -1
		_ExportDescrBuilding.addPopulationGrowthBonus("barracks", "city_watch", "city", 2 , requirements);				// -2
		_ExportDescrBuilding.addPopulationGrowthBonus("barracks", "militia_drill_square", "city", 1 , requirements);	// -2
		_ExportDescrBuilding.addPopulationGrowthBonus("barracks", "militia_barracks", "city", 2 , requirements);		// -3
		_ExportDescrBuilding.addPopulationGrowthBonus("barracks", "army_barracks", "city", 3 , requirements);			// -5
		_ExportDescrBuilding.addPopulationGrowthBonus("barracks", "royal_armoury", "city", 2 , requirements);			// -4

		// ### Castle ###
		// # Barracks #
		_ExportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "garrison_quarters", "castle", 1 , requirements);		// -1
		_ExportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "drill_square", "castle", 2 , requirements);			// -2
		_ExportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "barracks", "castle", 2 , requirements);				// -3
		_ExportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "armoury", "castle", 1 , requirements);				// -3

		// # Archers #
		_ExportDescrBuilding.addPopulationGrowthBonus("missiles", "archery_range", "castle", 1 , requirements);		// -1
		_ExportDescrBuilding.addPopulationGrowthBonus("missiles", "marksmans_range", "castle", 1 , requirements);		// -2


		// # Stables # : levels stables knights_stables barons_stables earls_stables kings_stables
		_ExportDescrBuilding.addPopulationGrowthBonus("equestrian", "barons_stables", "castle", 1 , requirements);		// -1
		_ExportDescrBuilding.addPopulationGrowthBonus("equestrian", "earls_stables", "castle", 1 , requirements);		// -2
		_ExportDescrBuilding.addPopulationGrowthBonus("equestrian", "kings_stables", "castle", 1 , requirements);		// -3
	}

	protected void islamUnitsUpkeepRecruitCostsBoost( double costMulti, double upkeepMulti ,  List<Pattern> unitsToExclude) throws PatcherLibBaseEx, SAXException, InstantiationException, IllegalAccessException, ParserConfigurationException, IOException {

		String factionsFilterCsv = FactionsDefs.islamFactionsCsv() + FactionsDefs.slaveCsv();
		UnitsManager unitsManager = new UnitsManager();

		unitsManager.multiplyUpkeepRecruitCosts(factionsFilterCsv, costMulti , upkeepMulti , unitsToExclude , _ExportDescrUnit);
	}

	protected void islamUnitsArmourLowered(int armorMin, int armorMax, int armourModifier, List<Pattern> unitsToExclude) {
		String factionsFilterCsv = FactionsDefs.islamFactionsCsv() + FactionsDefs.slaveCsv();
		UnitsManager unitsManager = new UnitsManager();

		unitsManager.modifyArmorStats(factionsFilterCsv , null , armorMin , armorMax , armourModifier, unitsToExclude, _ExportDescrUnit);
	}

	protected void islamUnitReplenishRatesBoost(List<Pattern> unitsToExclude) {

		String factionsFilterCsv = FactionsDefs.islamFactionsCsv() + FactionsDefs.slaveCsv();
		UnitsManager unitsManager = new UnitsManager();

		unitsManager.updateAllUnitsReplenishRatesByTurnNumber(factionsFilterCsv, 2.0 , -1.0 , unitsToExclude , _ExportDescrBuilding);

		// ## Additional Upgrade replenish for Ahdath Militia
		unitsManager.updateReplenishRates("Ahdath Militia" , factionsFilterCsv , 2.0 , -1.0 , _ExportDescrBuilding);

		// ## Additional Upgrade replenish for :
		unitsManager.updateReplenishRates("Fari Lancers" , factionsFilterCsv , 2.0 , -1.0 , _ExportDescrBuilding);
		unitsManager.updateReplenishRates("Tawashi Light Cavalry" , factionsFilterCsv , 2.0 , -1.0 , _ExportDescrBuilding);
		unitsManager.updateReplenishRates("Arab Cavalry" , factionsFilterCsv , 2.0 , -1.0 , _ExportDescrBuilding);

//		LinesProcessor lines = exportDescrBuilding.getLines();
//
//		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
//		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+.+");
//
//		Pattern factionRegex = Pattern.compile("factions\\s+\\{(.+)\\}");
//
//		int index = 0;
//		while (index >= 0) {
//			index = lines.findFirstByRegexLine(regex, index+1);
//			if(index >= 0) {
//
//				String lineOrg = lines.getLine(index);
//
//				UnitRecuitmentInfo unitRecrInfo = exportDescrBuilding.parseUnitRecruitmentInfo(lineOrg);
//
//				// ### Check if needs to be excluded - ommitted ###
//				boolean isShouldBeExcluded = false;
//				for(Pattern excludeNameRegex : unitsToExclude) {
//					if(excludeNameRegex.matcher(unitRecrInfo.Name).find()) {
//						isShouldBeExcluded = true;
//						break;
//					}
//				}
//				if(isShouldBeExcluded) continue;
//
//				// ## Faction Check ##
//				// factions { england, scotland, france, hre, spain, aragon, portugal, teutonic_order, jerusalem, }
//
//				Matcher matcher = factionRegex.matcher(unitRecrInfo.RequirementStr);
//				if(matcher.find()) {
//
//					String factionsStr = matcher.group(1);
//					if(unitRecrInfo.MaxStack >= 1) { // only True entires
//						if (FactionsDefs.isAllIslam(factionsStr)) {
//							// ### OK - this add satisfies FACTION condition ###
//
//							boolean isUpdated = false;
//
//							if (unitRecrInfo.MaxStack >= 1) {
//								isUpdated = true;
//								unitRecrInfo.MaxStack++;
//							}
//
//							int rateOrg = (int) ((1.0001 / unitRecrInfo.ReplenishRate) + 1);
//							String lineUpdated = null;
//							if (rateOrg > 3) {
//								isUpdated = true;
//
//								unitRecrInfo.ReplenishRate = 1.0001 / (Double) (rateOrg - 1.0);
//								lineUpdated = unitRecrInfo.toRecruitmentPoolLine();
//							}
//
//							if(isUpdated)
//								lines.replaceLine(index, lineUpdated);
//						}
//					}
//				}
//			}
//		}
	}



	protected void Turanian() throws PatcherLibBaseEx {
		String requires, attributeStr;
		int bonus;

		requires = " requires factions { "+ FactionsDefs.turanianFactionsCsv()+" }";

		// #### Feudal System ####
		_ExportDescrBuilding.addFlatCityCastleIncome(requires , 0.1);

		// ### LOWER TRADE MINUS ###
		TuranianTradeMinuses();

		// ### FREE UPKEEP on Walls ###
		TuranianFreeUpkeep();

		// ### HAPPINESS / LAW BONUSES  ###
		TuraniaHappinesLasBonus();

		// ### RECRUITMENT SLOTS ###
		TuranianRecruitmentSlots();

		// ### Replenish Rates ###
		UnitsManager unitsManager = new UnitsManager();
		unitsManager.updateAllUnitsReplenishRatesByTurnNumber(FactionsDefs.turanianFactionsCsv(), 3.0 , -2.0 , null , _ExportDescrBuilding);
	}

	protected void TuraniaHappinesLasBonus() throws PatcherLibBaseEx {
		// ### HAPPINESS / LAW BONUSES  ###
		String requires = " requires factions { "+ FactionsDefs.turanianFactionsCsv()+" }";
		// ### CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wal  - Village - no bonus - no building
		int bonus = 3;
		String attributeStr = "       happiness_bonus bonus ";
		// # Walls #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + bonus + requires );
		bonus++;

		// ### CASTLES ###
		// # Walls # - building core_castle_building  - levels motte_and_bailey wooden_castle castle fortress citadel
		bonus=3;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + bonus + requires );
		bonus++;

		// #### LAW BONUS - to deal with corruption
		bonus = 1;
		attributeStr = "       law_bonus bonus ";
		// # Walls #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + bonus + requires );
		bonus++;

		// ### CASTLES ###
		// # Walls # - building core_castle_building  - levels motte_and_bailey wooden_castle castle fortress citadel
		bonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + bonus + requires );
		bonus++;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + bonus + requires );
		bonus++;

	}

	protected void TuranianTradeMinuses() throws PatcherLibBaseEx {
		// ### LOWER TRADE MINUS ###

		String requires = " requires factions { "+ FactionsDefs.turanianFactionsCsv()+" }";
		String attributeStr;

		// # MARKETS
		// corn_exchange market fairground great_market merchants_quarter
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "corn_exchange" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "market" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "fairground" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "great_market" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market", "merchants_quarter" , "city", "       trade_base_income_bonus bonus -1" + requires );
		// corn_exchange market fairground
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "market" , "castle", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "fairground" , "castle", "       trade_base_income_bonus bonus -1" + requires );

		// # PORTS
		// port shipwright dockyard naval_drydock
		_ExportDescrBuilding.insertIntoBuildingCapabilities("port", "shipwright" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("port", "dockyard" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("port", "naval_drydock" , "city", "       trade_base_income_bonus bonus -1" + requires );
		// c_port c_shipwright c_dockyard c_naval_drydock
		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_dockyard" , "castle", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_naval_drydock" , "castle", "       trade_base_income_bonus bonus -1" + requires );

		// # SEA TRADES
		_ExportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "warehouse" , "city", "       trade_base_income_bonus bonus -1" + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "docklands" , "city", "       trade_base_income_bonus bonus -1" + requires );

		// # WALLS - low based trade minus
		// # CITIES ### :
		attributeStr = "       trade_base_income_bonus bonus -3";
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + requires );
		// # Castles #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr  + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + requires );
	}

	protected void TuranianFreeUpkeep() throws PatcherLibBaseEx {
		// ### FREE UPKEEP on Walls ###

		String requires = " requires factions { "+ FactionsDefs.turanianFactionsCsv()+" }";

		// # CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wall
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", "       free_upkeep bonus "+2 + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", "       free_upkeep bonus "+2 + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", "       free_upkeep bonus "+2 + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", "       free_upkeep bonus "+3 + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", "       free_upkeep bonus "+4 + requires );
		// # Castles #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", "       free_upkeep bonus "+2 + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", "       free_upkeep bonus "+2  + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", "       free_upkeep bonus "+2 + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", "       free_upkeep bonus "+3 + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", "       free_upkeep bonus "+4 + requires );
	}

	protected void TuranianRecruitmentSlots() throws PatcherLibBaseEx {
		// ### RECRUITMENT SLOTS bonus ###
		String requires = " factions { "+ FactionsDefs.turanianFactionsCsv()+" }";
		String attrStr;

		attrStr = "\t\t\trecruitment_slots bonus 3 requires not event_counter freeze_recr_pool 1 and "+requires+" ; Patcher Added";

		// # CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wall
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attrStr );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attrStr);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attrStr );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attrStr );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attrStr );
		// # Castles #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attrStr+2 + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attrStr+2  + requires);
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attrStr );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attrStr );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attrStr );
	}

	protected void minesBonuses(String factionsCsv, int bonus) throws PatcherLibBaseEx {
		String requires = " requires factions { "+ factionsCsv+" }";
		String attributeStr;

		attributeStr="mine_resource bonus "+bonus;

		// levels mines mines+1
		_ExportDescrBuilding.insertIntoBuildingCapabilities("mines", "mines" , "city", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("mines", "mines\\+1" , "city", attributeStr + requires );

		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_mines", "c_mines" , "castle", attributeStr + requires );
		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_mines", "c_mines\\+1" , "castle", attributeStr + requires );
	}

	protected ExportDescrBuilding _ExportDescrBuilding;
	protected ExportDescrUnitTyped _ExportDescrUnit;
	protected DescrSettlementMechanics _DescrSettlementMechanics;

	protected FactionsSpecifics _FactionsSpecifics;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public Civilizations( boolean isEnabled , FactionsSpecifics factionsSpecifics) {
		super("Civilizations: Latin-Catholic, Byzantie, Islam, Turman");

		setEnabled(isEnabled);
		_FactionsSpecifics = factionsSpecifics;
	}
}
