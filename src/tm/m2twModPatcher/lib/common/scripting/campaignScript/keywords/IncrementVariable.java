package tm.m2twModPatcher.lib.common.scripting.campaignScript.keywords;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;

/**
 * Created by Tomek on 2016-11-19.
 */
public class IncrementVariable extends ScriptElement {

	private String _VariableName;
	private int _Value;

	@Override
	public String getString() {
		return "inc_counter "+ _VariableName +" " + _Value;
	}

	public IncrementVariable(String variableName, int value) {
		this._VariableName = variableName;
		this._Value = value;
	}
}
