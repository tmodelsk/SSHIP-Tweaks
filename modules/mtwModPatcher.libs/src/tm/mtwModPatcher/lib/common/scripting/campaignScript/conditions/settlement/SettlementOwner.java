package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-24.
 */
public class SettlementOwner extends Condition {

	private String _SettlementName;
	private String _FactionName;

	@Override
	public String getString() {
		return "I_SettlementOwner " + _SettlementName + " = " + _FactionName;
	}

	public SettlementOwner(String settlementName, String factionName) {
		this._SettlementName = settlementName;
		this._FactionName = factionName;
	}
}
