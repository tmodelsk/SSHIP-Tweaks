package tm.m2twModPatcher.lib.common.scripting.campaignScript.commands;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Command;

/**
 * Created by Tomek on 2016-11-21.
 */
public class IncrementKingsPurse extends Command {

	private String _FactionName;
	private int _Value;

	@Override
	public String getString() {
		return "increment_kings_purse "+ _FactionName +" " + _Value;
	}

	public IncrementKingsPurse(String factionName, int value) {
		this._FactionName = factionName;
		this._Value = value;
	}
}
