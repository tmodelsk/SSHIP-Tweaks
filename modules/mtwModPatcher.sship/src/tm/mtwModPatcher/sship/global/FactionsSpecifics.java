package tm.mtwModPatcher.sship.global;

import lombok.val;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.managers.UnitsManager;

import java.util.UUID;

/**  */
public class FactionsSpecifics extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		Byzantium();

		RomanCatholicTradersBonuses();
		NorwegianDanesBoostReplenishRates();
		CumansBoostReplenishRates();
		HolyRomanEmpireLawMinus();
		turksRumBoost();

		// ### DeBuff Crusade States - minus LAW
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("       law_bonus bonus -2 requires factions { jerusalem, }");

		// France: Eldest Daughter of the Church - Religion Bonus
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("       religion_level bonus 1 requires factions { france, }");
	}

	protected void HolyRomanEmpireLawMinus() throws PatcherLibBaseEx {

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("		law_bonus bonus -3 requires factions { hre, }");
	}

	protected void NorwegianDanesBoostReplenishRates() throws PatcherLibBaseEx {
		UnitsManager unitsManager = new UnitsManager();
		val updatedLines = unitsManager.addToAllUnitsReplenishRates("norway, denmark,", 3.0 , -2.0 , null , exportDescrBuilding);
	}

	protected void CumansBoostReplenishRates() throws PatcherLibBaseEx {
		UnitsManager unitsManager = new UnitsManager();
		unitsManager.addToAllUnitsReplenishRates("cumans,", 3.0 , -2.0 , null , exportDescrBuilding);
	}

	protected void RomanCatholicTradersBonuses() throws PatcherLibBaseEx {
		String requires = " requires factions { aragon,  portugal, pisa, venice, england, jerusalem, }";
		String attributeStr;

		// # MARKETS
		attributeStr = "       trade_base_income_bonus bonus 1";
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "corn_exchange" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "market" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "fairground" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "great_market" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "merchants_quarter" , "city", attributeStr + requires );
		// corn_exchange market fairground
		exportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "corn_exchange" , "castle", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "market" , "castle", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "fairground" , "castle", attributeStr + requires );

		// # PORTS: port shipwright dockyard naval_drydock
		exportDescrBuilding.insertIntoBuildingCapabilities("port", "port" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("port", "shipwright" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("port", "dockyard" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("port", "naval_drydock" , "city", attributeStr + requires );
		// c_port c_shipwright c_dockyard c_naval_drydock
		exportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_port" , "castle", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_dockyard" , "castle", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_naval_drydock" , "castle", attributeStr + requires );

		// # SEA TRADES : merchants_wharf warehouse docklands
		exportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "merchants_wharf" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "warehouse" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "docklands" , "city", attributeStr + requires );

		exportDescrBuilding.insertIntoBuildingCapabilities("castle_sea_trade", "merchants_wharf" , "castle", attributeStr + requires );
	}

	protected void Byzantium() throws PatcherLibBaseEx {

		ByzantiumMilitaryBuildingsPenalties();

		// ##### Additional PRODUCTIVITY MINUS BIZANTIUM ONLY - on trade goods low based level #####
		ByzantiumTradeMinus();

		// ##### POPULATION GROWTH MINUS - BIZANTIUM ONLY !! #####
		ByzantiumPopulationMinus(7,6 , " and event_counter is_the_player 1");		// Player version
		ByzantiumPopulationMinus(9,8 , " and not event_counter is_the_player 1");	// AI version

		val unitsManager = new UnitsManager();
		val updatedLines = unitsManager.addToAllUnitsReplenishRates(FactionsDefs.byzantiumCsv(), 3.0 , 2.0 , null , exportDescrBuilding);
	}

	protected void ByzantiumMilitaryBuildingsPenalties() throws PatcherLibBaseEx {

		String requirements = "factions { " + FactionsDefs.byzantiumCsv() + " } ";

		// ### City Barracks ### : levels town_watch town_guard city_watch militia_drill_square militia_barracks army_barracks royal_armoury
		exportDescrBuilding.addPopulationGrowthBonus("barracks", "town_watch", "city", -1 , requirements);				// 0
		exportDescrBuilding.addPopulationGrowthBonus("barracks", "town_guard", "city", -1 , requirements);				// -1
		exportDescrBuilding.addPopulationGrowthBonus("barracks", "city_watch", "city", -1 , requirements);				// -2
		exportDescrBuilding.addPopulationGrowthBonus("barracks", "militia_drill_square", "city", -1 , requirements);	// -2
		exportDescrBuilding.addPopulationGrowthBonus("barracks", "militia_barracks", "city", -1 , requirements);		// -3
		exportDescrBuilding.addPopulationGrowthBonus("barracks", "army_barracks", "city", -1 , requirements);			// -5
		exportDescrBuilding.addPopulationGrowthBonus("barracks", "royal_armoury", "city", -1 , requirements);			// -4

		// ### Castle ### : levels mustering_hall garrison_quarters drill_square barracks armoury
		// # Barracks #
		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "mustering_hall", "castle", -1 , requirements);		// 0
		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "garrison_quarters", "castle", -1 , requirements);		// -1
		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "drill_square", "castle", -1 , requirements);			// -2
		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "barracks", "castle", -1 , requirements);				// -3
		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "armoury", "castle", -1 , requirements);				// -3

		// # Archers # : levels bowyer practice_range archery_range marksmans_range
		exportDescrBuilding.addPopulationGrowthBonus("missiles", "bowyer", "castle", -1 , requirements);				// 0
		exportDescrBuilding.addPopulationGrowthBonus("missiles", "practice_range", "castle", -1 , requirements);		// 0
		exportDescrBuilding.addPopulationGrowthBonus("missiles", "archery_range", "castle", -1 , requirements);		// -1
		exportDescrBuilding.addPopulationGrowthBonus("missiles", "marksmans_range", "castle", -1 , requirements);		// -2

		// # Stables # : levels stables knights_stables barons_stables earls_stables kings_stables
		exportDescrBuilding.addPopulationGrowthBonus("equestrian", "stables", "castle", -1 , requirements);			// 0
		exportDescrBuilding.addPopulationGrowthBonus("equestrian", "knights_stables", "castle", -1 , requirements);	// 0
		exportDescrBuilding.addPopulationGrowthBonus("equestrian", "barons_stables", "castle", -1 , requirements);		// -1
		exportDescrBuilding.addPopulationGrowthBonus("equestrian", "earls_stables", "castle", -1 , requirements);		// -2
		exportDescrBuilding.addPopulationGrowthBonus("equestrian", "kings_stables", "castle", -1 , requirements);		// -3
	}

	protected void ByzantiumTradeMinus() throws PatcherLibBaseEx {
		// ##### Additional PRODUCTIVITY MINUS BIZANTIUM ONLY - on trade goods low based level #####
		String requiresByz = " requires factions { byzantium, }";
		// # WALLS - low based trade minus
		// # CITIES ### :
		String attributeStr = "       trade_base_income_bonus bonus -1";
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + requiresByz);
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + requiresByz );
		// # Castles #
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr  + requiresByz);
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + requiresByz );


		// ###### TRADE BUIDLINGS MINUS - Bizantium ONLY #####
		// # MARKETS
		// corn_exchange market fairground great_market merchants_quarter
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "corn_exchange" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "market" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "fairground" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "great_market" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("market", "merchants_quarter" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		// corn_exchange market fairground
		exportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "market" , "castle", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("market_castle", "fairground" , "castle", "       trade_base_income_bonus bonus -1" + requiresByz );

		// # PORTS
		// port shipwright dockyard naval_drydock
		exportDescrBuilding.insertIntoBuildingCapabilities("port", "shipwright" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("port", "dockyard" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("port", "naval_drydock" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		// c_port c_shipwright c_dockyard c_naval_drydock
		exportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_dockyard" , "castle", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("castle_port", "c_naval_drydock" , "castle", "       trade_base_income_bonus bonus -1" + requiresByz );

		// # SEA TRADES
		exportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "warehouse" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		exportDescrBuilding.insertIntoBuildingCapabilities("sea_trade", "docklands" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
	}

	protected void ByzantiumPopulationMinus(int bonusCity, int bonusCastle, String requiresSuffix) throws PatcherLibBaseEx {
		// ##### POPULATION GROWTH MINUS - BIZANTIUM ONLY !! #####
		// # WALLS - low based trade minus
		// # CITIES ### :
		String attributeStr = "       population_growth_bonus bonus -";
		String requiresByz = " requires factions { byzantium, }" + requiresSuffix;

		int bonus = bonusCity;
		//exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + bonus + requiresByz);
		bonus++;
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + bonus + requiresByz );
		bonus++;
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + bonus + requiresByz );
		bonus++;
		exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + bonus + requiresByz );

		// # Castles #
		bonus = bonusCastle;
		//exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requires );
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr + bonus  + requiresByz);
		bonus++;
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + bonus + requiresByz );
		bonus++;
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + bonus + requiresByz );
		bonus++;
		exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + bonus + requiresByz );
	}

	protected void turksRumBoost() {
		UnitsManager unitsManager = new UnitsManager();
		val updatedLines = unitsManager.addToAllUnitsReplenishRates("rum,", 3.0 , -2.0 , null , exportDescrBuilding);
	}

	protected ExportDescrBuilding exportDescrBuilding;
	protected ExportDescrUnitTyped exportDescrUnit;


	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public FactionsSpecifics(boolean isEnabled) {
		super("Factions specifics Pros & Cons");
		setEnabled(isEnabled);

		addCategory("Campaign");
		addCategory("Economy");

		setDescriptionShort("Unique Pros & Cons for few factions");
		setDescriptionUrl("http://tmsship.wikidot.com/factions-specifics");
	}
}
