package tm.mtwModPatcher.sship.features.buildings;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.sship.lib.Buildings;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-23.
 */
public class BuildingTweaks extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {

		edb = fileEntityFactory.getFile(ExportDescrBuilding.class);
		registerForUpdate(edb);

		// MINES
		edb.setBuildingSettlementRequirement("mines", "mines", "city", "town");
		edb.setBuildingSettlementRequirement("castle_mines", "c_mines", "castle", "town");

		// GALLOWS
		edb.setBuildingSettlementRequirement("castle_gallows", "c_gallows", null, "village");

		// orphan Hospital : + Health Bonus
		edb.insertIntoBuildingCapabilities("orphan", "foundling_hospital", "city", "				population_health_bonus bonus 1");
		edb.insertIntoBuildingCapabilities("orphan", "orphanage", "city", "				population_health_bonus bonus 2");

		// PORT earlier:
		edb.setBuildingSettlementRequirement(Buildings.PortCity, Buildings.PortCityLevels.get(0), SettlType.City, SettlementLevel.L2_Town);
		edb.setBuildingSettlementRequirement(Buildings.PortCastle, Buildings.PortCastleLevels.get(0), SettlType.Castle, SettlementLevel.L2_Town);

		// SeaTrade earlier
		edb.setBuildingSettlementRequirement(Buildings.SeaTradeCity, Buildings.SeaTradeCityLevels.get(0), SettlType.City, SettlementLevel.L2_Town);
		edb.setBuildingSettlementRequirement(Buildings.SeaTradeCastle, Buildings.SeaTradeCastleLevels.get(0), SettlType.Castle, SettlementLevel.L2_Town);

		// city_hall :  levels town_hall council_chambers city_hall mayors_palace
		//exportDescrBuilding.insertIntoBuildingCapabilities("city_hall", "council_chambers" , "city", "       recruitment_slots bonus 1  requires not event_counter freeze_recr_pool 1");
		//exportDescrBuilding.insertIntoBuildingCapabilities("city_hall", "city_hall" , "city", "       recruitment_slots bonus 1  requires not event_counter freeze_recr_pool 1");
		//exportDescrBuilding.insertIntoBuildingCapabilities("city_hall", "mayors_palace" , "city", "       recruitment_slots bonus 1  requires not event_counter freeze_recr_pool 1");
	}

	private ExportDescrBuilding edb;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("0063ca7b-77d7-4e0a-bfc5-9eae4670adab");

	public BuildingTweaks() {
		super("Buildings Tweaks");

		addCategory("Campaign");

		setDescriptionShort("Small buildings tweaks.");
		setDescriptionUrl("http://tmsship.wikidot.com/buildings-tweaks");
	}
}
