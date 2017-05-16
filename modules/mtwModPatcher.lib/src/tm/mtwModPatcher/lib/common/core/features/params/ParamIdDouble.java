package tm.mtwModPatcher.lib.common.core.features.params;

/**
 * Created by tomek on 19.04.2017.
 */
public class ParamIdDouble extends ParamId<Double> {

	@Override
	public Class<?> getInnerType() {
		return Double.class;
	}

	public ParamIdDouble(String symbol, ValueGetterForParameter<Double> paramValueGetterMethod, ValueSetterFromParameter<Double> valueSetterFromParam) {
		super(symbol, paramValueGetterMethod, valueSetterFromParam);
	}

	public ParamIdDouble(String symbol, String name, ValueGetterForParameter<Double> paramValueGetterMethod, ValueSetterFromParameter<Double> valueSetterFromParam) {
		super(symbol, name, paramValueGetterMethod, valueSetterFromParam);
	}
}
