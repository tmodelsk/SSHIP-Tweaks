package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-24.
 */
public class IsLocalFaction extends Condition {

	private String _FactionName;

	@Override
	public String getString() {
		return "I_LocalFaction " + _FactionName;
	}

	public IsLocalFaction(String factionName) {
		this._FactionName = factionName;
	}
}
