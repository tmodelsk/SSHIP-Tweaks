package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.character;

/**
 * Created by Tomek on 2016-11-23.
 */
public class NotTraitCondition extends TraitCondition {

	@Override
	public String getString() {
		return "not " + super.getString();
	}

	public NotTraitCondition(String _TraitName, String _Operator, int _Level) {
		super(_TraitName, _Operator, _Level);
	}
}
