package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/**
 * Created by Tomek on 2016-11-22.
 */
public class ButtonPressed extends Condition {

	private String _ButtonSymbol;

	@Override
	public String getString() {
		return "ButtonPressed "+_ButtonSymbol;
	}

	public ButtonPressed(String buttonSymbol) {
		this._ButtonSymbol = buttonSymbol;
	}
}
