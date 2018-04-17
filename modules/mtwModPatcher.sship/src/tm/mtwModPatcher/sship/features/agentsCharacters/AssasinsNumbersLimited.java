package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.AgentType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.LogLevel;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character.IsAgentType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.NumberOfSettlements;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.DeclareVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.SetEventCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.SetVariable;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.FactionAiEcId;
import tm.mtwModPatcher.lib.managers.CampaignScriptManager;
import tm.mtwModPatcher.lib.managers.FactionsDefs;

import java.util.*;

/** Agents total numbers are Limited  aaas */
public class AssasinsNumbersLimited extends Feature {
	private CampaignScript campaignScript;
	private ExportDescrBuilding exportDescrBuilding;

	private List<String> factionsList;


	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		factionsList = FactionsDefs.allFactionsExceptRebelsList();

		val rootRegion = new RegionBlock(_CommentPrefix);

		rootRegion.add(createVariablesBlock());

		rootRegion.add(createAgentCountingMonitors());

		// ## write Faction Agents limiting monitors via CS Framework ##
		val csManager = new CampaignScriptManager(campaignScript);
		for (val factionName : factionsList) {

			val factionLimitingBlock = createFactionLimitingBody(factionName, AgentType.Assassin);
			csManager.insertAtEndOfPreFactionTurnStart(factionName, factionLimitingBlock);
		}
		campaignScript.insertAtEndOfFile(rootRegion);

		// ## Update Export Descr Buldings with AND event counter ... for agent
		updateAgentRecruitments();
	}

	private void updateAgentRecruitments() throws PatcherLibBaseEx {
		// Clear all assassins recruitments
		val agentAssassinRegex = "^\\s*agent\\s+assassin\\s+.*";

		exportDescrBuilding.removeBuildingCapabilitiesByRegex("taverns" , "tavern" , "city" , agentAssassinRegex);
		exportDescrBuilding.removeBuildingCapabilitiesByRegex("taverns" , "coaching_house" , "city" , agentAssassinRegex);
		exportDescrBuilding.removeBuildingCapabilitiesByRegex("taverns" , "pleasure_palace" , "city" , agentAssassinRegex);

		exportDescrBuilding.removeBuildingCapabilitiesByRegex("castle_academic" , "academy" , "castle" , agentAssassinRegex);


		val factions = FactionsDefs.allFactionsExceptRebelsList();

		val agentPrefix = "        agent assassin  0  requires factions { ";
		val agentSuffix = ", } and event_counter ";

		// Taverns
		for(val factionName : factions) {
			val agentLine = agentPrefix + factionName + agentSuffix + getAgentEventCounterFlag(factionName, AgentType.Assassin) + " 1";

			exportDescrBuilding.addCapabilities("taverns" , "tavern" , "city" , agentLine);
			exportDescrBuilding.addCapabilities("taverns" , "coaching_house" , "city" , agentLine);
			exportDescrBuilding.addCapabilities("taverns" , "pleasure_palace" , "city" , agentLine);
		}

		// Academy :
		val factionsAcademy = new ArrayList<String>();
		factionsAcademy.add("denmark");
		factionsAcademy.add("teutonic_order");
		factionsAcademy.add("norway");
		factionsAcademy.add("scotland");
		factionsAcademy.add("hungary");
		factionsAcademy.add("poland");
		for(val factionName : factionsAcademy) {
			val agentLine = agentPrefix + factionName + agentSuffix + getAgentEventCounterFlag(factionName, AgentType.Assassin) + " 1";

			exportDescrBuilding.addCapabilities("castle_academic" , "academy" , "castle" , agentLine);
		}

	}

	private ScriptBlock createFactionLimitingBody(String factionName, AgentType agentType) {
		val pc = new ContainerBlock();

		val eventCounter = getAgentEventCounterFlag(factionName, agentType);
		val agentVar = getAgentCountVariable(factionName, agentType);

		pc.add(new SetEventCounter(eventCounter, 1));

		int step = 5, agentLimit;
		int settlementsMin = 1;
		int settlemensMax = step;
		agentLimit = getAssasinsLimitBySettlementsNumber(settlemensMax);
		val firstif = new IfBlock(new NumberOfSettlements(factionName, "<=" , settlemensMax));
		firstif.andCondition(new CompareCounter(agentVar, ">=" , agentLimit));
		firstif.add(new SetEventCounter(eventCounter, 0));
		firstif.add(getLimitEnabledWriteHistory(agentType, factionName, settlementsMin, settlemensMax, agentLimit));

		pc.add(firstif);

		for (int i=2; i<=6; i++) {
			settlementsMin = (i-1) * step;
			settlemensMax = i*step;
			agentLimit = getAssasinsLimitBySettlementsNumber(settlemensMax);

			val ifBlock = new IfBlock(new NumberOfSettlements(factionName, ">" , settlementsMin));
			ifBlock.andCondition(new NumberOfSettlements(factionName, "<=" , settlemensMax));
			ifBlock.andCondition(new CompareCounter(agentVar, ">=" , agentLimit));	// getAssasinsLimitBySettlementsNumber(max)
			ifBlock.add(new SetEventCounter(eventCounter, 0));
			ifBlock.add(getLimitEnabledWriteHistory(agentType, factionName, settlementsMin, settlemensMax, agentLimit));

			pc.add(ifBlock);
		}

		settlementsMin = 6 * step;
		settlemensMax = 7 * step;
		val lastIf = new IfBlock(new NumberOfSettlements(factionName, ">" , settlementsMin));
		agentLimit = getAssasinsLimitBySettlementsNumber(settlemensMax);
		lastIf.andCondition(new CompareCounter(agentVar, ">=" , agentLimit));	// getAssasinsLimitBySettlementsNumber(7*step)
		lastIf.add(new SetEventCounter(eventCounter, 0));
		lastIf.add(getLimitEnabledWriteHistory(agentType, factionName, settlementsMin, settlemensMax, agentLimit));

		pc.add(lastIf);

		pc.add(new SetVariable(agentVar, 0));	// reset agent counter at start of every turn

		return pc;
	}

	private ScriptElement getLimitEnabledWriteHistory(AgentType agentType, String factionName, int settlementMin, int settlementMax, int agentLimit) {

		return new WriteToLog(LogLevel.Always,
				Ctm.format("Agents {0} LIMIT ENABLED for {1}: settlements ({2},{3}] agents max={4}",
										agentType, factionName, settlementMin, settlementMax, agentLimit));
	}

	private int getAssasinsLimitBySettlementsNumber(int settlementsNumber) {

		//return 10;

		if(settlementsNumber <= 5) return 3;

		int agentsMax = 3;
		settlementsNumber -= 5;

		agentsMax += settlementsNumber / 5;

		return agentsMax;
	}

	private ScriptBlock createAgentCountingMonitors() {
		val factionsAiIds = campaignScript.FactionsAiEcIds;

		val monitorsRegion = new RegionBlock(_CommentPrefix + ": Agents Counting Monitors");

		val agentFactionMonitor = new MonitorEventBlock(EventType.CharacterTurnEnd, new IsAgentType(AgentType.Assassin));

		// # For Local Player :
		IfBlock ifLocalPlayer = new IfBlock(new CompareCounter("ai_ec_id", "=" , 0));

		for(FactionAiEcId factionId : factionsAiIds) {
			IfBlock ifPlayerFactionIs = new IfBlock(new CompareCounter("pl_ec_id" , "=" , factionId.Id));
			val varName = getAgentCountVariable(factionId.Name , AgentType.Assassin);
			ifPlayerFactionIs.add(new IncrementVariable(varName, 1));

			ifLocalPlayer.add(ifPlayerFactionIs);
		}

		agentFactionMonitor.add(ifLocalPlayer);

		// ## Add AI :
		for (FactionAiEcId factionAiId : factionsAiIds) {
			IfBlock ifFaction = new IfBlock(new CompareCounter("ai_ec_id" , "=" , factionAiId.Id));

			val varName = getAgentCountVariable(factionAiId.Name , AgentType.Assassin);
			ifFaction.add(new IncrementVariable(varName , 1));
			agentFactionMonitor.add(ifFaction);
		}

		monitorsRegion.add(agentFactionMonitor);

		return monitorsRegion;
	}

	private ScriptBlock createVariablesBlock() {
		val variablesRegion = new RegionBlock(_CommentPrefix + ": Variables", true);

		for (val factionName : factionsList) {

			val varName = getAgentCountVariable(factionName , AgentType.Assassin);

			variablesRegion.add(new DeclareVariable(varName));
			variablesRegion.add(new SetVariable(varName , 0));
		}

		return  variablesRegion;
	}

	@SuppressWarnings("WeakerAccess")
	public static String getAgentCountVariable(String factionName, AgentType agentType) {
		return "AC_" + factionName +"_" + agentType +"_Count";
	}
	@SuppressWarnings("WeakerAccess")
	public static String getAgentEventCounterFlag(String factionName, AgentType agentType) {
		return "AC_IsAllowed_" + factionName +"_" + agentType +"_Count";
	}

	public AssasinsNumbersLimited() {
		super("Agents Numbers Limited");

		addCategory(CATEGORY_AGENTS);

		setDescriptionShort("Agents total numbers are Limited - crrently Assasins only");
		setDescriptionUrl("http://tmsship.wikidot.com/agents-numbers-limited");
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(AssasinsRemoved.Id);

		return conflicts;
	}

	private static String _CommentPrefix = "Assasins Numbers Limited";

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("c4b32ef2-3b8d-4738-9ea9-fce1267568c1");
}
