package tm.m2twModPatcher.lib.common.core.features.params;

/**
 * Created by tomek on 20.04.2017.
 */
public class ParamIdInteger extends ParamId<Integer> {


	@Override
	public Class<?> getInnerType() {
		return Integer.class;
	}

	public ParamIdInteger(String symbol, ValueGetterForParameter<Integer> valueGetterForParam, ValueSetterFromParameter<Integer> valueSetterFromParam) {
		super(symbol, valueGetterForParam, valueSetterFromParam);
	}

	public ParamIdInteger(String symbol, String name, ValueGetterForParameter<Integer> valueGetterForParam, ValueSetterFromParameter<Integer> valueSetterFromParam) {
		super(symbol, name, valueGetterForParam, valueSetterFromParam);
	}
}
