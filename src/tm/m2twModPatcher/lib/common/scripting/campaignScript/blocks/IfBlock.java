package tm.m2twModPatcher.lib.common.scripting.campaignScript.blocks;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.BlockWithConditions;
import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;
import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.ScriptLines;

/**   */
public class IfBlock extends BlockWithConditions {

	@Override
	public ScriptLines getScriptBlock() {
		ScriptLines lines = new ScriptLines();

		lines.line( getKeywordStart() + " " + _Conditions.getFirstLine());
		lines.add(_Conditions.getRestLines() , 1);
		lines.add(_Body, 1);
		lines.line(getKeywordEnd());

		return lines;
	}

	@Override
	protected String getKeywordStart() {
		return "if";
	}

	@Override
	protected String getKeywordEnd() {
		return "end_if";
	}

	public IfBlock(String conditionsStr) {
		super(conditionsStr);
	}

	public IfBlock(Condition condition) {
		super(condition);
	}
}
