package tm.m2twModPatcher.sship.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.params.ParamId;
import tm.m2twModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.m2twModPatcher.lib.fileEntities.data.DescrSettlementMechanics;

import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class WorldEconomyScaling extends Feature {

	@Getter @Setter
	private double economyMulti = 1.075;	// 1.1


	@Override
	public void executeUpdates() throws Exception {
		descrSettlementMechanics = getFileRegisterForUpdated(DescrSettlementMechanics.class);

		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_FARMS']/pip_modifier", "value", economyMulti);
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TAXES']/pip_modifier", "value", economyMulti);
		// ## Boost MINING income ##
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_MINING']/pip_modifier", "value", economyMulti);
		// ## Boost TRADE income - to equalize removed mwechants ##
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

	private DescrSettlementMechanics descrSettlementMechanics;

	@Override
	public UUID getId() {
		return Id;
	}

	public final static UUID Id = UUID.randomUUID();

	public WorldEconomyScaling() {
		setName("World Economy Scaling");

		addCategory("Economy");

		setDescriptionShort("World Economy Scaling up (more income) or down (less income), parametrized.");
		setDescriptionUrl("http://tmsship.wikidot.com/world-economy-scaling");
	}
}
