package tm.mtwModPatcher.sship.features.buildings;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.ExportDescrCharacterTraits;

import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class StrategyDreadBuildingsDisable extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		exportDescrCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);

		val edct = exportDescrCharacterTraits;

		edct.setAffectTraitChanceInTrigger("building_gallows$", "StrategyDread", 1);
		edct.setAffectTraitChanceInTrigger("building_mines$", "StrategyDread", 1);
		edct.setAffectTraitChanceInTrigger("building_c_mines$", "StrategyDread", 1);
	}

	private ExportDescrCharacterTraits exportDescrCharacterTraits;

	@Override
	public UUID getId() {
		return Id;
	}
	public final static UUID Id = UUID.fromString("d9a19a18-197b-497b-8a7a-12d1b29676c3");


	public StrategyDreadBuildingsDisable() {
		setName("Dread on Buildings Disable");

		addCategory("Agents");

		setDescriptionShort("Disables StrategyDread Trait being affected on buildings contruction");
		setDescriptionUrl("http://tmsship.wikidot.com/strategy-dread-buildings-disable");
	}
}
