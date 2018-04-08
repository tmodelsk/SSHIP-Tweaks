package tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character.IsTargetRegionOneOf;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.BlockWithConditions;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptLines;

import java.util.*;

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

	public void terminateMonitor() {
		add(TERMINATE_MONITOR);
	}

	public MonitorEventBlock(EventType eventType, String conditionsStr) {
		super(conditionsStr);
		_EventType = eventType;
	}
	public MonitorEventBlock(EventType eventType, Condition condition) {
		super(condition);
		_EventType = eventType;
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

	@Override
	protected void validate() {
		val conditionClasses = wrongCondition.get(_EventType);
		if(conditionClasses != null) {
			for (Condition cond : _Conditions.conditions()) {
				if(conditionClasses.contains(cond.getClass()))
					throw new PatcherLibBaseEx("Incompatible !");
			}
		}
	}

	private static Map<EventType, Set<Class>> initWrongContitions() {
		Map<EventType, Set<Class>> wc = new HashMap<>();

		Set<Class> settlTurnEnd = new HashSet<>();
		settlTurnEnd.add(IsTargetRegionOneOf.class);

		wc.put(EventType.SettlementTurnEnd, settlTurnEnd);

		return wc;
	}

	private static Map<EventType, Set<Class>> wrongCondition = initWrongContitions();

	public static final TerminateMonitor TERMINATE_MONITOR = TerminateMonitor.I;
}
