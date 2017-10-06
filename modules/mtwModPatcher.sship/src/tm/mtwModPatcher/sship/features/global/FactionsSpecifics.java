package tm.mtwModPatcher.sship.features.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.managers.UnitsManager;
import tm.mtwModPatcher.sship.lib.UnitRecruitmentSshipQueries;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**  */
public class FactionsSpecifics extends Feature {

	@Getter @Setter
	private int byzantiumPopulationPenaltyPlayer = 9;	// 7
	@Getter @Setter
	private int byzantiumPopulationPenaltyAi = 11;		// 9
	@Getter @Setter
	private int byzantiumPopulationPenaltyMilitaryBuildings = 2;
	@Getter @Setter
	private double byzantiumReplenishMult = 0.5;
	@Getter @Setter
	private double seljuksRumReplenishMult = 1.5;

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		Byzantium();

		//RomanCatholicTradersBonuses();
		NorwegianDanesBoostReplenishRates();
		CumansBoostReplenishRates();
		HolyRomanEmpireLawMinus();
		turksRumBoost();

		// ### DeBuff Crusade States - minus LAW
		edb.insertIntoCityCastleWallsCapabilities("       law_bonus bonus -2 requires factions { jerusalem, }");

		// France: Eldest Daughter of the Church - Religion Bonus
		edb.insertIntoCityCastleWallsCapabilities("       religion_level bonus 1 requires factions { france, }");

		// Moors: -1 Law
		edb.insertIntoCityCastleWallsCapabilities("		law_bonus bonus -1 requires factions { moors, }");
	}

	protected void HolyRomanEmpireLawMinus() throws PatcherLibBaseEx {

		edb.insertIntoCityCastleWallsCapabilities("		law_bonus bonus -3 requires factions { hre, }");
	}

	protected void NorwegianDanesBoostReplenishRates() throws PatcherLibBaseEx {
		UnitsManager unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry(Arrays.asList("norway","denmark"), null, 1.5, null, edb);
	}

	protected void CumansBoostReplenishRates() throws PatcherLibBaseEx {
		val unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry("cumans", null, 2.0, null, edb);
	}

	protected void RomanCatholicTradersBonuses() throws PatcherLibBaseEx {
		String requires = " requires factions { aragon,  portugal, pisa, venice, england, jerusalem, }";
		String attributeStr;

		// # MARKETS
		attributeStr = "       trade_base_income_bonus bonus 1";
		edb.insertIntoBuildingCapabilities("market", "corn_exchange" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("market", "market" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("market", "fairground" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("market", "great_market" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("market", "merchants_quarter" , "city", attributeStr + requires );
		// corn_exchange market fairground
		edb.insertIntoBuildingCapabilities("market_castle", "corn_exchange" , "castle", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("market_castle", "market" , "castle", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("market_castle", "fairground" , "castle", attributeStr + requires );

		// # PORTS: port shipwright dockyard naval_drydock
		edb.insertIntoBuildingCapabilities("port", "port" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("port", "shipwright" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("port", "dockyard" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("port", "naval_drydock" , "city", attributeStr + requires );
		// c_port c_shipwright c_dockyard c_naval_drydock
		edb.insertIntoBuildingCapabilities("castle_port", "c_port" , "castle", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("castle_port", "c_dockyard" , "castle", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("castle_port", "c_naval_drydock" , "castle", attributeStr + requires );

		// # SEA TRADES : merchants_wharf warehouse docklands
		edb.insertIntoBuildingCapabilities("sea_trade", "merchants_wharf" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("sea_trade", "warehouse" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("sea_trade", "docklands" , "city", attributeStr + requires );

		edb.insertIntoBuildingCapabilities("castle_sea_trade", "merchants_wharf" , "castle", attributeStr + requires );
	}

	protected void Byzantium() throws PatcherLibBaseEx {

		ByzantiumMilitaryBuildingsPenalties();

		// ##### Additional PRODUCTIVITY MINUS BIZANTIUM ONLY - on trade goods low based level #####
		ByzantiumTradeMinus();

		// ##### POPULATION GROWTH MINUS - BIZANTIUM ONLY !! #####
		if(byzantiumPopulationPenaltyPlayer > 0)
			ByzantiumPopulationMinus(byzantiumPopulationPenaltyPlayer,byzantiumPopulationPenaltyPlayer-1 , " and event_counter is_the_player 1");		// Player version
		if(byzantiumPopulationPenaltyAi > 0)
			ByzantiumPopulationMinus(byzantiumPopulationPenaltyAi,byzantiumPopulationPenaltyAi-1 , " and not event_counter is_the_player 1");	// AI version

		// Longer units replenishes
		val unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry("byzantium", null, byzantiumReplenishMult, null, edb);
	}

	protected void ByzantiumMilitaryBuildingsPenalties() throws PatcherLibBaseEx {
		String requirements = "factions { " + FactionsDefs.byzantiumCsv() + " } ";
		int bonus = - byzantiumPopulationPenaltyMilitaryBuildings;

		if(bonus != 0) {
			// ### City Barracks ### : levels town_watch town_guard city_watch militia_drill_square militia_barracks army_barracks royal_armoury
			edb.addPopulationGrowthBonus("barracks", "town_watch", "city", bonus , requirements);				// 0
			edb.addPopulationGrowthBonus("barracks", "town_guard", "city", bonus , requirements);				// -1
			edb.addPopulationGrowthBonus("barracks", "city_watch", "city", bonus , requirements);				// -2
			edb.addPopulationGrowthBonus("barracks", "militia_drill_square", "city", bonus , requirements);	// -2
			edb.addPopulationGrowthBonus("barracks", "militia_barracks", "city", bonus , requirements);		// -3
			edb.addPopulationGrowthBonus("barracks", "army_barracks", "city", bonus , requirements);			// -5
			edb.addPopulationGrowthBonus("barracks", "royal_armoury", "city", bonus , requirements);			// -4

			// ### Castle ### : levels mustering_hall garrison_quarters drill_square barracks armoury
			// # Barracks #
			edb.addPopulationGrowthBonus("castle_barracks", "mustering_hall", "castle", bonus , requirements);		// 0
			edb.addPopulationGrowthBonus("castle_barracks", "garrison_quarters", "castle", bonus , requirements);		// -1
			edb.addPopulationGrowthBonus("castle_barracks", "drill_square", "castle", bonus , requirements);			// -2
			edb.addPopulationGrowthBonus("castle_barracks", "barracks", "castle", bonus , requirements);				// -3
			edb.addPopulationGrowthBonus("castle_barracks", "armoury", "castle", bonus , requirements);				// -3

			// # Archers # : levels bowyer practice_range archery_range marksmans_range
			edb.addPopulationGrowthBonus("missiles", "bowyer", "castle", bonus , requirements);				// 0
			edb.addPopulationGrowthBonus("missiles", "practice_range", "castle", bonus , requirements);		// 0
			edb.addPopulationGrowthBonus("missiles", "archery_range", "castle", bonus , requirements);		// -1
			edb.addPopulationGrowthBonus("missiles", "marksmans_range", "castle", bonus , requirements);		// -2

			// # Stables # : levels stables knights_stables barons_stables earls_stables kings_stables
			edb.addPopulationGrowthBonus("equestrian", "stables", "castle", bonus , requirements);			// 0
			edb.addPopulationGrowthBonus("equestrian", "knights_stables", "castle", bonus , requirements);	// 0
			edb.addPopulationGrowthBonus("equestrian", "barons_stables", "castle", bonus , requirements);		// -1
			edb.addPopulationGrowthBonus("equestrian", "earls_stables", "castle", bonus , requirements);		// -2
			edb.addPopulationGrowthBonus("equestrian", "kings_stables", "castle", bonus , requirements);		// -3
		}
	}

	protected void ByzantiumTradeMinus() throws PatcherLibBaseEx {
		// ##### Additional PRODUCTIVITY MINUS BIZANTIUM ONLY - on trade goods low based level #####
		String requiresByz = " requires factions { byzantium, }", newLine;
		// # WALLS - low based trade minus
		// # CITIES ### :
		String attributeStr = "       trade_base_income_bonus bonus -2";	// -1
		newLine = attributeStr + requiresByz;
		edb.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", newLine );
		edb.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", newLine);
		edb.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", newLine );
		edb.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", newLine );
		edb.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", newLine );
		// # Castles #
		edb.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", newLine );
		edb.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", newLine);
		edb.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", newLine );
		edb.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", newLine );
		edb.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", newLine );


		// ###### TRADE BUIDLINGS MINUS - Bizantium ONLY #####
		// # MARKETS
		// corn_exchange market fairground great_market merchants_quarter
		attributeStr = "       trade_base_income_bonus bonus -2";
		newLine = attributeStr + requiresByz;
		edb.insertIntoBuildingCapabilities("market", "corn_exchange" , "city", newLine );
		edb.insertIntoBuildingCapabilities("market", "market" , "city", newLine );
		edb.insertIntoBuildingCapabilities("market", "fairground" , "city", newLine );
		edb.insertIntoBuildingCapabilities("market", "great_market" , "city", newLine );
		edb.insertIntoBuildingCapabilities("market", "merchants_quarter" , "city", newLine );
		// corn_exchange market fairground
		edb.insertIntoBuildingCapabilities("market_castle", "market" , "castle", newLine );
		edb.insertIntoBuildingCapabilities("market_castle", "fairground" , "castle", newLine );

		// # PORTS
		// port shipwright dockyard naval_drydock
		edb.insertIntoBuildingCapabilities("port", "shipwright" , "city", newLine );
		edb.insertIntoBuildingCapabilities("port", "dockyard" , "city", newLine );
		edb.insertIntoBuildingCapabilities("port", "naval_drydock" , "city", newLine );
		// c_port c_shipwright c_dockyard c_naval_drydock
		edb.insertIntoBuildingCapabilities("castle_port", "c_dockyard" , "castle", newLine );
		edb.insertIntoBuildingCapabilities("castle_port", "c_naval_drydock" , "castle", newLine );

		// # SEA TRADES
		edb.insertIntoBuildingCapabilities("sea_trade", "warehouse" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
		edb.insertIntoBuildingCapabilities("sea_trade", "docklands" , "city", "       trade_base_income_bonus bonus -1" + requiresByz );
	}

	protected void ByzantiumPopulationMinus(int bonusCity, int bonusCastle, String requiresSuffix) throws PatcherLibBaseEx {
		// ##### POPULATION GROWTH MINUS - BIZANTIUM ONLY !! #####
		// # WALLS - low based trade minus
		// # CITIES ### :
		String attributeStr = "       population_growth_bonus bonus -";
		String requiresByz = " requires factions { byzantium, }" + requiresSuffix;

		int bonus = bonusCity;
		//exportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr + bonus + requiresByz);
		bonus++;
		edb.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr + bonus + requiresByz );
		bonus++;
		edb.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr + bonus + requiresByz );
		bonus++;
		edb.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr + bonus + requiresByz );

		// # Castles #
		bonus = bonusCastle;
		//exportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr + requires );
		edb.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr + bonus  + requiresByz);
		bonus++;
		edb.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr + bonus + requiresByz );
		bonus++;
		edb.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr + bonus + requiresByz );
		bonus++;
		edb.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr + bonus + requiresByz );
	}

	protected void turksRumBoost() {
		val unitsManager = new UnitsManager();
		unitsManager.updateOrAddReplenishBonusEntry("rum", null, seljuksRumReplenishMult, null, edb);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdInteger("ByzantiumPopulationPenaltyMilitaryBuildings", "Byzantium Population Penalty Military Buildings",
				feature -> ((FactionsSpecifics)feature).getByzantiumPopulationPenaltyMilitaryBuildings(),
				(feature, value) -> ((FactionsSpecifics)feature).setByzantiumPopulationPenaltyMilitaryBuildings(value)));

		pars.add(new ParamIdInteger("ByzantiumPopulationPenaltyPlayer", "Byzantium Population Penalty Player",
				feature -> ((FactionsSpecifics)feature).getByzantiumPopulationPenaltyPlayer(),
				(feature, value) -> ((FactionsSpecifics)feature).setByzantiumPopulationPenaltyPlayer(value)));

		pars.add(new ParamIdInteger("ByzantiumPopulationPenaltyAI", "Byzantium Population Penalty AI",
				feature -> ((FactionsSpecifics)feature).getByzantiumPopulationPenaltyAi(),
				(feature, value) -> ((FactionsSpecifics)feature).setByzantiumPopulationPenaltyAi(value)));

		pars.add(new ParamIdDouble("ByzantiumReplenishMult", "Byzantium Replenish Mult",
				feature -> ((FactionsSpecifics)feature).getByzantiumReplenishMult(),
				(feature, value) -> ((FactionsSpecifics)feature).setByzantiumReplenishMult(value)));

		pars.add(new ParamIdDouble("SeljuksRumReplenishMult", "Seljuks Rum Replenish Mult",
				feature -> ((FactionsSpecifics)feature).getSeljuksRumReplenishMult(),
				(feature, value) -> ((FactionsSpecifics)feature).setSeljuksRumReplenishMult(value)));

		return pars;
	}

	protected ExportDescrBuilding edb;
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
