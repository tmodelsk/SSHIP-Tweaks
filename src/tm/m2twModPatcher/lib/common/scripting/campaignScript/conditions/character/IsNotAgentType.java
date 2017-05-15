package tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.m2twModPatcher.lib.common.entities.AgentType;
import tm.m2twModPatcher.lib.common.scripting.campaignScript.conditions.character.IsAgentType;

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
