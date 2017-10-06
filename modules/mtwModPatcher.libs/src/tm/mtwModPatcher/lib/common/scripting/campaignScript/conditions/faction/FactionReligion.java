package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import lombok.Getter;
import lombok.Setter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by tomek on 26.04.2017.
 */
public class FactionReligion extends Condition {

	@Getter @Setter
	private String religion;

	@Override
	public String getString() {
		return "FactionReligion " + religion;
	}

	public FactionReligion(String religion) {
		this.religion = religion;
	}

	public FactionReligion() {
	}
}
