package tm.mtwModPatcher.lib.common.core.features.params;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.val;
import tm.common.Ctm;

public abstract class ParamId<T> {

	@Getter
	private ValueGetterForParameter<T> valueGetterForParam;

	@Getter
	private ValueSetterFromParameter<T> valueSetterFromParam;

	@Getter
	private String symbol;

	@Getter
	private String name;

	public abstract Class<?> getInnerType();

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;

		val typed = Ctm.as(ParamId.class, obj);
		if(typed == null) return false;

		return symbol.equals(typed.symbol) && this.getClass() == typed.getClass();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(symbol, getInnerType());
	}

	public ParamId(String symbol, ValueGetterForParameter<T> valueGetterForParam, ValueSetterFromParameter<T> valueSetterFromParam) {
		this.symbol = symbol;
		this.valueGetterForParam = valueGetterForParam;
		this.valueSetterFromParam = valueSetterFromParam;
	}

	public ParamId(String symbol, String name, ValueGetterForParameter<T> valueGetterForParam, ValueSetterFromParameter<T> valueSetterFromParam) {
		this.symbol = symbol;
		this.name = name;
		this.valueGetterForParam = valueGetterForParam;
		this.valueSetterFromParam = valueSetterFromParam;
	}
}
