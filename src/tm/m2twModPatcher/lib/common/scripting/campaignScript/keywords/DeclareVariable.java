package tm.m2twModPatcher.lib.common.scripting.campaignScript.keywords;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;

/**
 * Created by Tomek on 2016-11-19.
 */
public class DeclareVariable extends ScriptElement {

	private String _VariableName;

	@Override
	public String getString() {
		return "declare_counter " + _VariableName;
	}

	public DeclareVariable(String variableName) {
		this._VariableName = variableName;
	}
}
