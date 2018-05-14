package tm.mtwModPatcher.sship.features.buildings;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.sship.features.global.ReligionReworked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static tm.mtwModPatcher.sship.lib.Buildings.*;

/**  */
public class TemplesTweaks extends Feature {

	@Override
	public void setParamsCustomValues() { }

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);

		/* ### Muslims Mosques ### */
		// # City #
		edb.setBuildingSettlementRequirement(TempleMuslimCity.name, "masjid", CityType, SettlementLevel.L1_Village);

		// # Castle #
		edb.setBuildingSettlementRequirement(TempleMuslimCastle.name, "c_small_masjid", CastleType, SettlementLevel.L1_Village);

		/* ### Catholic Churches ### */
		// # City #
		edb.setBuildingSettlementRequirement(TempleCatholicCity, "small_church", CityType, SettlementLevel.L1_Village);

		// # Castle #
		edb.setBuildingSettlementRequirement(TempleCatholicCastle, "small_chapel", CastleType, SettlementLevel.L1_Village);

		/* ### Catholic Monasteries ### */
		// # City #
		edb.setBuildingSettlementRequirement(MonasteryCatholicCity, "monastery", CityType, SettlementLevel.L1_Village);

		// # Castle #
		edb.setBuildingSettlementRequirement(MonasteryCatholicCastle, "castle_monastery", CastleType, SettlementLevel.L1_Village);
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val res = new HashSet<UUID>();
		res.add(ReligionReworked.Id);
		return res;
	}

	private ExportDescrBuilding edb;

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
