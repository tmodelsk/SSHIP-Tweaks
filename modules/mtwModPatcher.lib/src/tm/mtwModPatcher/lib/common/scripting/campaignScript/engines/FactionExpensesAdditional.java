package tm.mtwModPatcher.lib.common.scripting.campaignScript.engines;

import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.IsNotFactionAIControlled;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.DeclareVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.RegionBlock;

import java.util.List;

/**  */
public class FactionExpensesAdditional {

	public void ensureFactionVariablesExist() throws PatcherLibBaseEx {
		LinesProcessor csLines = _CampaignScript.getLines();
		int sectionStartIndex = csLines.findFirstRegexLine(_VariableDeclarationsTag);

		if(sectionStartIndex > 0) return;	// Section already exists !

		// ## Section Creation - it doesn't exist !! ##
		RegionBlock rootRegion = new RegionBlock(_VariableDeclarationsTag, true);

		List<String> factions = FactionsDefs.allFactionsExceptRebelsList();

		for (String factionName : factions) {
			rootRegion.add(new DeclareVariable(getFactionKingsPurseCompensateVariable(factionName)));
			//rootRegion.add(new DeclareVariable(getFactionExpenseVariable(factionName)));
		}
		//rootRegion.add(new DeclareVariable(getFactionExpenseVariableForLocal()));

		_CampaignScript.insertAtEndOfFile(rootRegion.getScriptBlock().getLines());
	}

	public void ensureLocalKingsPurseCompensateExists() throws PatcherLibBaseEx {
		LinesProcessor csLines = _CampaignScript.getLines();
		int sectionStartIndex = csLines.findFirstRegexLine(_CompensateKingsPurseTag);

		if(sectionStartIndex > 0) return;	// Section already exists !

		// ## Section Creation - it doesn't exist ##
		RegionBlock rootRegion = new RegionBlock(_CompensateKingsPurseTag);

		MonitorEventBlock factionMonitor;
		factionMonitor = new MonitorEventBlock(EventType.PreFactionTurnStart , new IsNotFactionAIControlled());

		List<String> factions = FactionsDefs.allFactionsExceptRebelsList();

		for(String factionName : factions) {
			int factionId = FactionsDefs.getFactionAiEcId(factionName);

			IfBlock ifFaction = new IfBlock( new CompareCounter("pl_ec_id" , "=" , factionId) );
			//ifFaction.add(new SetKingsPurse(factionName, 1000));

			int actValue = 100000; // 100 000
			while (true) {

				if(actValue == 1) {
					ifFaction.add(createKingsPurseCompensateIfBlock(2, factionName));
					ifFaction.add(createKingsPurseCompensateIfBlock(1, factionName));
					break;
				}
				else {
					ifFaction.add(createKingsPurseCompensateIfBlock(actValue, factionName));
				}
				actValue = actValue / 2;
			}

			factionMonitor.add(ifFaction);
		}
		rootRegion.add(factionMonitor);

		_CampaignScript.insertAtEndOfFile(rootRegion.getScriptBlock().getLines());
	}

	public ScriptBlock createKingsPurseCompensateIfBlock(int value, String factionName) {
		String kingsPurseVar = getFactionKingsPurseCompensateVariable(factionName);

		IfBlock ifBlock = new IfBlock(new CompareCounter(kingsPurseVar, ">=" , value));
		ifBlock.add(new IncrementKingsPurse(factionName, value));
		ifBlock.add(new IncrementVariable(kingsPurseVar , - value));
		if(IsLoggingEnabled)
			ifBlock.add(new WriteToLog(LogLevel.Always, "#### PreFactionTurnStart : Compensate Kings Purse "+ value +" !!! ####"));

		return  ifBlock;
	}

	public static String getFactionKingsPurseCompensateVariable(String factionName ) {
		return factionName + "_KingsPurseCompensate";
	}

	private String _VariableDeclarationsTag = "Faction Expenses Variables Tag - do not remove!";
	private String _CompensateKingsPurseTag = "Local Faction compensate Kings Purse Tag - do not remove!";

	private CampaignScript _CampaignScript;

	public boolean IsLoggingEnabled = false;


	public FactionExpensesAdditional(CampaignScript campaignScript) {
		this._CampaignScript = campaignScript;
	}
}
