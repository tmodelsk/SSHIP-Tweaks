package tm.mtwModPatcher.lib.common.scripting.campaignScript.core;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ScriptBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tomek on 2016-11-17.
 */
public class Conditions extends ScriptBlock {

	public Conditions and(String condition) {
		_RestLines.line("	and "+condition);

		return this;
	}

	public Conditions and(Condition condition) {
		conditions.add(condition);
		and(condition.getString());

		return this;
	}

	public Conditions and(Conditions conditions) {
		this.conditions.addAll(conditions.conditions());
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
		conditions.add(condition);
	}

	private String _FirstLine;
	private ScriptLines _RestLines = new ScriptLines();
	private List<Condition> conditions = new ArrayList<>();
	public List<Condition> conditions() {
		return Collections.unmodifiableList(conditions);
	}

	@Override
	public ScriptLines getScriptBlock() {
		return _RestLines;
	}
}
