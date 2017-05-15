package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-24.
 */
public class IsTargetRegionOneOf extends Condition {

	private String _ProvinceName;

	@Override
	public String getString() {
		return "IsTargetRegionOneOf "+_ProvinceName;
	}

	public IsTargetRegionOneOf(String provinceName) {
		this._ProvinceName = provinceName;
	}
}
