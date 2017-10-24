package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 17.10.2017.
 */
public class FeatureParam {

	@Getter @Setter
	private String Symbol;


	@Getter
	private String ValueString;
	@Getter
	private Boolean ValueBoolean;
	@Getter
	private Integer ValueInteger;
	@Getter
	private Double ValueDouble;


	public void setValue(String value) {
		ValueString = value;
	}
	public void setValue(Boolean value) {
		ValueBoolean = value;
	}
	public void setValue(Integer value) {
		ValueInteger = value;
	}
	public void setValue(Double value) {
		ValueDouble = value;
	}
}
