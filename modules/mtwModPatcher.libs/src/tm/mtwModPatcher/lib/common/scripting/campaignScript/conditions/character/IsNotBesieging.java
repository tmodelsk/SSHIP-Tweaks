package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character;

/**
 * Created by Tomek on 2016-11-18.
 */
public class IsNotBesieging extends IsBesieging {

	public String getString() {
		return "not " + super.getString();
	}
}
