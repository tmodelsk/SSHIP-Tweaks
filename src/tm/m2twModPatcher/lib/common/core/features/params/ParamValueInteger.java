package tm.m2twModPatcher.lib.common.core.features.params;

import lombok.val;

/**
 * Created by tomek on 20.04.2017.
 */
public class ParamValueInteger extends ParamValue<Integer> {


	@Override
	public String getValueAsString() {
		return getValue().toString();
	}

	@Override
	public void setValueStr(String strValue) {
		val intVal = Integer.parseInt(strValue);
		setValue(intVal);
	}

	public ParamValueInteger(ParamId<Integer> paramId) {
		super(paramId);
	}

	public ParamValueInteger(ParamId<Integer> paramId, Integer value) {
		super(paramId, value);
	}
}
