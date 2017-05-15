package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.character.IsBesieging;

/**
 * Created by Tomek on 2016-11-18.
 */
public class IsNotBesieging extends IsBesieging {

	public String getString() {
		return "not " + super.getString();
	}
}
