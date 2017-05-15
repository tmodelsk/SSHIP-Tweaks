package tm.m2twModPatcher.lib.common.core.features.params;

/**
 * Created by tomek on 19.04.2017.
 */
public class ParamValueString extends ParamValue<String> {


	@Override
	public String getValueAsString() {
		return getValue();
	}

	@Override
	public void setValueStr(String strValue) {
		setValue(strValue);
	}

	public ParamValueString(ParamId<String> paramId) {
		super(paramId);
	}

	public ParamValueString(ParamId<String> paramId, String value) {
		super(paramId, value);
	}
}
