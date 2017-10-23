package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;

import java.util.UUID;
import java.util.stream.Collectors;

/** Lowers Morale of all units by -1  */
public class LowerMorale extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		exportDescrUnit=getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		val units = exportDescrUnit.getUnits().stream().filter(u -> u.StatMental.Morale > 1).collect(Collectors.toList());

		for (val u: units) u.StatMental.Morale--;
	}

	private ExportDescrUnitTyped exportDescrUnit;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("1ef0bad9-58c7-409f-b815-1f0eb8b0832f");

	public LowerMorale() {
		setName("Lower Morale");
		addCategory("Unit");
		setDescriptionShort("Morale -1 for all units, good general is required");
		setDescriptionUrl("http://tmsship.wikidot.com/lower-morale");
	}
}
