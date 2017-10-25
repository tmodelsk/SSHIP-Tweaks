package tm.mtwModPatcher.sship.features.buildings;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;

import java.util.UUID;

/**  */
public class TemplesTweaks extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		_ExportDescrBuilding = fileEntityFactory.getFile(ExportDescrBuilding.class);
		registerForUpdate(_ExportDescrBuilding);

		// ### Muslims Mosques ###
		// # City #
		_ExportDescrBuilding.setBuildingSettlementRequirement("temple_muslim", "masjid", "city", SettlementLevel.L1_Village);

		// # Castle #
		_ExportDescrBuilding.setBuildingSettlementRequirement("temple_muslim_castle", "c_small_masjid", "castle", SettlementLevel.L1_Village);

		// ### Catholic Churches ###
		// # City #
		_ExportDescrBuilding.setBuildingSettlementRequirement("temple_catholic", "small_church", "city", SettlementLevel.L1_Village);
//		exportDescrBuilding.addLawBonus("temple_catholic", "small_church", "city", 1 );
//		exportDescrBuilding.addLawBonus("temple_catholic", "church", "city", 1 );
//		exportDescrBuilding.addLawBonus("temple_catholic", "abbey", "city", 2 );
//		exportDescrBuilding.addLawBonus("temple_catholic", "cathedral", "city", 2);
//		exportDescrBuilding.addLawBonus("temple_catholic", "huge_cathedral", "city", 3);

//		exportDescrBuilding.addRecruitemntSlotBonus("temple_catholic", "cathedral", "city", 1);
//		exportDescrBuilding.addRecruitemntSlotBonus("temple_catholic", "huge_cathedral", "city", 1);

		// # Castle #
		_ExportDescrBuilding.setBuildingSettlementRequirement("temple_catholic_castle", "small_chapel", "castle", SettlementLevel.L1_Village);
//		exportDescrBuilding.addLawBonus("temple_catholic_castle", "small_chapel", "castle", 1 );
//		exportDescrBuilding.addLawBonus("temple_catholic_castle", "chapel", "castle", 1 );
//		exportDescrBuilding.addRecruitemntSlotBonus("temple_catholic_castle", "chapel", "castle", 1 );

		// ### Catholic Monasteries ###
		// # City #
		_ExportDescrBuilding.setBuildingSettlementRequirement("friar", "monastery", "city", SettlementLevel.L1_Village);
//		exportDescrBuilding.addPopulationGrowthBonus("friar", "monastery", "city", 2 );
//		exportDescrBuilding.addPopulationGrowthBonus("friar", "medical_care", "city", 2 );
//		exportDescrBuilding.addPopulationGrowthBonus("friar", "hospital", "city", 3 );

		// # Castle #
		_ExportDescrBuilding.setBuildingSettlementRequirement("monastery_castle", "castle_monastery", "castle", SettlementLevel.L1_Village);
//		exportDescrBuilding.addPopulationGrowthBonus("monastery_castle", "castle_monastery", "castle", 2 );
	}

	protected ExportDescrBuilding _ExportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("cb98b8a2-4476-49c7-adf2-5b67e9c64781");

	public TemplesTweaks() {
		super("Temples Tweaks");

		addCategory("Campaign");

		setDescriptionShort("Temples are more important. They are available earlier to construct.");
		setDescriptionUrl("http://tmsship.wikidot.com/temples-tweaks");
	}
}
