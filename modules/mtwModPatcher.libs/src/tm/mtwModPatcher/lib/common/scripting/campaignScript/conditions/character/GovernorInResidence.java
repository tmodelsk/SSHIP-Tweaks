package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Does the settlement lack a governor?
 *vTrigger Requirements: settlement
 * http://www.twcenter.net/wiki/GovernorInResidence_(M2-Scripting)
 */
public class GovernorInResidence extends Condition {
	@Override
	public String getString() {
		return "GovernorInResidence";
	}
}
