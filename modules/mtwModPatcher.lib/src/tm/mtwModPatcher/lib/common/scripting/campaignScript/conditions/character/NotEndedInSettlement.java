package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character;

/**
 * Created by Tomek on 2016-11-22.
 */
public class NotEndedInSettlement extends EndedInSettlement {

	@Override
	public String getString() {
		return "not " + super.getString();
	}
}
