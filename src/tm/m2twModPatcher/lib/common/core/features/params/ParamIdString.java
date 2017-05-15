package tm.m2twModPatcher.lib.common.core.features.params;

/**
 * Created by tomek on 19.04.2017.
 */
public class ParamIdString extends ParamId<String> {

	@Override
	public Class<?> getInnerType() {
		return String.class;
	}

	public ParamIdString(String symbol, ValueGetterForParameter<String> paramValueGetterMethod, ValueSetterFromParameter<String> valueSetterFromParam) {
		super(symbol, paramValueGetterMethod, valueSetterFromParam);
	}

	public ParamIdString(String symbol, String name, ValueGetterForParameter<String> paramValueGetterMethod, ValueSetterFromParameter<String> valueSetterFromParam) {
		super(symbol, name, paramValueGetterMethod, valueSetterFromParam);
	}
}
