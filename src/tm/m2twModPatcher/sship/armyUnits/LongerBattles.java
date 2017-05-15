package tm.m2twModPatcher.sship.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.params.ParamId;
import tm.m2twModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.m2twModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.m2twModPatcher.lib.fileEntities.data.BattleConfig;
import tm.m2twModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;

import java.util.UUID;

/** Longer Battles - killing hit ration is reduced */
public class LongerBattles extends Feature {

	@Getter @Setter
	private double MeleeHitRate = 1.0; 						// 0.5
	@Getter @Setter
	private double ChargeMultiplier = 1.2;					// 2.0
	@Getter @Setter
	private boolean powerfulChargeForHeavyInfantry = false;	// true
	@Getter @Setter
	private boolean powerfulChargeForCavalry = false;		// true

	// zmiany : power_charge attribute dla Cavalry heavy ? / Knights / Westers Knights ???
	// 			move_speed_mod    1.5 (wszystkie cav 1.5 ???)

	@Override
	public void executeUpdates() throws Exception {
		_ExportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		_BattleConfig = getFileRegisterForUpdated(BattleConfig.class);

		// ## Set low melee-hit-rate
		_BattleConfig.setValue("/config/combat-balancing/melee-hit-rate", MeleeHitRate);


		val allUnits = _ExportDescrUnit.getUnits(); //_ExportDescrUnit.getUnits().stream().filter(u -> u.isCategoryCavalry()).collect(Collectors.toList());

		for (val unit : allUnits) {

			// ### Updating Charge ###
			unit.StatPri.ChargeBonus = (int)(unit.StatPri.ChargeBonus * ChargeMultiplier);

			if(powerfulChargeForHeavyInfantry) {
				// ### All Heavy Infantry gets powerful_charge attribute
				if(unit.isCategoryInfantry() && unit.isClassHeavy())
					unit.addAttribute("powerful_charge");
			}

			if(powerfulChargeForCavalry) {
				// ### Cavalry : ###
				int heavyCharge = (int)(8 * ChargeMultiplier);
				if(unit.isCategoryCavalry()) {
					// # Add powerful_charge for heavy chargers OR heavy units #
					if(unit.isClassHeavy() || unit.StatPri.ChargeBonus >= heavyCharge)
						unit.addAttribute("powerful_charge");
				}
			}
		}

	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		ParamIdDouble parDbl;
		parDbl= new ParamIdDouble("HitRatio", "Hit Ration",
				feature -> ((LongerBattles)feature).getMeleeHitRate(),
				(feature, value) -> ((LongerBattles)feature).setMeleeHitRate(value));
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
	private BattleConfig _BattleConfig;


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
