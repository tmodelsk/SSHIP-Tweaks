package tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;

/**
 * Created by Tomek on 2016-11-27.
 */
public class SetEventCounter extends ScriptElement {

	private String _EventCounterName;
	private int _Value;

	@Override
	public String getString() {
		return "set_event_counter " + _EventCounterName + " "+_Value;
	}

	public SetEventCounter(String eventCounterName, int value) {
		this._EventCounterName = eventCounterName;
		this._Value = value;
	}
}
