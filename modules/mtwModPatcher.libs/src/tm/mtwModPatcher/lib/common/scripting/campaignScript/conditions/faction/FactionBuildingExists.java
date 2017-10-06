package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import lombok.Getter;
import lombok.Setter;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/** http://www.twcenter.net/wiki/FactionBuildingExists_(M2-Scripting) */
public class FactionBuildingExists extends Condition {

	@Getter @Setter
	private String buildingName;

	@Getter @Setter
	private String operator;

	@Override
	public String getString() {
		return Ctm.msgFormat("FactionBuildingExists {0} {1} ", operator, buildingName);
	}

	public FactionBuildingExists(String buildingName) {
		this(buildingName, "=");
	}

	public FactionBuildingExists(String buildingName, String operator) {
		this.buildingName = buildingName;

		// currently only works with =, >= and >
		if(!operator.equals("=") && !operator.equals(">=") && !operator.equals(">"))
			throw new PatcherLibBaseEx("Wrong Operator: currently only works with =, >= and >");

		this.operator = operator;
	}

	public FactionBuildingExists() {
	}
}
