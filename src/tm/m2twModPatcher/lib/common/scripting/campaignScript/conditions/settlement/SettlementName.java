package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-24.
 */
public class SettlementName extends Condition {

	private String _SettlementName;

	@Override
	public String getString() {
		return "SettlementName " + _SettlementName;
	}

	public SettlementName(String settlementName) {
		this._SettlementName = settlementName;
	}
}
