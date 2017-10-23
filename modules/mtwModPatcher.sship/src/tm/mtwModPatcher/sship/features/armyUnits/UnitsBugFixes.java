package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;

import java.util.UUID;

/**
 * Created by tomek on 29.08.2017.
 */
public class UnitsBugFixes extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		// Mailed Knights dismounted
		val mailedKnightsCav = edu.loadUnit("Mailed Knights");
		val mailedKnightsInf = edu.loadUnit("Dismounted Sword Mailed Knights");

		mailedKnightsInf.StatMental = mailedKnightsCav.StatMental;

		// Christian Guard Cavalry
		val christianGuardCav = edu.loadUnit("Christian Guard");
		christianGuardCav.StatPri = mailedKnightsCav.StatPri;
		christianGuardCav.StatSec = mailedKnightsCav.StatSec;

		val christianGuardInf = edu.loadUnit("Dismounted Christian Guard");
		christianGuardInf.StatPri = mailedKnightsInf.StatPri;
		christianGuardInf.StatPriArmour = mailedKnightsInf.StatPriArmour;
		christianGuardInf.StatMental = christianGuardCav.StatMental;


	}

	private ExportDescrUnitTyped edu;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("");

	public UnitsBugFixes() {
		super("Units Bug Fixes");

		addCategory("Units");

		setDescriptionShort("SSHIP units bug fixes");
		setDescriptionUrl("http://tmsship.wikidot.com/unitsbugfixes");
	}
}
