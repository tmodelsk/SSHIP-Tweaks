package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.base;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/** General puprose not condition: not .... */
public class NotCondition extends Condition {

	private Condition condition;

	@Override
	public String getString() {
		return "not " + condition.getString();
	}


	public NotCondition(Condition condition) {
		this.condition = condition;
	}
}
