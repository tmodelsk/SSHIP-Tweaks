package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.base.CharacterCondition;

/**
 * Created by Tomek on 2016-11-19.
 */
public class PercentageUnitAttribute extends CharacterCondition {

	private String _UnitType;
	private String _Operator;
	private double _Value;



	@Override
	public String getString() {
		return "PercentageUnitAttribute "+_UnitType + " " + _Operator + " " + _Value;
	}

	public PercentageUnitAttribute(String _UnitType, String _Operator, double _Value) {
		this._UnitType = _UnitType;
		this._Operator = _Operator;
		this._Value = _Value;
	}
}
