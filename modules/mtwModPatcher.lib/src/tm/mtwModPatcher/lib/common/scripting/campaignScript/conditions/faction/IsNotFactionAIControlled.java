package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction;

/**
 * Created by Tomek on 2016-11-19.
 */
public class IsNotFactionAIControlled extends IsFactionAIControlled {

	@Override
	public String getString() {
		return "not " + super.getString();
	}
}
