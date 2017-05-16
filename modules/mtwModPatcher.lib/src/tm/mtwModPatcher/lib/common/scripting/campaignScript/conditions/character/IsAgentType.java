package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character;

import tm.mtwModPatcher.lib.common.entities.AgentType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-27.
 */
public class IsAgentType extends Condition {

	private AgentType _AgentType;

	@Override
	public String getString() {

		String typeStr;

		if(_AgentType == AgentType.NamedCharacter)
			typeStr = "named character";
		else typeStr = _AgentType.toString();

		typeStr = typeStr.toLowerCase();


		return "AgentType = " + typeStr;
	}

	public IsAgentType(AgentType agentType) {
		this._AgentType = agentType;
	}
}
