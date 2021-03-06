package tm.mtwModPatcher.lib.common.scripting.campaignScript.core;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.base.NotCondition;

/**
 * Created by Tomek on 2016-11-17.
 */
public abstract class Condition extends ScriptElement {

	/** Negates base condition */
	public Condition not() {
		return new NotCondition(this);
	}


	public Condition() {
	}
}
