package tm.m2twModPatcher.lib.common.scripting.campaignScript.core;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.blocks.ScriptBlock;

/**
 * Created by Tomek on 2016-11-17.
 */
public class Conditions extends ScriptBlock {

	public Conditions and(String condition) {
		_RestLines.line("	and "+condition);

		return this;
	}

	public Conditions and(Condition condition) {
		and(condition.getString());

		return this;
	}

	public Conditions and(Conditions conditions) {
		and(conditions.getFirstLine());

		ScriptLines lines = conditions.getRestLines();
		_RestLines.add(lines, 0);

		return this;
	}

	public String getFirstLine() {
		return _FirstLine;
	}

	public ScriptLines getRestLines() {
		return _RestLines;
	}

	public Conditions(String condition) {
		_FirstLine = condition;
	}

	public Conditions(Condition condition) {
		_FirstLine = condition.getString();
	}

	private String _FirstLine;
	private ScriptLines _RestLines = new ScriptLines();

	@Override
	public ScriptLines getScriptBlock() {
		return _RestLines;
	}
}
