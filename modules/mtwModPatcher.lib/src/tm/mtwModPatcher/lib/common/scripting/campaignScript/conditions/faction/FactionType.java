package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-19.
 */
public class FactionType extends Condition {

	private String _FactionName;

	@Override
	public String getString() {
		return "FactionType " + _FactionName;
	}

	public FactionType(String factionName) {
		this._FactionName = factionName;
	}
}
