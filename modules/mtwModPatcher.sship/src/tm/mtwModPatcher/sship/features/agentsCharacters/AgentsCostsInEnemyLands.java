package tm.mtwModPatcher.sship.features.agentsCharacters;

import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.AddMoney;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.IncrementKingsPurse;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.MonitorEventBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.IfBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character.IsInEnemyLands;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.RegionBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.engines.FactionExpensesAdditional;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.FactionAiEcId;

import java.util.List;
import java.util.UUID;

/**   */
public class AgentsCostsInEnemyLands extends Feature {

	public double AiMultiplier = 0.50;

	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);

		FactionExpensesAdditional factionExpenses = new FactionExpensesAdditional(_CampaignScript);
		factionExpenses.ensureFactionVariablesExist();
		factionExpenses.ensureLocalKingsPurseCompensateExists();
		//factionExpenses.ensureFactionChargingExpensesExist();

		List<FactionInfo> factions = FactionsDefs.getFactionInfos();
		factions.remove(FactionsDefs.Rebels);


		RegionBlock rootRegion = new RegionBlock("Agent costs in enemy lines ;TM Patcher");
		MonitorEventBlock monitor;

		int basicCosts = 200;

		monitor = createMonitorForAgent("spy" , 3 * basicCosts , factions);
		rootRegion.add(monitor);

		monitor = createMonitorForAgent("assassin" , 2 * basicCosts , factions);
		rootRegion.add(monitor);

		monitor = createMonitorForAgent("priest" , 2 * basicCosts , factions);
		rootRegion.add(monitor);

		monitor = createMonitorForAgent("diplomat" , 1 * basicCosts , factions);
		rootRegion.add(monitor);

		List<String> rl = rootRegion.getScriptBlock().getLines();
		_CampaignScript.insertAtEndOfFile( rl );

	}

	private MonitorEventBlock createMonitorForAgent(String agentType, int costValue , List< FactionInfo> factions) {
		List<FactionAiEcId> factionsAiIds = _CampaignScript.FactionsAiEcIds;

		// CharacterTurnStart
		MonitorEventBlock monitor = new MonitorEventBlock(EventType.CharacterTurnStart, "AgentType = " + agentType);
		monitor.andCondition(new IsInEnemyLands());

		// # For Local Player :
		IfBlock ifLocalPlayer = new IfBlock(new CompareCounter("ai_ec_id", "=" , 0));

		for(FactionAiEcId factionId : factionsAiIds) {
			IfBlock ifPlayer = new IfBlock(new CompareCounter("pl_ec_id" , "=" , factionId.Id));

			//ifPlayer.add(new AddMoney(factionId.Name , -costLevel));
			ifPlayer.add(new IncrementKingsPurse(factionId.Name, -costValue));
			ifPlayer.add(new IncrementVariable(FactionExpensesAdditional.getFactionKingsPurseCompensateVariable(factionId.Name), costValue));

			//ifPlayer.add(new WriteToLog(LogLevel.Always, "CharacterTurnStart "+agentType +" LOCAL "+ factionId.Name +" fired ########"));

			ifLocalPlayer.add(ifPlayer);
		}

		monitor.add(ifLocalPlayer);

		// ## Add AI :
		int aiCosts = (int)(costValue * AiMultiplier);
		for (FactionAiEcId factionAiId : factionsAiIds) {
			IfBlock ifFaction = new IfBlock(new CompareCounter("ai_ec_id" , "=" , factionAiId.Id));

			ifFaction.add(new AddMoney(factionAiId.Name, -aiCosts));
			//ifFaction.add(new WriteToLog(LogLevel.Always, "CharacterTurnStart Agent "+agentType +" Cost: "+ aiCosts +" : " + factionAiId.Name));

			monitor.add(ifFaction);
		}

		return monitor;
	}

	protected CampaignScript _CampaignScript;
	protected String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("c2555ac6-d71f-4cf5-89f8-c1a01c6ea949");

	public AgentsCostsInEnemyLands() {
		super("Diplomand, Spies , Assasins in enemy lands has additional upkeep costs");
	}
}
