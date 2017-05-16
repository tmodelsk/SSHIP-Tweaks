package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-19.
 */
public class TurnNumber extends Condition {

	private String _Operator;
	private int _Value;

	@Override
	public String getString() {
		return "I_TurnNumber " + _Operator + " " + _Value;
	}

	public TurnNumber(String _Operator, int _Value) {
		this._Operator = _Operator;
		this._Value = _Value;
	}
}
