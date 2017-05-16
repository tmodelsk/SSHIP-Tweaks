package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Test if the settlement is the current crusade target
 * Trigger Requirements: settlement
 * http://www.twcenter.net/wiki/IsCrusadeTarget_(M2-Scripting)
 */
public class IsCrusadeTarget extends Condition {

	@Override
	public String getString() {
		return "IsCrusadeTarget";
	}

	public IsCrusadeTarget() {
	}
}
