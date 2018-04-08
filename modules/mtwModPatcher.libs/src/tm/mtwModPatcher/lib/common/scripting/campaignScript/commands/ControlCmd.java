package tm.mtwModPatcher.lib.common.scripting.campaignScript.commands;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Command;

public class ControlCmd extends Command {

	private String faction;

	@Override
	public String getString() {
		return "console_command control "+faction;
	}

	public ControlCmd(String faction) {
		this.faction = faction;
	}
}
