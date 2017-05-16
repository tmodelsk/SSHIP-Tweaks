package tm.m2twModPatcher.sship.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.Arrays;
import java.util.UUID;

/**
 * PeasantsRecruitmentRemoved
 */
public class PeasantsRecruitmentRemoved extends Feature {
	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		val unitNames = Arrays.asList("Peasants" , "Southern Peasants", "EE Peasants");

		unitNames.forEach(unitName -> exportDescrBuilding.removeUnitRecruitment(unitName));
	}

	private ExportDescrBuilding exportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public PeasantsRecruitmentRemoved() {
		setName("Peasants Recruitment Removed");

		setDescriptionShort("Peasants Recruitment Removed, \"Peasants\" , \"Southern Peasants\", \"EE Peasants\" ");
	}
}
