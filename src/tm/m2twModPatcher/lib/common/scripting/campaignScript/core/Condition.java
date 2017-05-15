package tm.m2twModPatcher.lib.common.scripting.campaignScript.core;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.base.NotCondition;

/**
 * Created by Tomek on 2016-11-17.
 */
public abstract class Condition extends ScriptElement {

	public Condition not() {
		return new NotCondition(this);
	}


	public Condition() {
	}
}
