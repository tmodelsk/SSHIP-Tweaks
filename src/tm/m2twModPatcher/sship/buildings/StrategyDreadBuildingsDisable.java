package tm.m2twModPatcher.sship.buildings;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.ExportDescrCharacterTraits;

import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class StrategyDreadBuildingsDisable extends Feature {

	private ExportDescrCharacterTraits exportDescrCharacterTraits;

	@Override
	public void executeUpdates() throws Exception {
		exportDescrCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);

		val edct = exportDescrCharacterTraits;

		edct.setAffectTraitChanceInTrigger("building_gallows$", "StrategyDread", 1);
		edct.setAffectTraitChanceInTrigger("building_mines$", "StrategyDread", 1);
		edct.setAffectTraitChanceInTrigger("building_c_mines$", "StrategyDread", 1);
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public final static UUID Id = UUID.randomUUID();


	public StrategyDreadBuildingsDisable() {
		setName("Dread on Buildings Disable");

		addCategory("Agents");

		setDescriptionShort("Disables StrategyDread Trait being affected on buildings contruction");
		setDescriptionUrl("http://tmsship.wikidot.com/strategy-dread-buildings-disable");
	}
}
