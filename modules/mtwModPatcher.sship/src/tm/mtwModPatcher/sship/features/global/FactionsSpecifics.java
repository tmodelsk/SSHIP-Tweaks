package tm.mtwModPatcher.sship.features.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.managers.UnitsManager;

import java.util.Arrays;
import java.util.UUID;

import static tm.mtwModPatcher.lib.managers.FactionsDefs.CUMANS;
import static tm.mtwModPatcher.lib.managers.FactionsDefs.NOVOGROD;

/**  */
public class FactionsSpecifics extends Feature {

	@Override
	public void setParamsCustomValues() {
		byzantiumPopulationPenaltyPlayer = 11;    // 7
		byzantiumPopulationPenaltyAi = 13;        // 9
		byzantiumPopulationPenaltyMilitaryBuildings = 2;
		byzantiumReplenishMult = 0.5;
		seljuksRumReplenishMult = 1.5;
	}

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		byzantium();

		//romanCatholicTradersBonuses();
		norwegianDanesBoostReplenishRates();
		cumansBoostReplenishRates();
		lithuaniaBoostReplenishRates();
		holyRomanEmpireLawMinus();
		turksRumBoost();

		// ### DeBuff Crusade States - minus LAW
		edb.addToCityCastleWallsCapabilities("       law_bonus bonus -2 requires factions { jerusalem, }");

		// France: Eldest Daughter of the Church - Religion Bonus
		edb.addToCityCastleWallsCapabilities("       religion_level bonus 1 requires factions { france, }");

		// Moors: -1 Law
		edb.addToCityCastleWallsCapabilities("		law_bonus bonus -1 requires factions { moors, }");
	}

	protected void holyRomanEmpireLawMinus() throws PatcherLibBaseEx {

		edb.addToCityCastleWallsCapabilities("		law_bonus bonus -3 requires factions { hre, }");
	}

	protected void norwegianDanesBoostReplenishRates() throws PatcherLibBaseEx {
		UnitsManager unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry(Arrays.asList("norway", "denmark"), null, 1.5, null, edb);
	}

	protected void cumansBoostReplenishRates() throws PatcherLibBaseEx {
		val unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry(CUMANS.symbol, null, 2.0, null, edb);
		unitsManager.multiplyUpkeepRecruitCosts(CUMANS.symbol, 1.0, 0.8, null, exportDescrUnit);
	}

	protected void lithuaniaBoostReplenishRates() {
		val unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry(NOVOGROD.symbol, null, 2.0, null, edb);
	}

	protected void romanCatholicTradersBonuses() throws PatcherLibBaseEx {
		String requires = " requires factions { aragon,  portugal, pisa, venice, england, jerusalem, }";
		String attributeStr;

		// # MARKETS
		attributeStr = "       trade_base_income_bonus bonus 1";
		edb.addCapabilities("market", "corn_exchange", "city", attributeStr + requires);
		edb.addCapabilities("market", "market", "city", attributeStr + requires);
		edb.addCapabilities("market", "fairground", "city", attributeStr + requires);
		edb.addCapabilities("market", "great_market", "city", attributeStr + requires);
		edb.addCapabilities("market", "merchants_quarter", "city", attributeStr + requires);
		// corn_exchange market fairground
		edb.addCapabilities("market_castle", "corn_exchange", "castle", attributeStr + requires);
		edb.addCapabilities("market_castle", "market", "castle", attributeStr + requires);
		edb.addCapabilities("market_castle", "fairground", "castle", attributeStr + requires);

		// # PORTS: port shipwright dockyard naval_drydock
		edb.addCapabilities("port", "port", "city", attributeStr + requires);
		edb.addCapabilities("port", "shipwright", "city", attributeStr + requires);
		edb.addCapabilities("port", "dockyard", "city", attributeStr + requires);
		edb.addCapabilities("port", "naval_drydock", "city", attributeStr + requires);
		// c_port c_shipwright c_dockyard c_naval_drydock
		edb.addCapabilities("castle_port", "c_port", "castle", attributeStr + requires);
		edb.addCapabilities("castle_port", "c_dockyard", "castle", attributeStr + requires);
		edb.addCapabilities("castle_port", "c_naval_drydock", "castle", attributeStr + requires);

		// # SEA TRADES : merchants_wharf warehouse docklands
		edb.addCapabilities("sea_trade", "merchants_wharf", "city", attributeStr + requires);
		edb.addCapabilities("sea_trade", "warehouse", "city", attributeStr + requires);
		edb.addCapabilities("sea_trade", "docklands", "city", attributeStr + requires);

		edb.addCapabilities("castle_sea_trade", "merchants_wharf", "castle", attributeStr + requires);
	}

	protected void byzantium() throws PatcherLibBaseEx {

		byzantiumMilitaryBuildingsPenalties();

		// ##### Additional PRODUCTIVITY MINUS BIZANTIUM ONLY - on trade goods low based level #####
		byzantiumTradeMinus();

		// ##### POPULATION GROWTH MINUS - BIZANTIUM ONLY !! #####
		if (byzantiumPopulationPenaltyPlayer > 0)
			ByzantiumPopulationMinus(byzantiumPopulationPenaltyPlayer, byzantiumPopulationPenaltyPlayer - 1, " and event_counter is_the_player 1");        // Player version
		if (byzantiumPopulationPenaltyAi > 0)
			ByzantiumPopulationMinus(byzantiumPopulationPenaltyAi, byzantiumPopulationPenaltyAi - 1, " and not event_counter is_the_player 1");    // AI version

		// Longer units replenishes
		val unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry("byzantium", null, byzantiumReplenishMult, null, edb);
	}

	protected void byzantiumMilitaryBuildingsPenalties() throws PatcherLibBaseEx {
		String requirements = "factions { " + FactionsDefs.byzantiumCsv() + " } ";
		int bonus = -byzantiumPopulationPenaltyMilitaryBuildings;

		if (bonus != 0) {
			// ### City Barracks ### : levels town_watch town_guard city_watch militia_drill_square militia_barracks army_barracks royal_armoury
			edb.addPopulationGrowthBonus("barracks", "town_watch", "city", bonus, requirements);                // 0
			edb.addPopulationGrowthBonus("barracks", "town_guard", "city", bonus, requirements);                // -1
			edb.addPopulationGrowthBonus("barracks", "city_watch", "city", bonus, requirements);                // -2
			edb.addPopulationGrowthBonus("barracks", "militia_drill_square", "city", bonus, requirements);    // -2
			edb.addPopulationGrowthBonus("barracks", "militia_barracks", "city", bonus, requirements);        // -3
			edb.addPopulationGrowthBonus("barracks", "army_barracks", "city", bonus, requirements);            // -5
			edb.addPopulationGrowthBonus("barracks", "royal_armoury", "city", bonus, requirements);            // -4

			// ### Castle ### : levels mustering_hall garrison_quarters drill_square barracks armoury
			// # Barracks #
			edb.addPopulationGrowthBonus("castle_barracks", "mustering_hall", "castle", bonus, requirements);        // 0
			edb.addPopulationGrowthBonus("castle_barracks", "garrison_quarters", "castle", bonus, requirements);        // -1
			edb.addPopulationGrowthBonus("castle_barracks", "drill_square", "castle", bonus, requirements);            // -2
			edb.addPopulationGrowthBonus("castle_barracks", "barracks", "castle", bonus, requirements);                // -3
			edb.addPopulationGrowthBonus("castle_barracks", "armoury", "castle", bonus, requirements);                // -3

			// # Archers # : levels bowyer practice_range archery_range marksmans_range
			edb.addPopulationGrowthBonus("missiles", "bowyer", "castle", bonus, requirements);                // 0
			edb.addPopulationGrowthBonus("missiles", "practice_range", "castle", bonus, requirements);        // 0
			edb.addPopulationGrowthBonus("missiles", "archery_range", "castle", bonus, requirements);        // -1
			edb.addPopulationGrowthBonus("missiles", "marksmans_range", "castle", bonus, requirements);        // -2

			// # Stables # : levels stables knights_stables barons_stables earls_stables kings_stables
			edb.addPopulationGrowthBonus("equestrian", "stables", "castle", bonus, requirements);            // 0
			edb.addPopulationGrowthBonus("equestrian", "knights_stables", "castle", bonus, requirements);    // 0
			edb.addPopulationGrowthBonus("equestrian", "barons_stables", "castle", bonus, requirements);        // -1
			edb.addPopulationGrowthBonus("equestrian", "earls_stables", "castle", bonus, requirements);        // -2
			edb.addPopulationGrowthBonus("equestrian", "kings_stables", "castle", bonus, requirements);        // -3
		}
	}

	protected void byzantiumTradeMinus() throws PatcherLibBaseEx {
		// ##### Additional PRODUCTIVITY MINUS BIZANTIUM ONLY - on trade goods low based level #####
		String requiresByz = " requires factions { byzantium, }", newLine;
		// # WALLS - low based trade minus
		// # CITIES ### :
		String attributeStr = "       trade_base_income_bonus bonus -2";    // -1
		newLine = attributeStr + requiresByz;
		edb.addCapabilities("core_building", "wooden_pallisade", "city", newLine);
		edb.addCapabilities("core_building", "wooden_wall", "city", newLine);
		edb.addCapabilities("core_building", "stone_wall", "city", newLine);
		edb.addCapabilities("core_building", "large_stone_wall", "city", newLine);
		edb.addCapabilities("core_building", "huge_stone_wall", "city", newLine);
		// # Castles #
		edb.addCapabilities("core_castle_building", "motte_and_bailey", "castle", newLine);
		edb.addCapabilities("core_castle_building", "wooden_castle", "castle", newLine);
		edb.addCapabilities("core_castle_building", "castle", "castle", newLine);
		edb.addCapabilities("core_castle_building", "fortress", "castle", newLine);
		edb.addCapabilities("core_castle_building", "citadel", "castle", newLine);


		// ###### TRADE BUIDLINGS MINUS - Bizantium ONLY #####
		// # MARKETS
		// corn_exchange market fairground great_market merchants_quarter
		attributeStr = "       trade_base_income_bonus bonus -2";
		newLine = attributeStr + requiresByz;
		edb.addCapabilities("market", "corn_exchange", "city", newLine);
		edb.addCapabilities("market", "market", "city", newLine);
		edb.addCapabilities("market", "fairground", "city", newLine);
		edb.addCapabilities("market", "great_market", "city", newLine);
		edb.addCapabilities("market", "merchants_quarter", "city", newLine);
		// corn_exchange market fairground
		edb.addCapabilities("market_castle", "market", "castle", newLine);
		edb.addCapabilities("market_castle", "fairground", "castle", newLine);

		// # PORTS
		// port shipwright dockyard naval_drydock
		edb.addCapabilities("port", "shipwright", "city", newLine);
		edb.addCapabilities("port", "dockyard", "city", newLine);
		edb.addCapabilities("port", "naval_drydock", "city", newLine);
		// c_port c_shipwright c_dockyard c_naval_drydock
		edb.addCapabilities("castle_port", "c_dockyard", "castle", newLine);
		edb.addCapabilities("castle_port", "c_naval_drydock", "castle", newLine);

		// # SEA TRADES
		edb.addCapabilities("sea_trade", "warehouse", "city", "       trade_base_income_bonus bonus -1" + requiresByz);
		edb.addCapabilities("sea_trade", "docklands", "city", "       trade_base_income_bonus bonus -1" + requiresByz);
	}

	protected void ByzantiumPopulationMinus(int bonusCity, int bonusCastle, String requiresSuffix) throws PatcherLibBaseEx {
		// ##### POPULATION GROWTH MINUS - BIZANTIUM ONLY !! #####
		// # WALLS - low based trade minus
		// # CITIES ### :
		String attributeStr = "       population_growth_bonus bonus -";
		String requiresByz = " requires factions { byzantium, }" + requiresSuffix;

		int bonus = bonusCity;
		//exportDescrBuilding.addCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requires );
		edb.addCapabilities("core_building", "wooden_wall", "city", attributeStr + bonus + requiresByz);
		bonus++;
		edb.addCapabilities("core_building", "stone_wall", "city", attributeStr + bonus + requiresByz);
		bonus++;
		edb.addCapabilities("core_building", "large_stone_wall", "city", attributeStr + bonus + requiresByz);
		bonus++;
		edb.addCapabilities("core_building", "huge_stone_wall", "city", attributeStr + bonus + requiresByz);

		// # Castles #
		bonus = bonusCastle;
		//exportDescrBuilding.addCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requires );
		edb.addCapabilities("core_castle_building", "wooden_castle", "castle", attributeStr + bonus + requiresByz);
		bonus++;
		edb.addCapabilities("core_castle_building", "castle", "castle", attributeStr + bonus + requiresByz);
		bonus++;
		edb.addCapabilities("core_castle_building", "fortress", "castle", attributeStr + bonus + requiresByz);
		bonus++;
		edb.addCapabilities("core_castle_building", "citadel", "castle", attributeStr + bonus + requiresByz);
	}

	protected void turksRumBoost() {
		val unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry("rum", null, seljuksRumReplenishMult, null, edb);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdInteger("ByzantiumPopulationPenaltyMilitaryBuildings", "Byzantium Population Penalty Military Buildings",
				feature -> ((FactionsSpecifics) feature).getByzantiumPopulationPenaltyMilitaryBuildings(),
				(feature, value) -> ((FactionsSpecifics) feature).setByzantiumPopulationPenaltyMilitaryBuildings(value)));

		pars.add(new ParamIdInteger("ByzantiumPopulationPenaltyPlayer", "Byzantium Population Penalty Player",
				feature -> ((FactionsSpecifics) feature).getByzantiumPopulationPenaltyPlayer(),
				(feature, value) -> ((FactionsSpecifics) feature).setByzantiumPopulationPenaltyPlayer(value)));

		pars.add(new ParamIdInteger("ByzantiumPopulationPenaltyAI", "Byzantium Population Penalty AI",
				feature -> ((FactionsSpecifics) feature).getByzantiumPopulationPenaltyAi(),
				(feature, value) -> ((FactionsSpecifics) feature).setByzantiumPopulationPenaltyAi(value)));

		pars.add(new ParamIdDouble("ByzantiumReplenishMult", "Byzantium Replenish Mult",
				feature -> ((FactionsSpecifics) feature).getByzantiumReplenishMult(),
				(feature, value) -> ((FactionsSpecifics) feature).setByzantiumReplenishMult(value)));

		pars.add(new ParamIdDouble("SeljuksRumReplenishMult", "Seljuks Rum Replenish Mult",
				feature -> ((FactionsSpecifics) feature).getSeljuksRumReplenishMult(),
				(feature, value) -> ((FactionsSpecifics) feature).setSeljuksRumReplenishMult(value)));

		return pars;
	}

	@Getter @Setter private int byzantiumPopulationPenaltyPlayer;
	@Getter @Setter private int byzantiumPopulationPenaltyAi;
	@Getter @Setter private int byzantiumPopulationPenaltyMilitaryBuildings;
	@Getter @Setter private double byzantiumReplenishMult;
	@Getter @Setter private double seljuksRumReplenishMult;

	protected ExportDescrBuilding edb;
	protected ExportDescrUnitTyped exportDescrUnit;


	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.fromString("c856c808-119c-4d43-b6cd-d7680c68e04b");

	public FactionsSpecifics(boolean isEnabled) {
		super("Factions specifics Pros & Cons");
		setEnabled(isEnabled);

		addCategory("Campaign");
		addCategory("Economy");

		setDescriptionShort("Unique Pros & Cons for few factions");
		setDescriptionUrl("http://tmsship.wikidot.com/factions-specifics");
	}
}
