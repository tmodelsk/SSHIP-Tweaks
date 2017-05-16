package tm.m2twModPatcher.sship.armyUnits;

import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.entities.UnitReplenishRate;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Archers Levy Boost - makes Peasant Archers more usefull
 */
public class ArchersLevyBoost extends Feature {
	@Override
	public void executeUpdates() throws Exception {
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		val peasantArrowArchers = exportDescrUnit.getUnits().stream()
				.filter(u -> u.StatPri.IsMissleUnit() && u.StatPri.Ammunition.equals("peasant_arrow"))
				.collect(Collectors.toList());

		val unitNames = new ArrayUniqueList<String>();

		for (val unit: peasantArrowArchers) {
			unit.StatPri.Damage++;
			unit.StatPri.MissleRange *= 0.8;
			unit.RecruitPriorityOffset = 5;

			unitNames.add(unit.Name);
		}

		unitNames.forEach(unitName ->
			exportDescrBuilding.updateUnitReplenishRates(unitName, UnitReplenishRate.R1, 2));
	}

	protected ExportDescrUnitTyped exportDescrUnit;
	protected ExportDescrBuilding exportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public ArchersLevyBoost() {
		setName("Archers - Levy/Peasant archers boost");

		addCategory("Units");
		addCategory("Missile");

		setDescriptionShort("All Archers using peasant_arrow = (Levy / Peasant Archers) are more usefull");
		setDescriptionUrl("http://tmsship.wikidot.com/archers-levy-boost");
	}
}
