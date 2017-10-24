package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;

import java.util.UUID;
import java.util.stream.Collectors;

/** Boosts cavalry movement speed  */
public class CavalrySpeedBonus extends Feature {

	@Override
	public void setParamsCustomValues() {
		cavalrySpeedMulti = 1.10;
	}

	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		val cavUnits = edu.getUnits().stream().filter(u -> u.isCategoryCavalry()).collect(Collectors.toList());

		for (val unit : cavUnits) {
			// # Speed Up cavalry unit a bit
			unit.multiplyMoveSpeedMod(cavalrySpeedMulti);
		}
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		val paramCavSpeedMulti = new ParamIdDouble("CavalrySpeedMulti", "Cavalry Speed Multiplier", (f) -> {
			CavalrySpeedBonus thisFeature = (CavalrySpeedBonus)f;
			return thisFeature.getCavalrySpeedMulti();
		}, (Feature f, Double value) -> {
			CavalrySpeedBonus thisFeature = (CavalrySpeedBonus)f;
			thisFeature.setCavalrySpeedMulti(value);
		} );

		parIds.add(paramCavSpeedMulti);

		return parIds;
	}

	@Getter @Setter
	private double cavalrySpeedMulti;

	private ExportDescrUnitTyped edu;

	public CavalrySpeedBonus() {
		super("Cavalry Speed Bonus");

		addCategory("Battle");
		addCategory("Units");

		setDescriptionShort("All Cavalry units gain small movement speed bonus");
		setDescriptionUrl("http://tmsship.wikidot.com/cavalry-speed-bonus");

	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("046ffa20-534e-4418-ab08-27c3bdc3d618");
}
