package tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;

/**
 * Created by Tomek on 2016-11-19.
 */
public class SetVariable extends ScriptElement {

	private String _VariableName;
	private int _Value;

	@Override
	public String getString() {
		return "set_counter " + _VariableName + " "+_Value;
	}

	public SetVariable(String variableName, int value) {
		this._VariableName = variableName;
		this._Value = value;
	}
}
