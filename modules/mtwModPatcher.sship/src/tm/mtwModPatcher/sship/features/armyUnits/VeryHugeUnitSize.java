package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.managers.UnitsManager;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tomek on 22.06.2017.
 */
public class VeryHugeUnitSize extends Feature {

	@Getter
	private double sizeMulti = 0.75; //0.75; //1.25;
	public void setSizeMulti(double value) {
		if(value > 1.25) throw new PatcherLibBaseEx("Size Multiplier max value is 1.25");

		sizeMulti = value;
	}

	@Getter
	private double baseCostMulti = 0.80; //1.25;
	public void setBaseCostMulti(double value) {
		if(value > 1.25) throw new PatcherLibBaseEx("Size Multiplier max value is 1.25");

		baseCostMulti = value;
	}

	@Getter @Setter
	private boolean updateCosts = ConfigurationSettings.isDevEnvironment();
	@Getter @Setter
	private boolean updateReplenish = ConfigurationSettings.isDevEnvironment();

	private static final int SIZE_MAX = 100;

	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);

		val sorted =  edu.getUnits().stream().sorted(Comparator.comparingInt(o -> o.Soldier.NumberOfMen)).collect(Collectors.toList());

		for(val unit : sorted) {
			if(unit.Soldier.NumberOfMen * sizeMulti > SIZE_MAX) {
				unit.Soldier.NumberOfMen = SIZE_MAX;

				unit.StatPri.ChargeBonus++;
				unit.StatPri.Damage++;
				unit.StatPriArmour.Armour++;
			} else {
				unit.Soldier.NumberOfMen *= sizeMulti;
				if(unit.Soldier.SpecialNumber > 0 && !unit.Name.equals("Great Cross")) {
					int special = unit.Soldier.SpecialNumber;
					special = (int) (special*sizeMulti + 1);

					unit.Soldier.SpecialNumber = special;
				}
			}

			if(updateCosts) {
				unit.StatCost.Cost *= ( sizeMulti / baseCostMulti );
				unit.StatCost.Upkeep *= ( sizeMulti / baseCostMulti );
			}
		}

		if(updateReplenish) {
			val unitMan = new UnitsManager();
			val replenishMult = 1 / sizeMulti;
			unitMan.updateAllReplenish(null, replenishMult, null, edb);
		}
	}

	@Override
	public ListUnique<ParamId> defineParamsIds()  {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdDouble("BaseCostMulti" , "Base Cost Multi",
				feature -> ((VeryHugeUnitSize)feature).getBaseCostMulti(),
				(feature, value) -> ((VeryHugeUnitSize)feature).setBaseCostMulti(value)));

		pars.add(new ParamIdDouble("SizeMulti" , "Size Multi",
				feature -> ((VeryHugeUnitSize)feature).getSizeMulti(),
				(feature, value) -> ((VeryHugeUnitSize)feature).setSizeMulti(value)));

		pars.add(new ParamIdBoolean("UpdateCosts" , "Update Costs",
				feature -> ((VeryHugeUnitSize)feature).isUpdateCosts(),
				(feature, value) -> ((VeryHugeUnitSize)feature).setUpdateCosts(value)));

		pars.add(new ParamIdBoolean("UpdateReplenish" , "Update Replenish Rates",
				feature -> ((VeryHugeUnitSize)feature).isUpdateReplenish(),
				(feature, value) -> ((VeryHugeUnitSize)feature).setUpdateReplenish(value)));

		return pars;
	}

	public VeryHugeUnitSize() {
		super("Very Huge Unit Size");

		addCategory("Units");

		setDescriptionShort("Very Huge Unit Size, number of men * 125%");
		setDescriptionUrl("http://tmsship.wikidot.com/very-huge-unit-size");

		if(ConfigurationSettings.isDevEnvironment())
			sizeMulti = 0.85;
	}

	private ExportDescrUnitTyped edu;
	private ExportDescrBuilding edb;


	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("");
}
