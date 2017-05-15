package tm.m2twModPatcher.lib.common.core.features.params;

/**
 * Created by tomek on 19.04.2017.
 */
public class ParamIdBoolean extends ParamId<Boolean> {

	@Override
	public Class<?> getInnerType() {
		return Boolean.class;
	}

	public ParamIdBoolean(String symbol, ValueGetterForParameter<Boolean> paramValueGetterMethod, ValueSetterFromParameter<Boolean> valueSetterFromParam) {
		super(symbol, paramValueGetterMethod, valueSetterFromParam);
	}

	public ParamIdBoolean(String symbol, String name, ValueGetterForParameter<Boolean> paramValueGetterMethod, ValueSetterFromParameter<Boolean> valueSetterFromParam) {
		super(symbol, name, paramValueGetterMethod, valueSetterFromParam);
	}
}
