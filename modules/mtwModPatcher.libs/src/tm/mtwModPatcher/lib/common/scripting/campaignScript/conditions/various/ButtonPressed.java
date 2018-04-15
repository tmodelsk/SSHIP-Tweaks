package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

public class ButtonPressed extends Condition {

	private String _ButtonSymbol;

	@Override
	public String getString() {
		return "ButtonPressed "+_ButtonSymbol;
	}

	public ButtonPressed(String buttonSymbol) {
		this._ButtonSymbol = buttonSymbol;
	}

	public static final String SIEGE_ASSAULT_BUTTON = "siege_assault_button";

	public static ButtonPressed SiegeAssault() {
		return new ButtonPressed(SIEGE_ASSAULT_BUTTON);
	}


}
