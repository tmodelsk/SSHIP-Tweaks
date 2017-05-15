package tm.m2twModPatcher.sship.Obsolete;

import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-22.
 */
public class RecruitmentSlotsBoost extends Feature {

	@Override
	public void executeUpdates() throws Exception {

		_ExportDescrBuilding = fileEntityFactory.getFile(ExportDescrBuilding.class);
		registerUpdatedFile(_ExportDescrBuilding);

		// ### City Barracks ###

		//exportDescrBuilding.addRecruitemntSlotBonus("barracks", "town_guard", "city", 1);

//		exportDescrBuilding.addRecruitemntSlotBonus("barracks", "city_watch", "city", 1);
//		exportDescrBuilding.addRecruitemntSlotBonus("barracks", "militia_drill_square", "city", 2);
//		exportDescrBuilding.addRecruitemntSlotBonus("barracks", "militia_barracks", "city", 3);
//		exportDescrBuilding.addRecruitemntSlotBonus("barracks", "army_barracks", "city", 3);
//		exportDescrBuilding.addRecruitemntSlotBonus("barracks", "royal_armoury", "city", 3);

		// ## Castle ##
//		exportDescrBuilding.addRecruitemntSlotBonus("castle_barracks", "mustering_hall", "castle", 1);
//		exportDescrBuilding.addRecruitemntSlotBonus("castle_barracks", "garrison_quarters", "castle", 1);
//		exportDescrBuilding.addRecruitemntSlotBonus("castle_barracks", "drill_square", "castle", 2);
//		exportDescrBuilding.addRecruitemntSlotBonus("castle_barracks", "barracks", "castle", 3);
//		exportDescrBuilding.addRecruitemntSlotBonus("castle_barracks", "armoury", "castle", 4);

		_ExportDescrBuilding.insertIntoCityWallsAllCapabilities("		recruitment_slots 1 requires not event_counter freeze_recr_pool 1");
	}

	protected ExportDescrBuilding _ExportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public RecruitmentSlotsBoost() {
		super("Boost up recruitment slots number");
	}
}
