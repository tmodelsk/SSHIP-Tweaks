package tm.mtwModPatcher.lib.common.core.features.params;

import lombok.val;

/**
 * Created by tomek on 20.04.2017.
 */
public class ParamValueBoolean extends ParamValue<Boolean> {


	@Override
	public String getValueAsString() {
		return getValue().toString();
	}

	@Override
	public void setValueStr(String strValue) {
		val bool = Boolean.parseBoolean(strValue);
		setValue(bool);
	}

	public ParamValueBoolean(ParamId<Boolean> paramId) {
		super(paramId);
	}

	public ParamValueBoolean(ParamId<Boolean> paramId, Boolean value) {
		super(paramId, value);
	}
}
