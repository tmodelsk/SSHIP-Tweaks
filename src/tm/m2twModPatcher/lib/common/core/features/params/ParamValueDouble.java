package tm.m2twModPatcher.lib.common.core.features.params;

import lombok.val;

/**
 * Created by tomek on 19.04.2017.
 */
public class ParamValueDouble extends ParamValue<Double> {


	@Override
	public String getValueAsString() {
		return getValue().toString();
	}

	@Override
	public void setValueStr(String strValue) {
		val dbl = Double.parseDouble(strValue);
		setValue(dbl);
	}

	public ParamValueDouble(ParamId<Double> paramId) {
		super(paramId);
	}

	public ParamValueDouble(ParamId<Double> paramId, Double value) {
		super(paramId, value);
	}
}
