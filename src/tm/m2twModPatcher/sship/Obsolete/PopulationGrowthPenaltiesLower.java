package tm.m2twModPatcher.sship.Obsolete;

import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-25.
 * This Feature lowers population (food) growth penaltiesd
 */
public class PopulationGrowthPenaltiesLower  extends Feature{

	protected ExportDescrBuilding _ExportDescrBuilding;

	@Override
	public void executeUpdates() throws Exception {

		_ExportDescrBuilding = fileEntityFactory.getFile(ExportDescrBuilding.class);
		registerUpdatedFile(_ExportDescrBuilding);

		// ### City Barracks ###
//		exportDescrBuilding.addPopulationGrowthBonus("barracks", "town_guard", "city", 1);				// -1
//		exportDescrBuilding.addPopulationGrowthBonus("barracks", "city_watch", "city", 2);				// -2
//		exportDescrBuilding.addPopulationGrowthBonus("barracks", "militia_drill_square", "city", 1);	// -2
//		exportDescrBuilding.addPopulationGrowthBonus("barracks", "militia_barracks", "city", 1);		// -3
//		exportDescrBuilding.addPopulationGrowthBonus("barracks", "army_barracks", "city", 1);			// -5
//		exportDescrBuilding.addPopulationGrowthBonus("barracks", "royal_armoury", "city", 1);			// -4
//
//		// ### Castle ###
//		// # Barracks #
//		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "garrison_quarters", "castle", 1);		// -1
//		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "drill_square", "castle", 2);			// -2
//		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "barracks", "castle", 2);				// -3
//		exportDescrBuilding.addPopulationGrowthBonus("castle_barracks", "armoury", "castle", 1);				// -3
//
//		// # Archers #
//		exportDescrBuilding.addPopulationGrowthBonus("missiles", "archery_range", "castle", 1);		// -1
//		exportDescrBuilding.addPopulationGrowthBonus("missiles", "marksmans_range", "castle", 1);		// -2

		// ## Gallows ##
		//exportDescrBuilding.addPopulationGrowthBonus("castle_gallows", "c_gallows", null, 1);		// -5 player, -3 AI
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public PopulationGrowthPenaltiesLower() {
		super("Lower penalties of growth");
	}
}
