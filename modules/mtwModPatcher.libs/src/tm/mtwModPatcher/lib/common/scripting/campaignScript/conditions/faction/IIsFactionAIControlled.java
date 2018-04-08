package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

public class IIsFactionAIControlled extends Condition {

	private String faction;

	@Override
	public String getString() {
		return "I_IsFactionAIControlled "+faction;
	}

	public IIsFactionAIControlled(String faction) {
		this.faction = faction;
	}
}
