package tm.mtwModPatcher.sship.features.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.data._root.DescrSettlementMechanics;

import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class WorldEconomyScaling extends Feature {

	@Override
	public void setParamsCustomValues() {
		economyMulti = 1.025;	// 1.075  1.1
	}

	@Override
	public void executeUpdates() throws Exception {
		descrSettlementMechanics = getFileRegisterForUpdated(DescrSettlementMechanics.class);

		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_FARMS']/pip_modifier", "value", economyMulti);
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TAXES']/pip_modifier", "value", economyMulti);
		// ## MINING income ##
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_MINING']/pip_modifier", "value", economyMulti);
		// ## TRADE income ##
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TRADE']/pip_modifier", "value", economyMulti);
		// ## SIF_BUILDINGS - any bonus given from EDB (1 per building)
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_BUILDINGS']/pip_modifier", "value", economyMulti);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val params = new ArrayUniqueList<ParamId>();

		params.add(new ParamIdDouble("EconomyMultiplier", "Economy Multiplier",
				feature -> ((WorldEconomyScaling)feature).getEconomyMulti(),
				(feature, value) -> ((WorldEconomyScaling)feature).setEconomyMulti(value)));

		return params;
	}

	@Getter @Setter private double economyMulti;

	private DescrSettlementMechanics descrSettlementMechanics;

	@Override
	public UUID getId() {
		return Id;
	}
	public final static UUID Id = UUID.fromString("22e5a288-548f-4217-8ab5-45d288e2b0b9");

	public WorldEconomyScaling() {
		setName("World Economy Scaling");

		addCategory("Economy");

		setDescriptionShort("World Economy Scaling up (more income) or down (less income), parametrized.");
		setDescriptionUrl("http://tmsship.wikidot.com/world-economy-scaling");
	}
}
