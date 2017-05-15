package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Is the settlement rioting at the moment?
 * Trigger Requirements: settlement
 * http://www.twcenter.net/wiki/IsSettlementRioting_(M2-Scripting)
 */
public class IsSettlementRioting extends Condition {
	@Override
	public String getString() {
		return "IsSettlementRioting";
	}
}
