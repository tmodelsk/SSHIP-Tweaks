package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.data._root.ExportDescrCharacterTraits;

import java.util.UUID;

public class BattleDreadLowered extends Feature {

	@Override
	public void setParamsCustomValues() {
		multi = 0.75;
	}

	@Override
	public void executeUpdates() throws Exception {
		exportDescrCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);
		val edct = exportDescrCharacterTraits;

		edct.multiplyAffectTraitChanceInTrigger("prisoners_ransomed2PL", "BattleDread", multi);
		edct.multiplyAffectTraitChanceInTrigger("prisoners_ransomed2AI", "BattleDread", multi);

		edct.multiplyAffectTraitChanceInTrigger("battle_BattleChivalry_ChaseRouters", "BattleDread", multi);

		edct.multiplyAffectTraitChanceInTrigger("battle3Chivalry1_UnfairBattle", "BattleDread", multi);
		edct.multiplyAffectTraitChanceInTrigger("battle3Dread_TotalAnnihilation", "BattleDread", multi);

		edct.multiplyAffectTraitChanceInTrigger("Worth_of_Adoption_chiv_2", "BattleDread", multi);
		edct.multiplyAffectTraitChanceInTrigger("Worth_of_Adoption_chiv_3", "BattleDread", multi);
		edct.multiplyAffectTraitChanceInTrigger("Lesser_General_Adoption_18_VnV_Trigger", "BattleDread", multi);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdDouble("Multiplier", "Multiplier",
				f -> ((BattleDreadLowered)f).getMulti(), (f,value) -> ((BattleDreadLowered)f).setMulti(value) ));

		return pars;
	}

	@Getter @Setter private double multi;
	private ExportDescrCharacterTraits exportDescrCharacterTraits;

	@Override
	public UUID getId() {
		return Id;
	}
	public final static UUID Id = UUID.fromString("ec755155-e6d9-4633-bba7-75a0fd229d84");

	public BattleDreadLowered() {
		setName("Battle Dread Lowered");

		addCategory(CATEGORY_BATTLE);
		addCategory(CATEGORY_AGENTS);

		setDescriptionShort("In general, chances for BattleDread additional Levels are lowered by multiplier % for triggers.");
		setDescriptionUrl("http://tmsship.wikidot.com/battle-dread-lowered");
	}
}
