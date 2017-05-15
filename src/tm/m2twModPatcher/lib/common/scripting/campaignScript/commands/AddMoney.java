package tm.m2twModPatcher.lib.common.scripting.campaignScript.commands;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;

/**
 * Created by Tomek on 2016-11-19.
 */
public class AddMoney extends ScriptElement {

	protected int _Value;
	protected String _FactionName = null;

	public AddMoney(String factionName , int value) {
		this._Value = value;
		this._FactionName = factionName;
	}

	@Override
	public String getString() {

		String str = "add_money ";

		if(_FactionName != null)
			str += _FactionName + " ";

		str += _Value;

		return str;
	}
}
