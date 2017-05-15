package tm.m2twModPatcher.lib.common.scripting.campaignScript.commands;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;

/**
 * Created by Tomek on 2016-11-19.
 */
public class WriteToLog extends ScriptElement {

	private LogLevel _LogLevel;
	private String _Message;

	@Override
	public String getString() {
		return "log " + _LogLevel.toString().toLowerCase() + " " + _Message ;
	}

	public WriteToLog(String msg) {
		this(LogLevel.Always, msg);
	}

	public WriteToLog(LogLevel logLevel, String message) {
		this._LogLevel = logLevel;
		this._Message = message;
	}
}
