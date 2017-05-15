package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Test if this faction is excommunicated
 * http://www.twcenter.net/wiki/FactionExcommunicated_%28M2-Scripting%29
 */
public class FactionExcommunicated extends Condition {
	@Override
	public String getString() {
		return "FactionExcommunicated";
	}

	public FactionExcommunicated() {
	}
}
