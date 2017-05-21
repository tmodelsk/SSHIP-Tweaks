package tm.mtwModPatcher.sship.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

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

		addCategory("Units");

		setDescriptionShort("Peasants Recruitment Removed, \"Peasants\" , \"Southern Peasants\", \"EE Peasants\" ");
		setDescriptionUrl("http://tmsship.wikidot.com/peasants-recruitment-removed");
	}
}
