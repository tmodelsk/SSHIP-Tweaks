package tm.m2twModPatcher.lib.common.core.features.params;

import tm.m2twModPatcher.lib.common.core.features.Feature;

/**
 * Created by tomek on 19.04.2017.
 */
public interface ValueSetterFromParameter<T> {
	void setValueFromParam(Feature feature, T value);
}
