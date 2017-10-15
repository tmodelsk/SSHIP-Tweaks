package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

import java.util.List;

/**
 * Created by tomek on 10.10.2017.
 */
public class IsRegionOneOf extends Condition {

	@Getter @Setter
	private String provinceNames;

	@Override
	public String getString() {
		return "IsRegionOneOf "+provinceNames;
	}
	public IsRegionOneOf(List<String> provinceNames) {
		val sb = new StringBuilder();

		for (val provinceName: provinceNames ) {
			sb.append(provinceName);
			sb.append(" ");
		}

		this.provinceNames = sb.toString();
	}


	public IsRegionOneOf(String provinceNames) {
		this.provinceNames = provinceNames;
	}
}
