package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.ExportDescrCharacterTraits;

import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class BattleDreadLowered extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		exportDescrCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);
		val edct = exportDescrCharacterTraits;

		edct.multiplyAffectTraitChanceInTrigger("prisoners_ransomed2PL", "BattleDread", 0.50);
		edct.multiplyAffectTraitChanceInTrigger("prisoners_ransomed2AI", "BattleDread", 0.50);

		edct.multiplyAffectTraitChanceInTrigger("battle_BattleChivalry_ChaseRouters", "BattleDread", 0.50);

		edct.multiplyAffectTraitChanceInTrigger("battle3Chivalry1_UnfairBattle", "BattleDread", 0.50);
		edct.multiplyAffectTraitChanceInTrigger("battle3Dread_TotalAnnihilation", "BattleDread", 0.50);


		edct.multiplyAffectTraitChanceInTrigger("Worth_of_Adoption_chiv_2", "BattleDread", 0.50);
		edct.multiplyAffectTraitChanceInTrigger("Worth_of_Adoption_chiv_3", "BattleDread", 0.50);
		edct.multiplyAffectTraitChanceInTrigger("Lesser_General_Adoption_18_VnV_Trigger", "BattleDread", 0.50);
	}


	protected ExportDescrCharacterTraits exportDescrCharacterTraits;

	@Override
	public UUID getId() {
		return Id;
	}
	public final static UUID Id = UUID.fromString("ec755155-e6d9-4633-bba7-75a0fd229d84");

	public BattleDreadLowered() {
		setName("Battle Dread Lowered");

		addCategory("Battle");
		addCategory("Agents");

		setDescriptionShort("In general, chances for BattleDread additional Levels are lowered by 50% for triggers:");
		setDescriptionUrl("http://tmsship.wikidot.com/battle-dread-lowered");
	}
}
