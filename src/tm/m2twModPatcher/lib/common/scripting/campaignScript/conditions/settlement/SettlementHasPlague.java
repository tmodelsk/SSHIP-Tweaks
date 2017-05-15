package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 *  Is the settlement plague-ridden?
 *  Trigger Requirements: settlement
 *  http://www.twcenter.net/wiki/SettlementHasPlague_(M2-Scripting)
 */
public class SettlementHasPlague extends Condition {
	@Override
	public String getString() {
		return "SettlementHasPlague";
	}
}
