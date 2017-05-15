package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-27.
 */
public class NumberOfSettlements extends Condition {

	private String _FactionName;
	private String _Operator;
	private int _Value;

	@Override
	public String getString() {
		return "I_NumberOfSettlements "+ _FactionName +" "+ _Operator +" "+_Value;
	}

	public NumberOfSettlements(String factionName, String operator, int value) {
		this._FactionName = factionName;
		this._Operator = operator;
		this._Value = value;
	}
}
