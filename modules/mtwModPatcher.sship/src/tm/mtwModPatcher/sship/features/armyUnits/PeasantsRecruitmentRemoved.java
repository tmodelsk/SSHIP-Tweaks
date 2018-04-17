package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static tm.mtwModPatcher.sship.lib.Units.PEASANT_ARCHERS;

/** PeasantsRecruitmentRemoved  */
public class PeasantsRecruitmentRemoved extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		val units = new ArrayList<String>();

		val peasantInfantry = Arrays.asList("Peasants" , "Southern Peasants", "EE Peasants");
		val peasantArchers = Arrays.asList(PEASANT_ARCHERS);

		units.addAll(peasantInfantry);
		units.addAll(peasantArchers);

		units.forEach(unitName -> exportDescrBuilding.removeUnitRecruitment(unitName));
	}

	private ExportDescrBuilding exportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("69dbb261-6d1f-44b6-b423-e019d35da5ae");

	public PeasantsRecruitmentRemoved() {
		setName("Peasants Recruitment Removed");

		addCategory("Units");

		setDescriptionShort("Peasants Recruitment Removed, \"Peasants\" , \"Southern Peasants\", \"EE Peasants\" ,  "+PEASANT_ARCHERS);
		setDescriptionUrl("http://tmsship.wikidot.com/peasants-recruitment-removed");
	}
}
