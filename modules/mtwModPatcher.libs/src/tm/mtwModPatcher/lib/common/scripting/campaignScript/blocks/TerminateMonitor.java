package tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;

public class TerminateMonitor extends ScriptElement {
	@Override
	public String getString() {
		return "terminate_monitor";
	}

	public static final TerminateMonitor I = new TerminateMonitor();
}
