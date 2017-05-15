package tm.m2twModPatcher.lib.common.core.features.params;

import lombok.Getter;
import lombok.Setter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tm.m2twModPatcher.lib.common.core.features.Feature;

/**
 * Created by tomek on 19.04.2017.
 */
public abstract class ParamValue<T> {

	@Getter
	private ParamId<T> paramId;

	@Getter @Setter
	private T value;

	public Object getValueAsObj() {
		return value;
	}
	public abstract String getValueAsString();
	public Double getValueAsDouble() {
		throw new NotImplementedException();
	}
	public Boolean getValueAsBoolean() {
		throw new NotImplementedException();
	}

	public abstract void setValueStr(String strValue);

	public void reloadValueFromFeature(Feature feature) {
		setValue( paramId.getValueGetterForParam().getValueForParam(feature) );
	}
	public void saveValueToFeature(Feature feature) {
		paramId.getValueSetterFromParam().setValueFromParam(feature, value);
	}

	public String getName() {
		return paramId.getName();
	}

	public Class<?> getInnerType() {
		return paramId.getInnerType();
	}

	@Override
	public boolean equals(Object obj) {
		return paramId.equals(obj);
	}

	@Override
	public int hashCode() {
		return paramId.hashCode();
	}

	public ParamValue(ParamId<T> paramId) {
		this.paramId = paramId;
	}

	public ParamValue(ParamId<T> paramId, T value) {
		this.paramId = paramId;
		this.value = value;
	}
}
