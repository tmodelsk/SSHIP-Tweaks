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

/**  */
public class SettlementUnrestLowered extends Feature {

	@Override
	public void setParamsCustomValues() {
		orderSqualorValue = 0.225;    // 0.225	// 0.20  0.18 0.29		 org 0.45
		orderReligiousUnrestValue = 1.5;    // 2.3 , 1.5	org 0.7
		orderGarrisonValue = 5.0;        // 4.0	org 2.0
	}

	@Override
	public void executeUpdates() throws Exception {
		descrSettlementMechanics = getFileRegisterForUpdated(DescrSettlementMechanics.class);

		// ### ORDER MODIFICATIONS ###
		descrSettlementMechanics.setAttribute("/root/factor_modifiers/factor[@name='SOF_SQUALOUR']/pip_modifier", "value", orderSqualorValue);
		descrSettlementMechanics.setAttribute("/root/factor_modifiers/factor[@name='SOF_RELIGIOUS_UNREST']/pip_modifier", "value", orderReligiousUnrestValue);
		descrSettlementMechanics.setAttribute("/root/factor_modifiers/factor[@name='SOF_GARRISON']/pip_modifier", "value", orderGarrisonValue);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val params = new ArrayUniqueList<ParamId>();

		params.add(new ParamIdDouble("OrderSqualorValue", "Order Squalor Value",
				feature -> ((SettlementUnrestLowered) feature).getOrderSqualorValue(),
				(feature, value) -> ((SettlementUnrestLowered) feature).setOrderSqualorValue(value)));

		params.add(new ParamIdDouble("OrderReligiousUnrestValue", "Order Religious Unrest Value",
				feature -> ((SettlementUnrestLowered) feature).getOrderReligiousUnrestValue(),
				(feature, value) -> ((SettlementUnrestLowered) feature).setOrderReligiousUnrestValue(value)));

		params.add(new ParamIdDouble("OrderGarrisonValue", "Order Garrison Value",
				feature -> ((SettlementUnrestLowered) feature).getOrderGarrisonValue(),
				(feature, value) -> ((SettlementUnrestLowered) feature).setOrderGarrisonValue(value)));

		return params;
	}

	@Getter @Setter private double orderSqualorValue;
	@Getter @Setter private double orderReligiousUnrestValue;
	@Getter @Setter private double orderGarrisonValue;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("aef3b392-e7e0-41d2-9baf-94becd4257bf");

	private DescrSettlementMechanics descrSettlementMechanics;

	public SettlementUnrestLowered() {

		setName("Settlement Unrest Lowered");

		addCategory("Economy");
		addCategory("Campaign");

		setDescriptionShort("Settlement Unrest level is lowered");
		setDescriptionUrl("http://tmsship.wikidot.com/settlement-unrest-lowered");
	}
}
