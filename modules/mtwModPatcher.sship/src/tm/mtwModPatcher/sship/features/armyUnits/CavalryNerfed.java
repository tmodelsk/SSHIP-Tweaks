package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.Tuple2;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherNotSupportedEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherUnexpectedEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.MountEffect;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.data.exportDescrUnit.WeaponStat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Created by tomek on 29.10.2017 */
public class CavalryNerfed extends Feature {
	@Override
	public void setParamsCustomValues() {
		cavalryDamageMult = 0.75;    // 0.5 ?
		spearmenBonusMult = 1.0;
		heavyInfBonusMult = 0.5;
		lightInfBonusMult = 0.0;
		lightInfBonus = 0;
		skimirhsInfBonus = -1;
		missileInfBonus = -2;
	}


	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		val infantryProcessed = new ArrayList<UnitDef>();	// safety checker
		val infantry = edu.getUnits().stream().filter(u -> u.isCategoryInfantry()).collect(Collectors.toList());

		val spears = infantry.stream().filter(u -> u.isClassSpearmen()).collect(Collectors.toList());
		infantryProcessed.addAll(spears);

		val heavyInf = infantry.stream().filter(u -> u.isClassHeavy()).collect(Collectors.toList());
		infantryProcessed.addAll(heavyInf);

		val lightInf = infantry.stream().filter(u -> u.isClassLight()).collect(Collectors.toList());
		infantryProcessed.addAll(lightInf);

		val skimirhes = infantry.stream().filter(u -> u.isClassSkirmish()).collect(Collectors.toList());
		infantryProcessed.addAll(skimirhes);

		val missiles = infantry.stream().filter(u -> u.isClassMissile()).collect(Collectors.toList());
		infantryProcessed.addAll(missiles);

		// ### Safety Checks ###
		val restInfantry = infantry.stream().filter(u -> !infantryProcessed.contains(u)).collect(Collectors.toList());
		if (restInfantry.size() > 0)
			throw new PatcherLibBaseEx(Ctm.msgFormat("Problem with dividing infantry into categories, {0} are left!", restInfantry.size()));

		val categoriesSum = heavyInf.size() + lightInf.size() + spears.size() + skimirhes.size() + missiles.size() + restInfantry.size();
		val infantrySize = infantry.size();

		if( categoriesSum != infantrySize)
			consoleLogger.writeLine(Ctm.msgFormat("WARNING: Problem with dividing infantry into categories, {0} in categories, {1} total", categoriesSum, infantrySize));

		addCavalryBonusMult(spears, spearmenBonusMult);
		addCavalryBonusMult(heavyInf, heavyInfBonusMult);
		addCavalryBonusMult(lightInf, lightInfBonusMult);
		addCavalryBonus(lightInf, lightInfBonus);
		addCavalryBonus(skimirhes, skimirhsInfBonus);
		addCavalryBonus(missiles, missileInfBonus);

		infantryBonuses = infantryBonuses.stream().sorted( (ib2, ib1) -> Integer.compare(ib1.getItem2(), ib2.getItem2()) ).collect(Collectors.toList());


		if (cavalryDamageMult != 1.0) {
			val cavs = edu.getUnits().stream()
					.filter(u -> u.isCategoryCavalry()).collect(Collectors.toList());

			cavs.forEach(cav -> processCavalryUnit(cav));
		}
	}

	private void addCavalryBonusMult(List<UnitDef> units, double mult) {
		if(mult != 0.0) {
			int bonus;
			for (val unit : units) {
				bonus = (int) (unit.StatPri.Damage * mult);
				addCavalryBonus(unit, bonus);
			}
		}
	}
	private void addCavalryBonus(List<UnitDef> units, int bonus) {
		if(bonus != 0.0)
			for (val unit : units) {
				addCavalryBonus(unit, bonus);
			}
	}

	private void addCavalryBonus(UnitDef unit, int bonus) {
		unit.MountEffect.addEffectOffset(MountEffect.HORSE, bonus);
		unit.MountEffect.addEffectOffset(MountEffect.CAMEL, bonus);

		infantryBonuses.add(new Tuple2<>(unit, bonus));
	}

	private List<Tuple2<UnitDef, Integer>> infantryBonuses = new ArrayList<>();

	private static boolean isSecondaryMelee(UnitDef u) {
		return u.StatSec.WeaponType.equals("melee") && u.StatSec.Damage > 0;
	}

	private void processCavalryUnit(UnitDef unitDef) {

		if(unitDef.Name.equals("Caballeros Hidalgos"))
		{String xxx = "break";}

		WeaponStat stat;
		Boolean chargeUpdate = false;

		if(unitDef.isClassSkirmish() || unitDef.isClassMissile()) {
			stat = unitDef.StatSec;
			chargeUpdate = true;
		}
		else if(unitDef.isClassHeavy() || unitDef.isClassLight()) {
			if(isSecondaryMelee(unitDef)) stat = unitDef.StatSec;
			else {
				stat = unitDef.StatPri;
				chargeUpdate = true;
			}
		}
		else throw new PatcherNotSupportedEx("Unable to process "+unitDef.Name + " " + unitDef.Class);


		val damageOrg = stat.Damage;

		stat.Damage = 1 + (int) ((damageOrg - 1) * cavalryDamageMult);
		val damageOffset = damageOrg - stat.Damage;

		if (damageOffset != 0) {
			unitDef.MountEffect.addEffectOffset(MountEffect.HORSE, damageOffset);
			unitDef.MountEffect.addEffectOffset(MountEffect.CAMEL, damageOffset);
		}

		if(chargeUpdate && damageOffset > 0)
				stat.ChargeBonus += damageOffset;
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdDouble("cavDamageMulti", "Cavalry Damage Multiplier"
				, f -> ((CavalryNerfed) f).getCavalryDamageMult()
				, (f, value) -> ((CavalryNerfed) f).setCavalryDamageMult(value)));

		pars.add(new ParamIdDouble("spearmenBonusMult", "Spearmen Bonus Multiplier"
				, f -> ((CavalryNerfed) f).getSpearmenBonusMult()
				, (f, value) -> ((CavalryNerfed) f).setSpearmenBonusMult(value)));

		pars.add(new ParamIdDouble("heavyInfBonusMult", "Heavy Infantry Bonus Multiplier"
				, f -> ((CavalryNerfed) f).getHeavyInfBonusMult()
				, (f, value) -> ((CavalryNerfed) f).setHeavyInfBonusMult(value)));

		pars.add(new ParamIdDouble("lightInfBonusMult", "Light Infantry Bonus Multiplier"
				, f -> ((CavalryNerfed) f).getLightInfBonusMult()
				, (f, value) -> ((CavalryNerfed) f).setLightInfBonusMult(value)));

		pars.add(new ParamIdInteger("lightInfBonus", "Light Infantry Bonus"
				, f -> ((CavalryNerfed) f).getLightInfBonus()
				, (f, value) -> ((CavalryNerfed) f).setLightInfBonus(value)));

		pars.add(new ParamIdInteger("skimirshInfBonus", "Skimirsh Infantry Bonus"
				, f -> ((CavalryNerfed) f).getSkimirhsInfBonus()
				, (f, value) -> ((CavalryNerfed) f).setSkimirhsInfBonus(value)));

		pars.add(new ParamIdInteger("missileInfBonus", "Missile Infantry Bonus"
				, f -> ((CavalryNerfed) f).getMissileInfBonus()
				, (f, value) -> ((CavalryNerfed) f).setMissileInfBonus(value)));

		return pars;
	}

	@Getter @Setter private double cavalryDamageMult;
	@Getter @Setter private double spearmenBonusMult;
	@Getter @Setter private double heavyInfBonusMult;
	@Getter @Setter private double lightInfBonusMult;
	@Getter @Setter private int lightInfBonus;
	@Getter @Setter private int skimirhsInfBonus;
	@Getter @Setter private int missileInfBonus;


	private ExportDescrUnitTyped edu;

	public CavalryNerfed() {
		super("Cavalry Nerfed");

		addCategory("Battle");
		addCategory("Units");

		setDescriptionShort("All Cavalry units are weaker (melee non charge damage) against infantry");
		setDescriptionUrl("http://tmsship.wikidot.com/cavalry-nerfed");
	}

	@Override
	public UUID getId() {
		return Id;
	}

	public static final UUID Id = UUID.fromString("445e3668-396a-4fd8-9a40-4f46ba357b51");
}
