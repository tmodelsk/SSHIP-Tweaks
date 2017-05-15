package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Test if the settlement is the current jihad target
 * Trigger Requirements: settlement
 * http://www.twcenter.net/wiki/IsJihadTarget_(M2-Scripting)
 */
public class IsJihadTarget extends Condition {
	@Override
	public String getString() {
		return "IsJihadTarget";
	}

	public IsJihadTarget() {
	}
}
