package tm.mtwModPatcher.sship.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.data.BattleConfig;
import tm.mtwModPatcher.lib.data.aiSets.*;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;

import javax.xml.xpath.XPathExpressionException;
import java.util.UUID;

/** Longer Battles - killing hit ration is reduced */
public class LongerBattles extends Feature {

	@Getter @Setter
	private double meleeHitRate = 1.0; 						// 0.5
	@Getter @Setter
	private double missileCavalryAccuracy = 1.75;			// org 1.5
	@Getter @Setter
	private double chargeMultiplier = 1.1;					// 2.0
	@Getter @Setter
	private boolean powerfulChargeForHeavyInfantry = false;	// true
	@Getter @Setter
	private boolean powerfulChargeForCavalry = false;		// true

	// zmiany : power_charge attribute dla Cavalry heavy ? / Knights / Westers Knights ???
	// 			move_speed_mod    1.5 (wszystkie cav 1.5 ???)

	@Override
	public void executeUpdates() throws Exception {
		_ExportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		battleConfig = getFileRegisterForUpdated(BattleConfig.class);
		battleConfigSet1 = getFileRegisterForUpdated(BattleConfigSet1.class);
		battleConfigSet2 = getFileRegisterForUpdated(BattleConfigSet2.class);
		battleConfigSet3 = getFileRegisterForUpdated(BattleConfigSet3.class);
		battleConfigSet4 = getFileRegisterForUpdated(BattleConfigSet4.class);
		battleConfigSet5 = getFileRegisterForUpdated(BattleConfigSet5.class);

		// ## Set low melee-hit-rate
		updateBattleConfig(battleConfig, null);
		updateBattleConfig(battleConfigSet1, 1);
		updateBattleConfig(battleConfigSet2, 2);
		updateBattleConfig(battleConfigSet3, 3);
		updateBattleConfig(battleConfigSet4, 4);
		updateBattleConfig(battleConfigSet5, 5);

		val allUnits = _ExportDescrUnit.getUnits(); //_ExportDescrUnit.getUnits().stream().filter(u -> u.isCategoryCavalry()).collect(Collectors.toList());

		for (val unit : allUnits) {

			// ### Updating Charge ###
			unit.StatPri.ChargeBonus = (int)(unit.StatPri.ChargeBonus * chargeMultiplier);

			if(powerfulChargeForHeavyInfantry) {
				// ### All Heavy Infantry gets powerful_charge attribute
				if(unit.isCategoryInfantry() && unit.isClassHeavy())
					unit.addAttribute("powerful_charge");
			}

			if(powerfulChargeForCavalry) {
				// ### Cavalry : ###
				int heavyCharge = (int)(8 * chargeMultiplier);
				if(unit.isCategoryCavalry()) {
					// # Add powerful_charge for heavy chargers OR heavy units #
					if(unit.isClassHeavy() || unit.StatPri.ChargeBonus >= heavyCharge)
						unit.addAttribute("powerful_charge");
				}
			}
		}

	}

	private void updateBattleConfig(BattleConfig bt, Integer setNumber) throws XPathExpressionException {

		double meleeHitRate = this.meleeHitRate;

		if(setNumber != null) {
			double shift = 3 - setNumber;
			meleeHitRate += shift * 0.1;
		}

		bt.setValue("/config/combat-balancing/melee-hit-rate", meleeHitRate);
		bt.setValue("/config/combat-balancing/missile-target-accuracy/cavalry", meleeHitRate);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds()  {
		val parIds = new ArrayUniqueList<ParamId>();

		ParamIdDouble parDbl;
		parDbl= new ParamIdDouble("HitRatio", "Hit Ration",
				feature -> ((LongerBattles)feature).getMeleeHitRate(),
				(feature, value) -> ((LongerBattles)feature).setMeleeHitRate(value));
		parIds.add(parDbl);

		parDbl= new ParamIdDouble("MissileCavalryAccuracy", "Missile Cavalry Accuracy",
				feature -> ((LongerBattles)feature).getMissileCavalryAccuracy(),
				(feature, value) -> ((LongerBattles)feature).setMissileCavalryAccuracy(value));
		parIds.add(parDbl);

		parDbl= new ParamIdDouble("ChargeMulti" , "Charge Multiplier",
				feature -> ((LongerBattles)feature).getChargeMultiplier(),
				(feature, value) -> ((LongerBattles)feature).setChargeMultiplier(value));
		parIds.add(parDbl);


		parIds.add(new ParamIdBoolean("PowerfulChargeForHeavyInfantry" , "PowerfulCharge For Heavy Infantry",
				feature -> ((LongerBattles)feature).isPowerfulChargeForHeavyInfantry(),
				(feature, value) -> ((LongerBattles)feature).setPowerfulChargeForHeavyInfantry(value) ));

		parIds.add(new ParamIdBoolean("PowerfulChargeForCavalry" , "PowerfulCharge For Cavalry",
				feature -> ((LongerBattles)feature).isPowerfulChargeForCavalry(),
				(feature, value) -> ((LongerBattles)feature).setPowerfulChargeForCavalry(value) ));

		return parIds;
	}

	private ExportDescrUnitTyped _ExportDescrUnit;
	private BattleConfig battleConfig;
	private BattleConfigSet1 battleConfigSet1;
	private BattleConfigSet2 battleConfigSet2;
	private BattleConfigSet3 battleConfigSet3;
	private BattleConfigSet4 battleConfigSet4;
	private BattleConfigSet5 battleConfigSet5;


	public LongerBattles() {
		super("Longer Battles");

		addCategory("Battle");
		setDescriptionShort("Longer Battles - killing hit rate is reduced");

		descriptionUrl = "http://tmsship.wikidot.com/longer-battles";
		bugReportingUrl = null;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
