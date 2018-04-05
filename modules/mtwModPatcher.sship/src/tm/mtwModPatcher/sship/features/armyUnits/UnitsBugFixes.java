package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.WeaponStat;

import java.util.UUID;
import java.util.stream.Collectors;

/** Created by tomek on 29.08.2017. */
public class UnitsBugFixes extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		// ## Mailed Knights dismounted
		val mailedKnightsCav = edu.loadUnit("Mailed Knights");
		val mailedKnightsInf = edu.loadUnit("Dismounted Sword Mailed Knights");

		mailedKnightsInf.StatMental = mailedKnightsCav.StatMental;

		// ## Christian Guard Cavalry
		WeaponStat weaponStat; String comments;
		val christianGuardCav = edu.loadUnit("Christian Guard");

		weaponStat = christianGuardCav.StatPri;
		christianGuardCav.StatPri = mailedKnightsCav.StatPri;
		christianGuardCav.StatPri.Comments = weaponStat.Comments;

		weaponStat = christianGuardCav.StatSec;
		christianGuardCav.StatSec = mailedKnightsCav.StatSec;
		christianGuardCav.StatSec.Comments = weaponStat.Comments;

		val christianGuardInf = edu.loadUnit("Dismounted Christian Guard");

		weaponStat = christianGuardInf.StatPri;
		christianGuardInf.StatPri = mailedKnightsInf.StatPri;
		christianGuardInf.StatPri.Comments = weaponStat.Comments;

		comments = christianGuardInf.StatPriArmour.Comments;
		christianGuardInf.StatPriArmour = mailedKnightsInf.StatPriArmour;
		christianGuardInf.StatPriArmour.Comments = comments;

		comments = christianGuardInf.StatMental.Comments;
		christianGuardInf.StatMental = christianGuardCav.StatMental;
		christianGuardInf.StatMental.Comments = comments;

		// ## Abna Heavy Axemen should be heavy not spearmen ##
		val abnaAxe = edu.loadUnit("Abna Heavy Axemen").Class = "heavy";

		// ## Spear units that are Light classed
		val spearsStrange = edu.getUnits().stream()
				.filter(u -> u.isCategoryInfantry() &&
						(u.isClassLight() && u.StatPriAttr.containsSpearOrPike()))
				.collect(Collectors.toList());
		spearsStrange.forEach( u -> u.Class = "spearmen" );

		// ## Spear units that are Light classed
		val spearsVeryStrange = edu.getUnits().stream()
				.filter(u -> u.isCategoryInfantry() &&
						(!u.isClassSpearmen() && !u.isClassHeavy() && u.StatPriAttr.containsSpearOrPike()))
				.collect(Collectors.toList());
		if(spearsVeryStrange.size() > 0) throw new PatcherLibBaseEx(Ctm.msgFormat("There're spear infantry ({0}) with class not spear", spearsStrange.size()));

	}

	private ExportDescrUnitTyped edu;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("6eedc772-e16a-4ec2-8de4-833dcabda13d");

	public UnitsBugFixes() {
		super("Units Bug Fixes");

		addCategory("Units");

		setDescriptionShort("SSHIP units bug fixes");
		setDescriptionUrl("http://tmsship.wikidot.com/unitsbugfixes");
	}
}
