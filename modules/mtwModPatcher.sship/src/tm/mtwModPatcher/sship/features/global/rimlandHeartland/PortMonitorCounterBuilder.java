package tm.mtwModPatcher.sship.features.global.rimlandHeartland;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ContainerBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.IfBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.MonitorEventBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.RegionBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.FactionTypeSlave;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.IsRegionOneOf;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.IsSettlementRioting;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.SettlementBuildingExists;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.SettlementHasPlague;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.DeclareVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.SetVariable;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.FactionAiEcId;
import tm.mtwModPatcher.lib.managers.SettlementManager;
import tm.mtwModPatcher.sship.lib.Buildings;
import tm.mtwModPatcher.sship.lib.HiddenResources;
import tm.mtwModPatcher.sship.lib.Provinces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by tomek on 05.10.2017.
 */
public class PortMonitorCounterBuilder {

	public MonitorEventBlock createMainCounter(String buildingLevel, String operator, int increment) {

		val factionsAiIds = campaignScript.FactionsAiEcIds;

		val monitor = new MonitorEventBlock(EventType.SettlementTurnEnd,
				new FactionTypeSlave().not(),
				new SettlementBuildingExists(buildingLevel, operator),
				new SettlementHasPlague().not(),
				new IsSettlementRioting().not(),
				new IsRegionOneOf(islandProvinces).not());

		// # For Local Player :
		IfBlock ifLocalPlayer = new IfBlock(new CompareCounter("ai_ec_id", "=" , 0));
		for(FactionAiEcId factionId : factionsAiIds) {
			val ifPlayerFactionIs = new IfBlock(new CompareCounter("pl_ec_id" , "=" , factionId.Id));
			ifPlayerFactionIs.andCondition(new CompareCounter(SettlementIsRoadOrRiverVar, "=", 1));

			ifPlayerFactionIs.add(getMainCounterBody(factionId.Name, increment, "Player " + operator + " " + buildingLevel));
			ifLocalPlayer.add(ifPlayerFactionIs);
		}
		monitor.add(ifLocalPlayer);

		for (val factionAiId : factionsAiIds) {
			val ifFaction = new IfBlock(new CompareCounter("ai_ec_id", "=", factionAiId.Id));
			ifFaction.andCondition(new CompareCounter(SettlementIsRoadOrRiverVar, "=", 1));

			ifFaction.add(getMainCounterBody(factionAiId.Name, increment, "AI " + operator + " " + buildingLevel));
			monitor.add(ifFaction);
		}

		return monitor;
	}

	private ContainerBlock getMainCounterBody(String factionName, int increment, String logSuffix) {
		val bl = new ContainerBlock();

		val variable = factionVariables.get(factionName);
		bl.add(new IncrementVariable(variable, increment));
		if(logLevel >= 2)
			bl.add(new WriteToLog(Ctm.msgFormat("SeaTrade Bonus: {0} increment {1} {2}", factionName, increment, logSuffix)));

		return bl;
	}


	public RegionBlock createRoadOrRiverPortFlagMonitors() {
		val rb = new RegionBlock("SeaTrade bonuses: Road OR RiverPort flag");

		rb.add(new DeclareVariable(SettlementIsRoadOrRiverVar));

		MonitorEventBlock monitor;

		// Reset counter
		monitor = new MonitorEventBlock(EventType.SettlementTurnEnd,new FactionTypeSlave().not());

		monitor.add(new SetVariable(SettlementIsRoadOrRiverVar, 0));
		if(logLevel >= 2)
			monitor.add(new WriteToLog("SettlementTurnEnd SettlementIsRoadOrRiverVar reset"));
		rb.add(monitor);

		// ### Roads ###
		monitor = new MonitorEventBlock(EventType.SettlementTurnEnd,new FactionTypeSlave().not());
		monitor.andCondition(new SettlementBuildingExists(Buildings.RoadCityLevels.get(0) , ">="));
		monitor.add(new SetVariable(SettlementIsRoadOrRiverVar, 1));
		if(logLevel >= 2)
			monitor.add(new WriteToLog("SettlementTurnEnd SettlementIsRoadOrRiverVar city road 1"));
		rb.add(monitor);

		monitor = new MonitorEventBlock(EventType.SettlementTurnEnd,new FactionTypeSlave().not());
		monitor.andCondition(new SettlementBuildingExists(Buildings.RoadCastleLevels.get(0) , ">="));
		monitor.add(new SetVariable(SettlementIsRoadOrRiverVar, 1));
		if(logLevel >= 2)
			monitor.add(new WriteToLog("SettlementTurnEnd SettlementIsRoadOrRiverVar castle road 1"));
		rb.add(monitor);

		// ### Rivers ###
		val portWithRiverProvinces = seashoreProvinces.getList().stream()
				.filter( p -> p.getItem3()).map( p -> p.getItem1())
				.collect(Collectors.toList());

		monitor = new MonitorEventBlock(EventType.SettlementTurnEnd,new FactionTypeSlave().not());
		monitor.andCondition(new IsRegionOneOf(portWithRiverProvinces));
		monitor.add(new SetVariable(SettlementIsRoadOrRiverVar, 1));
		if(logLevel >= 2)
			monitor.add(new WriteToLog("SettlementTurnEnd SettlementIsRoadOrRiverVar port with river port 1"));
		rb.add(monitor);

		return rb;
	}

	private static final String SettlementIsRoadOrRiverVar = "SettlementHasRoadOrRiverPort";

	private Map<String, String> factionVariables;
	private CampaignScript campaignScript;
	private SettlementManager settlementManager;

	@Getter @Setter
	private List<String> islandProvinces;
	@Getter @Setter
	private RimlandProvinceList seashoreProvinces;
	@Getter @Setter
	private int logLevel = 0;

	public PortMonitorCounterBuilder(Map<String, String> factionVariables, CampaignScript campaignScript, SettlementManager settlementManager) {
		this.factionVariables = factionVariables;
		this.campaignScript = campaignScript;
		this.settlementManager = settlementManager;
	}
}
