package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

public class IHotSeatEnabled extends Condition {
	@Override
	public String getString() {
		return "I_HotseatEnabled";
	}

	public final static IHotSeatEnabled I = new IHotSeatEnabled();
}
