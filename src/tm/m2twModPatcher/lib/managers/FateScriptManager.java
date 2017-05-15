package tm.m2twModPatcher.lib.managers;

import tm.common.Ctm;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.entities.RegionOwnershipInfo;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.m2twModPatcher.lib.fileEntities.data.world.maps.campaign.CampaignScript;

/**
 * Manages Faction's Fate scripts
 */
public class FateScriptManager {

	public void writeRegionRangeScript(String factionName , int yearStart , int yearEnd , int regionMin , int regionMax, int florinBonus) {
		writeRegionRangeScript(factionName, yearStart, yearEnd, regionMin, regionMax, florinBonus, null);
	}

	public void writeRegionRangeScript(String factionName , int yearStart , int yearEnd , int regionMin , int regionMax, int florinBonus, String logComment) {
		int insertIndex = getOrCreateFactionFateMonitor(factionName)+1;

		int turnStart = 1 + (yearStart - 1132) * 2;
		int turnEnd = 1 + (yearEnd - 1132) * 2;

		String s = "";
		s += "  if I_TurnNumber >= " + turnStart +nl;
		s += "   and I_TurnNumber <= " + turnEnd +nl;
		s += "   and I_NumberOfSettlements "+ factionName +" >= " + regionMin +nl;
		s += "   and I_NumberOfSettlements "+ factionName +" <= " + regionMax +nl;

		//s += "     add_money " + factionName + " " + florinBonus +nl;
		s += "    console_command add_money " + factionName +", " + florinBonus + nl;

		if(logComment == null) logComment="?";
		s += "    " + new WriteToLog(Ctm.msgFormat("Fate {0} BONUS {1}fl {2} for RegionRange, years [{3},{4}], regions [{5},{6}]",
							factionName, new Integer(florinBonus).toString(), logComment, new Integer(yearStart).toString(), new Integer(yearEnd).toString(), regionMin, regionMax)).getString() + nl;

		s += "  end_if" +nl;

		_CampaignScript.getLines().insertAt(insertIndex , s);
	}

	public void writeRegionNegativeOwnershipScript(String factionName , int yearStart , int yearEnd , RegionOwnershipInfo regionOwnershipsNegation, int florinBonus) {
		writeRegionNegativeOwnershipScript(factionName, yearStart, yearEnd, regionOwnershipsNegation, florinBonus, null);
	}

	public void writeRegionNegativeOwnershipScript(String factionName , int yearStart , int yearEnd , RegionOwnershipInfo regionOwnershipsNegation, int florinBonus, String logComment) {

		int insertIndex = getOrCreateFactionFateMonitor(factionName)+1;

		int turnStart = 1 + (yearStart - 1132) * 2;
		int turnEnd = 1 + (yearEnd - 1132) * 2;

		String s = "";
		s += "  if I_TurnNumber >= " + turnStart +nl;
		s += "   and I_TurnNumber <= " + turnEnd +nl;

		for(String ownerName : regionOwnershipsNegation.FactionsList) {

			// and I_SettlementOwner Constantinople = byzantium
			s += "   and not I_SettlementOwner " + regionOwnershipsNegation.RegionName + " = " + ownerName +nl;

		}

		//s += "     add_money " + factionName + " " + florinBonus +nl;
		s += "    console_command add_money " + factionName +", " + florinBonus + nl;

		if(logComment == null) logComment="?";
		s += "    " + new WriteToLog(Ctm.msgFormat("Fate {0} BONUS {1}fl {2} for RegionNegativeOwnership, years [{3},{4}]",
				factionName, new Integer(florinBonus).toString(), logComment, new Integer(yearStart).toString(), new Integer(yearEnd).toString())).getString() + nl;

		s += "  end_if" +nl;

		_CampaignScript.getLines().insertAt(insertIndex , s);
	}

	protected int getOrCreateFactionFateMonitor(String factionName) throws PatcherLibBaseEx {
		int index;

		String tagStr = "; === Fate Script Monitor for Faction "+factionName+" . Do not remove or change - its tagging comment";
		String regex = "^"+tagStr;

		index = _CampaignScript.getLines().findFirstRegexLine(regex);

		if(index < 0) {
			LinesProcessor lines = _CampaignScript.getLines();
			String s = "";

			int insertLine = lines.findExpFirstRegexLine("^wait_monitors");

			s += nl + tagStr +nl;
			s += "monitor_event PreFactionTurnStart FactionType "+factionName +nl;
			s += "    and not FactionIsLocal" +nl;
			s += "end_monitor" +nl+" " +nl;

			lines.insertAt(insertLine, s);

			index = lines.findExpFirstRegexLine(regex);
		}

		index += 1 + 1;	// move to monitor add (+1 IsFactionLocal condition)

		return index;
	}

	protected CampaignScript _CampaignScript;

	protected String nl = System.lineSeparator();

	public FateScriptManager(CampaignScript _CampaignScript) {
		this._CampaignScript = _CampaignScript;
	}
}
