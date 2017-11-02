package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;

import java.util.UUID;
import java.util.stream.Collectors;

/** Created by tomek on 24.08.2017. */
public class NonSpearInfantryAttackBoost extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		val all = edu.getUnits();
		val infantry = all.stream().filter( u -> u.isCategoryInfantry() ).collect(Collectors.toList());
		val meleePri = infantry.stream().filter( u -> u.StatPri.WeaponType != null && u.StatPri.WeaponType.equals("melee") );
		val noSpearPri = meleePri.filter( u -> u.StatPri.SoundType != null
				&& !u.StatPri.SoundType.equals("spear")
				&& !u.StatPri.SoundType.equals("knife")
				&& !u.hasFormation(UnitDef.FORMATION_PHALANX)
				&& !u.Name.toUpperCase().contains("BILLMEN")
				&& !u.Name.toUpperCase().contains("BERDICHE")
				&& !u.Name.toUpperCase().contains("HALBERD")

		);

		val unitsPri = noSpearPri.collect(Collectors.toList());
		unitsPri.forEach( u -> u.StatPri.Damage++ );

		StringBuilder strB = new StringBuilder();
		strB.append("Units StatPri Attack+:");
		unitsPri.forEach( u -> strB.append(u.Name).append(", ") );

		this.consoleLogger.writeLine(strB.toString());

//		val meleeSec = infantry.stream().filter( u -> u.StatSec.WeaponType != null && u.StatSec.WeaponType.equals("melee") );
//		val noSpearSec = meleeSec.filter( u -> u.StatSec.SoundType != null && !u.StatSec.SoundType.equals("spear") && !u.StatSec.SoundType.equals("knife") );
//
//		val unitsSec = noSpearSec.collect(Collectors.toList());
//		unitsSec.forEach( u -> u.StatSec.Damage++ );

		//val x="break";
	}

	private ExportDescrUnitTyped edu;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("f56c9fb3-4c28-4e0d-a9ca-fef068970eee");

	public NonSpearInfantryAttackBoost() {
		super("Non Spear Infantry Attack Boost");

		addCategory("Units");

		setDescriptionShort("Non Spear Infantry Attack Boost +1");
		setDescriptionUrl("http://tmsship.wikidot.com/non-spear-infantry-attack-boost");
	}
}
