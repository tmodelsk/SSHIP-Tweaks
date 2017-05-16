package tm.mtwModPatcher.sship.Obsolete;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-27.
 */
	public class FreeUpkeepEconomy extends Feature {

	protected ExportDescrBuilding _ExportDescrBuilding;
	protected ExportDescrUnitTyped _ExportDescrUnit;

	@Override
	public void executeUpdates() throws Exception {

		_ExportDescrBuilding = fileEntityFactory.getFile(ExportDescrBuilding.class);
		registerUpdatedFile(_ExportDescrBuilding);
		_ExportDescrUnit = fileEntityFactory.getFile(ExportDescrUnitTyped.class);
		registerUpdatedFile(_ExportDescrUnit);

		_ExportDescrUnit.setAttributeForAllUnits("free_upkeep_unit");

		// ### CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wall

		// ## Village ## - no bonus - no building
		int wallsBonus = 2;

		// # Walls #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		// # Barracks #
		// town_watch town_guard city_watch militia_drill_square militia_barracks army_barracks royal_armoury
		int barracksBonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("barracks", "town_watch" , "city", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("barracks", "town_guard" , "city", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("barracks", "city_watch" , "city", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("barracks", "militia_drill_square" , "city", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("barracks", "militia_barracks" , "city", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("barracks", "army_barracks" , "city", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("barracks", "royal_armoury" , "city", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		// # Local Reqruitment # - building professional_city
		// levels p_large_town p_city
		int localRecrBonus = 1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("professional_city", "p_large_town" , "city", "       free_upkeep bonus "+localRecrBonus );
		localRecrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("professional_city", "p_city" , "city", "       free_upkeep bonus "+localRecrBonus );
		localRecrBonus++;


		// ### CASTLES ###
		// # Walls # - building core_castle_building
		// levels motte_and_bailey wooden_castle castle fortress citadel
		wallsBonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", "       free_upkeep bonus "+wallsBonus );
		wallsBonus++;

		// # Barracks # : building castle_barracks
		// levels mustering_hall garrison_quarters drill_square barracks armoury
		barracksBonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_barracks", "mustering_hall" , "castle", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_barracks", "garrison_quarters" , "castle", "       free_upkeep bonus "+barracksBonus );
//		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_barracks", "drill_square" , "castle", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_barracks", "barracks" , "castle", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("castle_barracks", "armoury" , "castle", "       free_upkeep bonus "+barracksBonus );
		barracksBonus++;

		// # Stables # : building equestrian
		// levels stables knights_stables barons_stables earls_stables kings_stables
		int stablesBonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("equestrian", "stables" , "castle", "       free_upkeep bonus "+stablesBonus );
		stablesBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("equestrian", "knights_stables" , "castle", "       free_upkeep bonus "+stablesBonus );
		stablesBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("equestrian", "barons_stables" , "castle", "       free_upkeep bonus "+stablesBonus );
		stablesBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("equestrian", "earls_stables" , "castle", "       free_upkeep bonus "+stablesBonus );
		stablesBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("equestrian", "kings_stables" , "castle", "       free_upkeep bonus "+stablesBonus );
		stablesBonus++;

		// # Ranges # : building missiles
		// levels bowyer practice_range archery_range marksmans_range
		int rangesBonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("missiles", "bowyer" , "castle", "       free_upkeep bonus "+rangesBonus );
		rangesBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("missiles", "practice_range" , "castle", "       free_upkeep bonus "+rangesBonus );
		rangesBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("missiles", "archery_range" , "castle", "       free_upkeep bonus "+rangesBonus );
		rangesBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("missiles", "marksmans_range" , "castle", "       free_upkeep bonus "+rangesBonus );
		rangesBonus++;

	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public FreeUpkeepEconomy() {
		super("Large free upkeeps to boost economy for defending nations (small ones)");
	}
}
