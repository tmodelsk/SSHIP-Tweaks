package tm.mtwModPatcher.lib.managers;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.MonitorEventBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ScriptBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.base.NotCondition;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.base.TrueCondition;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.FactionType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;

/**
 * Created by Tomek on 2016-11-27.
 */
public class CampaignScriptManager {

	// not tested
	public void insertAtEndOfFactionNotSlaveTurnStart(ScriptBlock block) throws PatcherLibBaseEx {
		int insertIndex = getFactionTurnStartNotSlaveInsertIndex();

		if(insertIndex < 0) {
			// create monitor
			val monitor = new MonitorEventBlock(EventType.FactionTurnStart,
					new NotCondition(new FactionType("slave")), getTagFactionNotSlaveTurnStart());

			_CampaignScript.insertAtEndOfFile(monitor);
			insertIndex = getFactionTurnStartNotSlaveInsertIndex();
			if(insertIndex < 0) throw new PatcherLibBaseEx("Unable to find created Framework section FactionTurnStart NotSlave");
		}

		_CampaignScript.getLines().insertAt(insertIndex , block.getScriptBlock().getLines());
	}

	public void insertAtEndOfPreFactionNoConditionTurnStart(ScriptBlock block) throws PatcherLibBaseEx {
		int insertIndex = getPreFactionTurnStartNoConditionInsertIndex();

		if(insertIndex < 0) {
			// create monitor
			val monitor = new MonitorEventBlock(EventType.PreFactionTurnStart,
					new TrueCondition(), getTagPreFactionNoConditionTurnStart());

			_CampaignScript.insertAtEndOfFile(monitor);
			insertIndex = getPreFactionTurnStartNoConditionInsertIndex();
			if(insertIndex < 0) throw new PatcherLibBaseEx("Unable to find created Framework section PreFactionTurnStart NoCondition");
		}

		_CampaignScript.getLines().insertAt(insertIndex , block.getScriptBlock().getLines());
	}

	// // not tested
	public void insertAtEndOfPreFactionNotSlaveTurnStart(ScriptBlock block) throws PatcherLibBaseEx {
		int insertIndex = getPreFactionTurnStartNotSlaveInsertIndex();

		if(insertIndex < 0) {
			// create monitor
			val monitor = new MonitorEventBlock(EventType.PreFactionTurnStart,
					new NotCondition(new FactionType("slave")), getTagPreFactionNotSlaveTurnStart());

			_CampaignScript.insertAtEndOfFile(monitor);
			insertIndex = getPreFactionTurnStartNotSlaveInsertIndex();
			if(insertIndex < 0) throw new PatcherLibBaseEx("Unable to find created Framework section PreFactionTurnStart NotSlave");
		}

		_CampaignScript.getLines().insertAt(insertIndex , block.getScriptBlock().getLines());
	}

	public void insertAtEndOfPreFactionTurnStart(String factionName, ScriptBlock block) throws PatcherLibBaseEx {
		int insertIndex = getPreFactionTurnStartInsertIndex(factionName);

		if(insertIndex < 0) {
			// create monitor
			val monitor = new MonitorEventBlock(EventType.PreFactionTurnStart, new FactionType(factionName), getTagPreFactionTurnStart(factionName));

			_CampaignScript.insertAtEndOfFile(monitor);
			insertIndex = getPreFactionTurnStartInsertIndex(factionName);
			if(insertIndex < 0) throw new PatcherLibBaseEx("Unable to find created Framework section PreFactionTurnStart "+factionName);
		}

		_CampaignScript.getLines().insertAt(insertIndex , block.getScriptBlock().getLines());
	}


	private int getPreFactionTurnStartInsertIndex(String factionName) throws PatcherLibBaseEx {
		val tagStr = getTagPreFactionTurnStart(factionName);

		LinesProcessor lines = _CampaignScript.getLines();

		int indexStart = lines.findFirstRegexLine("monitor_event.+;" + tagStr);

		if(indexStart < 0) return -1;

		// Find end_monitor

		int indexEnd = lines.findExpFirstRegexLine("end_monitor" , indexStart+1);

		return indexEnd;
	}
	private String getTagPreFactionTurnStart(String factionName) {
		return "CS Framework Tag: PreFactionTurnStart "+factionName+" : do not remove";
	}

	private int getPreFactionTurnStartNotSlaveInsertIndex() throws PatcherLibBaseEx {
		val tagStr = getTagPreFactionNotSlaveTurnStart();

		LinesProcessor lines = _CampaignScript.getLines();

		int indexStart = lines.findFirstRegexLine("monitor_event.+;" + tagStr);

		if(indexStart < 0) return -1;

		// Find end_monitor

		int indexEnd = lines.findExpFirstRegexLine("end_monitor" , indexStart+1);

		return indexEnd;
	}
	private String getTagPreFactionNotSlaveTurnStart() {
		return "CS Framework Tag: PreFactionTurnStart NotSlave : do not remove";
	}

	private int getPreFactionTurnStartNoConditionInsertIndex() throws PatcherLibBaseEx {
		val tagStr = getTagPreFactionNoConditionTurnStart();

		LinesProcessor lines = _CampaignScript.getLines();

		int indexStart = lines.findFirstRegexLine("monitor_event.+;" + tagStr);

		if(indexStart < 0) return -1;

		// Find end_monitor

		int indexEnd = lines.findExpFirstRegexLine("end_monitor" , indexStart+1);

		return indexEnd;
	}
	private String getTagPreFactionNoConditionTurnStart() {
		return "CS Framework Tag: PreFactionTurnStart NoCondition : do not remove";
	}

	private int getFactionTurnStartNotSlaveInsertIndex() throws PatcherLibBaseEx {
		val tagStr = getTagFactionNotSlaveTurnStart();

		LinesProcessor lines = _CampaignScript.getLines();

		int indexStart = lines.findFirstRegexLine("monitor_event.+;" + tagStr);

		if(indexStart < 0) return -1;

		// Find end_monitor

		int indexEnd = lines.findExpFirstRegexLine("end_monitor" , indexStart+1);

		return indexEnd;
	}
	private String getTagFactionNotSlaveTurnStart() {
		return "CS Framework Tag: FactionTurnStart NotSlave : do not remove";
	}

	private CampaignScript _CampaignScript;

	public CampaignScriptManager(CampaignScript campaignScript) {
		this._CampaignScript = campaignScript;
	}
}
