package tm.mtwModPatcher.lib.common.scripting.campaignScript.commands;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Command;

public class PerfectSpyCmd extends Command {
	@Override
	public String getString() {
		return "console_command toggle_perfect_spy";
	}

	public static final PerfectSpyCmd I = new PerfectSpyCmd();
}
