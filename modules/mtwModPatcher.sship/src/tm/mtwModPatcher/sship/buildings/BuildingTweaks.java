package tm.mtwModPatcher.sship.buildings;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-23.
 */
public class BuildingTweaks extends Feature {

	protected ExportDescrBuilding _ExportDescrBuilding;

	@Override
	public void executeUpdates() throws Exception {

		_ExportDescrBuilding = fileEntityFactory.getFile(ExportDescrBuilding.class);
		registerUpdatedFile(_ExportDescrBuilding);

		_ExportDescrBuilding.setBuildingSettlementRequirement("mines", "mines", "city", "town");
		_ExportDescrBuilding.setBuildingSettlementRequirement("castle_mines", "c_mines", "castle", "town");

		_ExportDescrBuilding.setBuildingSettlementRequirement("castle_gallows", "c_gallows", null, "village");

		// orphan Hospital : + Health Bonus
		_ExportDescrBuilding.insertIntoBuildingCapabilities("orphan", "foundling_hospital", "city", "				population_health_bonus bonus 1");
		_ExportDescrBuilding.insertIntoBuildingCapabilities("orphan", "orphanage", "city", "				population_health_bonus bonus 2");

		// city_hall :  levels town_hall council_chambers city_hall mayors_palace
		//exportDescrBuilding.insertIntoBuildingCapabilities("city_hall", "council_chambers" , "city", "       recruitment_slots bonus 1  requires not event_counter freeze_recr_pool 1");
		//exportDescrBuilding.insertIntoBuildingCapabilities("city_hall", "city_hall" , "city", "       recruitment_slots bonus 1  requires not event_counter freeze_recr_pool 1");
		//exportDescrBuilding.insertIntoBuildingCapabilities("city_hall", "mayors_palace" , "city", "       recruitment_slots bonus 1  requires not event_counter freeze_recr_pool 1");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public BuildingTweaks() {
		super("Buildings Tweaks");

		addCategory("Campaign");

		setDescriptionShort("Small buildings tweaks.");
		setDescriptionUrl("http://tmsship.wikidot.com/buildings-tweaks");
	}
}
