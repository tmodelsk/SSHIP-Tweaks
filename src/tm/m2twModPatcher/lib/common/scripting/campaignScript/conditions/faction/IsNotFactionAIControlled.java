package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.faction.IsFactionAIControlled;

/**
 * Created by Tomek on 2016-11-19.
 */
public class IsNotFactionAIControlled extends IsFactionAIControlled {

	@Override
	public String getString() {
		return "not " + super.getString();
	}
}
