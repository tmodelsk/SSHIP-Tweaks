package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-24.
 */
public class ISettlementUnderSiege extends Condition {

	private String _SettlementName;


	@Override
	public String getString() {
		return "I_SettlementUnderSiege " + _SettlementName;
	}

	public ISettlementUnderSiege(String settlementName) {
		this._SettlementName = settlementName;
	}
}
