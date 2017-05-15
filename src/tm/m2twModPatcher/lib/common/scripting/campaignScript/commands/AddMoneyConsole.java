package tm.m2twModPatcher.lib.common.scripting.campaignScript.commands;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Command;

/**
 * Created by Tomek on 2016-11-17.
 */
public class AddMoneyConsole extends Command {

	protected int _Value;
	protected String _FactionName = null;

	public AddMoneyConsole(String _FactionName , int _Value) {
		this._Value = _Value;
		this._FactionName = _FactionName;
	}

	public AddMoneyConsole(int _Value) {
		this._Value = _Value;
		_FactionName = null;
	}

	@Override
	public String getString() {

		String str = "console_command add_money ";

		if(_FactionName != null)
			str += _FactionName + ", ";

		str += _Value;

		return str;
	}
}
