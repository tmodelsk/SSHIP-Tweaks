package tm.m2twModPatcher.lib.common.scripting.campaignScript.blocks;

import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.BlockWithConditions;
import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.Condition;
import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.m2twModPatcher.lib.common.scripting.campaignScript.core.ScriptLines;

import java.util.List;

/**  */
public class MonitorEventBlock extends BlockWithConditions {

	@Override
	public ScriptLines getScriptBlock() {
		ScriptLines lines = new ScriptLines();

		String tagCommentTmp="";
		if(_TagCommentStr != null)
			tagCommentTmp = " ;" + _TagCommentStr;

		lines.line( getKeywordStart() +" " + _EventType + " " + _Conditions.getFirstLine() + tagCommentTmp);
		lines.add(_Conditions.getRestLines() , 1);
		lines.add(_Body, 1);
		lines.line(getKeywordEnd());

		return lines;
	}

	private EventType _EventType;

	private String _TagCommentStr = null;

	@Override
	protected String getKeywordStart() {
		return "monitor_event";
	}

	@Override
	protected String getKeywordEnd() {
		return "end_monitor";
	}

	public MonitorEventBlock(EventType eventType, String conditionsStr) {
		super(conditionsStr);

		_EventType = eventType;
	}
	public MonitorEventBlock(EventType eventType, Condition condition) {
		this(eventType, condition.getString());
	}
	public MonitorEventBlock(EventType eventType, Condition condition, String tagCommentStr) {
		this(eventType, condition);
		_TagCommentStr = tagCommentStr;
	}
	public MonitorEventBlock(EventType eventType, Condition ... conditions) {
		this(eventType, conditions[0].getString());

		for (int i = 1; i <  conditions.length; i++) {
			_Conditions.and(conditions[i]);
		}
	}

	public MonitorEventBlock(EventType eventType, List<Condition> conditions) {
		this(eventType, conditions.get(0).getString());

		for (int i = 1; i <  conditions.size(); i++) {
			_Conditions.and(conditions.get(i));
		}
	}

//	public MonitorEventBlock(EventType eventType, List<CharacterCondition> conditions) {
//		this(eventType, conditions.getFirst(0).getString());
//
//		for (int i = 1; i <  conditions.size(); i++) {
//			_Conditions.and(conditions.getFirst(i));
//		}
//	}
}
