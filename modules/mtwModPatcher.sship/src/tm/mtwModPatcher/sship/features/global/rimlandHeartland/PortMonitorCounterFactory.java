package tm.mtwModPatcher.sship.features.global.rimlandHeartland;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ContainerBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.IfBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.MonitorEventBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.FactionTypeSlave;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.SettlementBuildingExists;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.FactionAiEcId;

import java.util.Map;

/**
 * Created by tomek on 05.10.2017.
 */
public class PortMonitorCounterFactory {

	public MonitorEventBlock create(String buildingLevel, String operator, int increment) {

		val factionsAiIds = campaignScript.FactionsAiEcIds;

		val monitor = new MonitorEventBlock(EventType.SettlementTurnEnd,
				new FactionTypeSlave().not(), new SettlementBuildingExists(buildingLevel, operator));

		// # For Local Player :
		IfBlock ifLocalPlayer = new IfBlock(new CompareCounter("ai_ec_id", "=" , 0));
		for(FactionAiEcId factionId : factionsAiIds) {
			IfBlock ifPlayerFactionIs = new IfBlock(new CompareCounter("pl_ec_id" , "=" , factionId.Id));

			ifPlayerFactionIs.add(getBody(factionId.Name, increment, "Player"));
			ifLocalPlayer.add(ifPlayerFactionIs);
		}
		monitor.add(ifLocalPlayer);

		for (val factionAiId : factionsAiIds) {
			val ifFaction = new IfBlock(new CompareCounter("ai_ec_id", "=", factionAiId.Id));

			ifFaction.add(getBody(factionAiId.Name, increment, "AI"));
			monitor.add(ifFaction);
		}

		return monitor;
	}

	private ContainerBlock getBody(String factionName, int increment, String logSuffix) {
		val bl = new ContainerBlock();

		val variable = factionVariables.get(factionName);
		bl.add(new IncrementVariable(variable, increment));
		bl.add(new WriteToLog(Ctm.msgFormat("SeaTrade Bonus: {0} increment {1} {2}", factionName, increment, logSuffix)));

		return bl;
	}

	private Map<String, String> factionVariables;
	private CampaignScript campaignScript;

	public PortMonitorCounterFactory(Map<String, String> factionVariables, CampaignScript campaignScript) {
		this.factionVariables = factionVariables;
		this.campaignScript = campaignScript;
	}
}
