package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.mtwModPatcher.lib.common.entities.AgentType;

/**
 * Created by Tomek on 2016-12-04.
 */
public class IsNotAgentType extends IsAgentType {

	@Override
	public String getString() {
		return "not " + super.getString();
	}

	public IsNotAgentType(AgentType agentType) {
		super(agentType);
	}
}
