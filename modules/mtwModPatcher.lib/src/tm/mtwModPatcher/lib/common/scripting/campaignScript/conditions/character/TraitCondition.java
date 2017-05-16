package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.base.CharacterCondition;

/**
 * Created by Tomek on 2016-11-18.
 */
public class TraitCondition extends CharacterCondition {

	private String _TraitName;
	private String _Operator;
	private int _Level;
	@Override
	public String getString() {
		return "Trait " + _TraitName + " " + _Operator + " " + _Level;
	}


	public TraitCondition(String _TraitName, String _Operator, int _Level) {
		this._TraitName = _TraitName;
		this._Operator = _Operator;
		this._Level = _Level;
	}
}
