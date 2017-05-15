package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.various;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-17.
 */
public class CompareCounter extends Condition {

	private String _CounterName;

	private String _Operator;

	private int _Value;

	public CompareCounter(String _CounterName, String _Operator, int _Value) {
		this._CounterName = _CounterName;
		this._Operator = _Operator;
		this._Value = _Value;
	}

	@Override
	public String getString() {
		return "I_CompareCounter " +  _CounterName + " " + _Operator + " " + _Value;
	}
}
