package tm.mtwModPatcher.lib.common.scripting.campaignScript.core;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ScriptBlock;

import java.util.List;

/**
 * Created by Tomek on 2016-11-17.
 */
public abstract class BlockWithConditions extends ScriptBlock {

	protected Conditions _Conditions;
	protected ScriptLines _Body = new ScriptLines();


	protected abstract String getKeywordStart();
	protected abstract String getKeywordEnd();

	public BlockWithConditions(String conditionsStr) {
		_Conditions = new Conditions(conditionsStr);
	}
	public BlockWithConditions(Condition condition) {
		_Conditions = new Conditions(condition);
	}

	public Conditions andCondition(String condition) {
		_Conditions.and(condition);

		return _Conditions;
	}
	public Conditions andCondition(Condition condition) {
		_Conditions.and(condition);
		validate();

		return _Conditions;
	}
	public Conditions andCondition(Conditions conditions) {
		_Conditions.and(conditions);
		validate();

		return _Conditions;
	}
	public Conditions andCondition(List<Condition> conditions) {

		for (Condition condition : conditions)
			andCondition(condition);

		validate();

		return _Conditions;
	}

	public void add(String line) {
		_Body.line(line);
	}

	public void add(ScriptBlock block) {
		_Body.add(block.getScriptBlock(), 0);
	}

	public void add(ScriptElement element) {
		_Body.line(element.getString());
	}

	public boolean isBodyEmpty() {
		return _Body.isEmpty();
	}

	protected void validate() { }
}
