package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.common.entities.AgentType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.AddMoney;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.IncrementKingsPurse;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.LogLevel;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.TraitName;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.CommentLine;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.engines.FactionExpensesAdditional;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.data._root.ExportDescrCharacterTraits;
import tm.mtwModPatcher.lib.data.text.ExportVnvs;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.FactionAiEcId;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** Additional army supplies costs when in enemy lands */
public class ArmySuppliesCosts extends Feature {

	@Override
	public void setParamsCustomValues() {
		fullStackFullCost = 3000;
		aiMultiplier = 0.75;
		loggingEnabled = true;
		cumansExcluded = true;
		siegeCostDisabled = true;
	}

	@Override
	public void executeUpdates() throws Exception {

		edCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);
		dStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		eVnvs = getFileRegisterForUpdated(ExportVnvs.class);

		FactionExpensesAdditional factionExpenses = new FactionExpensesAdditional(campaignScript);
		factionExpenses.ensureFactionVariablesExist();
		factionExpenses.ensureLocalKingsPurseCompensateExists();

		makeUpdatesBasedOnTraitsArmySize();

		if (siegeCostDisabled) removeSSHIPBesiegeUpkeepScript();
	}

	public void makeUpdatesBasedOnTraitsArmySize() throws PatcherLibBaseEx {
		boolean isForLocalFactionOnly = false;

		addArmySizeTraits();
		addArmySizeTriggers(isForLocalFactionOnly, EventType.CharacterTurnStart);
		addArmySizeTraitsDescriptions(fullStackFullCost);

		RegionBlock rootRegion = new RegionBlock("Army Costs in Enemy lands monitors");

		List<Condition> conditions = Arrays.asList(new IsInEnemyLands());
		if (isForLocalFactionOnly)
			conditions.add(new CharacterIsLocal());
		// new TurnNumber(">",0)  , new IsNotBesieging()

		// ## Captains Groups: Charge ONLY with small amount (1/20) -> unable to determine captain group ssize
		// ## Captain group could be one unit, could be full stack
		val captainsBlock = createArmyMonitorsForCaptains(fullStackFullCost / 10); // cost of 2 units on long campaign
		rootRegion.add(captainsBlock);

		// ## Create Army Size monitors based on InEnemyRegion Trait
		val conditionsPlusInEnemyRegion0 = createConditionsPlusInEnemyRegion(0, conditions);
		val conditionsPlusInEnemyRegion1 = createConditionsPlusInEnemyRegion(1, conditions);
		val conditionsPlusInEnemyRegion2 = createConditionsPlusInEnemyRegion(2, conditions);
		val conditionsPlusInEnemyRegion3 = createConditionsPlusInEnemyRegion(3, conditions);

		// rootRegion.add(createArmyMonitorsForAllArmySizes(fullStackFullCost, conditions));	// original
		rootRegion.add(createArmyMonitorsForAllArmySizes(fullStackFullCost * 0.25, conditionsPlusInEnemyRegion0, "InEnemyRegion 0"));
		rootRegion.add(createArmyMonitorsForAllArmySizes(fullStackFullCost * 0.50, conditionsPlusInEnemyRegion1, "InEnemyRegion 1"));
		rootRegion.add(createArmyMonitorsForAllArmySizes(fullStackFullCost * 0.75, conditionsPlusInEnemyRegion2, "InEnemyRegion 2"));
		rootRegion.add(createArmyMonitorsForAllArmySizes(fullStackFullCost * 1.00, conditionsPlusInEnemyRegion3, "InEnemyRegion 3"));

		List<String> resultLines = rootRegion.getScriptBlock().getLines();

		campaignScript.insertAtEndOfFile(resultLines);
	}

	// Adds Trait InEnemyLands [Level] to input Conditions list
	private List<Condition> createConditionsPlusInEnemyRegion(int level, List<Condition> conditions) {

		val conditionsPlus = new ArrayList<Condition>();
		conditionsPlus.addAll(conditions);

		if (level == 0) {
			conditionsPlus.add(new NotTraitCondition(TraitName.InEnemyRegion, ">", 0));
		} else if (level > 0) {
			conditionsPlus.add(new TraitCondition(TraitName.InEnemyRegion, "=", level));
		}

		return conditionsPlus;
	}

	// Creates monitor for captains comnnaders - very special one
	private ContainerBlock createArmyMonitorsForCaptains(double captainCost) {
		val container = new ContainerBlock();

		// ## Captains Groups: Charge ONLY with small amount (1/20) -> unable to determine captain group ssize
		// ## Captain group could be one unit, could be full stack
		val captainsConditions = new ArrayList<Condition>();
		captainsConditions.add(new IsGeneral());
		captainsConditions.add(new IsInEnemyLands());
		captainsConditions.add(new IsAgentType(AgentType.General));
		captainsConditions.add(new NotTraitCondition("ArmySize", ">", 0));    // na wszelki wypadek

		container.add(new CommentLine("Captains (not NamedChar, not Family Member) in EnemyLands Charging"));
		container.add(createArmySizeMonitor(0, captainCost, captainsConditions, "Captain"));

		return container;
	}

	// Creates monitors for all ArmySizes & Generals basing on input conditions
	private ScriptBlock createArmyMonitorsForAllArmySizes(double fullCost, List<Condition> conditions, String commentSuffix) {
		ContainerBlock container = new ContainerBlock();

		// ## Charge ONLY Named Chars & Family Members in Enem Lands, not important is commander or not
		val namedCharsAndFamilyConditions = new ArrayList<Condition>();
		namedCharsAndFamilyConditions.addAll(conditions);
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.Priest));
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.Princess));
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.Merchant));
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.Spy));
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.Assassin));
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.Diplomat));
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.Admiral));
		namedCharsAndFamilyConditions.add(new IsNotAgentType(AgentType.General));

		container.add(new CommentLine("All NamedChars & FamilyMembers in EnemyLands Charging (Not IsGeneral), not important is commander or not, " + commentSuffix));
		container.add(createArmySizeMonitor(0, fullCost * 0.24, namedCharsAndFamilyConditions, "NotGeneral " + commentSuffix));

		// ### Army Size Monitors: Charging commanders (IsGeneral) ###
		val conditionsPlusIsGeneral = new ArrayList<Condition>();
		// ### Charging based on IsGeneral & ArmySize (NamedChars & FamilyMembers") ###
		conditionsPlusIsGeneral.addAll(conditions);
		conditionsPlusIsGeneral.add(new IsGeneral());

		container.add(new CommentLine("Charging based on IsGeneral & ArmySize (NamedChars & Family Members), " + commentSuffix));
		// # 0.25 * fullCost is already charged by commander NamedChar or FamilyMember #
		container.add(createArmySizeMonitor(2, fullCost * 0.25, conditionsPlusIsGeneral, "General, ArmySize2, " + commentSuffix));
		container.add(createArmySizeMonitor(3, fullCost * 0.50, conditionsPlusIsGeneral, "General, ArmySize3, " + commentSuffix));
		container.add(createArmySizeMonitor(4, fullCost * 0.75, conditionsPlusIsGeneral, "General, ArmySize4, " + commentSuffix));

		return container;
	}

	private MonitorEventBlock createArmySizeMonitor(int armySize, double cost, List<Condition> conditions) {
		return createArmySizeMonitor(armySize, cost, conditions, "");
	}

	private MonitorEventBlock createArmySizeMonitor(int armySize, double cost, List<Condition> conditions, String commentSuffix) {

		List<Condition> tmp = new ArrayList<>(conditions);    // make copy
		EventType monitorEvent = EventType.CharacterTurnStart;

		// ### Monitor Event with given conditions list AND ArmySize Trait
		MonitorEventBlock monitor = new MonitorEventBlock(monitorEvent,
				tmp.stream().collect(Collectors.toList()));

		if (armySize > 0)
			monitor.andCondition(new TraitCondition("ArmySize", "=", armySize));
		else if (armySize == 0) {
			// Do nothing //monitor.and(new NotTraitCondition("ArmySize" , ">" , 0));
		} else throw new PatcherLibBaseEx("Not implemented");

		List<FactionAiEcId> factions = campaignScript.FactionsAiEcIds;

		// ## Proceed Local Faction ##
		IfBlock ifLocal = new IfBlock(new CompareCounter("ai_ec_id", "=", 0));
		int costInt = (int) cost;
		for (FactionAiEcId faction : factions) {
			if (cumansExcluded & faction.Name.equals("cumans")) continue;

			IfBlock ifLocalFaction = new IfBlock(new CompareCounter("pl_ec_id", "=", faction.Id));

			ifLocalFaction.add(new IncrementKingsPurse(faction.Name, -costInt));
			ifLocalFaction.add(new IncrementVariable(FactionExpensesAdditional.getFactionKingsPurseCompensateVariable(faction.Name), costInt));
			if (loggingEnabled)
				ifLocalFaction.add(new WriteToLog(LogLevel.Always, monitorEvent + " Army Supplies Costs : " + costInt + " : Local " + faction.Name + ", " + commentSuffix));

			ifLocal.add(ifLocalFaction);
		}
		monitor.add(ifLocal);

		// ## Proceed AI's ##
		for (FactionAiEcId faction : factions) {
			if (cumansExcluded & faction.Name.equals("cumans")) continue;

			int aiCost = (int) (aiMultiplier * cost);
			IfBlock ifAi = new IfBlock(new CompareCounter("ai_ec_id", "=", faction.Id));
			ifAi.add(new AddMoney(faction.Name, -aiCost));
			if (loggingEnabled)
				ifAi.add(new WriteToLog(LogLevel.Always, monitorEvent + " Army Supplies Costs : " + aiCost + " : " + faction.Name + ", " + commentSuffix));
			monitor.add(ifAi);
		}

		return monitor;
	}

	private void addArmySizeTraits() throws PatcherLibBaseEx {

		LinesProcessor lines = edCharacterTraits.getLines();
		int index = lines.findFirstByRexexLines("^;=+ VNV TRAITS START HERE ", "^;=+;");
		if (index < 0) throw new PatcherLibBaseEx("Unable to find start of traits");
		index += 2;

		String str = "", nl = System.lineSeparator();

		// ##### TRAIT Army Size - from BGR IV #####

		str += ";--------------------------------------------" + nl;
		str += ";----- TM Patcher Added : Army Size Traits --" + nl;
		str += "Trait ArmySize" + nl;
		str += " Characters family" + nl;
		str += " AntiTraits ArmySizeReset" + nl;
		str += "" + nl;
		str += " Level ArmySize_1" + nl;
		str += "   Description ArmySize_1_desc" + nl;
		str += "   EffectsDescription ArmySize_1_effects_desc" + nl;
		str += "   Threshold  1" + nl;
		str += "" + nl;
		str += "   Effect MovementPoints  1" + nl;
		str += "" + nl;

		str += "Level ArmySize_2" + nl;
		str += "Description ArmySize_2_desc" + nl;
		str += "EffectsDescription ArmySize_2_effects_desc" + nl;
		str += "Threshold  2" + nl;
		str += "" + nl;
		str += "Effect MovementPoints  2" + nl;
		str += "" + nl;
		str += "Level ArmySize_3" + nl;
		str += "Description ArmySize_3_desc" + nl;
		str += "EffectsDescription ArmySize_3_effects_desc" + nl;
		str += "Threshold  3" + nl;
		str += "" + nl;
		str += "Effect MovementPoints  3" + nl;
		str += "" + nl;
		str += "Level ArmySize_4" + nl;
		str += "Description ArmySize_4_desc" + nl;
		str += "EffectsDescription ArmySize_4_effects_desc" + nl;
		str += "Threshold  4" + nl;
		str += "" + nl;
		str += "Effect MovementPoints  4" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;
		str += "Trait ArmySizeReset" + nl;
		str += "Characters family" + nl;
		str += "AntiTraits ArmySize" + nl;
		str += "" + nl;
		str += "Level ArmySize_Reset" + nl;
		str += "Description ArmySize_Reset_desc" + nl;
		str += "EffectsDescription ArmySize_Reset_effects_desc" + nl;
		str += "Threshold  1" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;
		str += "" + nl;

		lines.insertAt(index, str);
	}

	private void addArmySizeTraitsDescriptions(int fullStackCost) throws PatcherLibBaseEx {
		LinesProcessor lines = eVnvs.getLines();

		int insertIndex = lines.findExpFirstRegexLine("Listens_Teachings");

		String str = "", nl = System.lineSeparator();

		str += "{ArmySize_1}	Army Size is 1/4" + nl;
		str += "{ArmySize_1_desc}	Army Size & Field Costs are 1/4 of Full Stack Army." + nl;
		str += "{ArmySize_1_effects_desc}	Army Field Costs " + getArmySizeTriggerCostCsvDescr(1, fullStackCost) + nl;

		str += "{ArmySize_2}	Army Size is 1/2" + nl;
		str += "{ArmySize_2_desc}	Army Size & Field Costs are 1/2 of Full Stack Army." + nl;
		str += "{ArmySize_2_effects_desc}	Army Field Costs " + getArmySizeTriggerCostCsvDescr(2, fullStackCost) + nl;

		str += "{ArmySize_3}	Army Size is 3/4" + nl;
		str += "{ArmySize_3_desc}	Army Size & Field Costs are 3/4 of Full Stack Army." + nl;
		str += "{ArmySize_3_effects_desc}	Army Field Costs " + getArmySizeTriggerCostCsvDescr(3, fullStackCost) + nl;

		str += "{ArmySize_4}	Army Size is Fullstack" + nl;
		str += "{ArmySize_4_desc}	Army Size & Field Costs are Full Stack Army." + nl;
		str += "{ArmySize_4_effects_desc}	Army Field Costs " + getArmySizeTriggerCostCsvDescr(4, fullStackCost) + nl;

		str += "{ArmySize_Reset}	ArmySize_Reset" + nl;
		str += "{ArmySize_Reset_desc}	unseen" + nl;
		str += "{ArmySize_Reset_effects_desc}	unseen" + nl;

		lines.insertAt(insertIndex, str);
	}

	private String getArmySizeTriggerCostCsvDescr(int armySize, int fullStackCost) {
		int fullCost = (int) (fullStackCost / (4.0 / armySize));

		String str = "";


		str += "(" + (int) (fullCost * 0.25);
		str += "," + (int) (fullCost * 0.50);
		str += "," + (int) (fullCost * 0.75);
		str += "," + (int) (fullCost * 1.00);
		str += ")";

		return str;
	}

	private void addArmySizeTriggers(boolean onlyForLocal, EventType characterEventType) throws PatcherLibBaseEx {
		LinesProcessor lines = edCharacterTraits.getLines();
		String str = "", nl = System.lineSeparator();
		int index;

		String isLocalCondition = "and CharacterIsLocal" + nl;

		// ### Triggers for ARMY SIZE ###
		str += ";----------------------------------------------" + nl;
		str += ";----- TM Patcher Added : Army Size Triggers --" + nl;

		str += "Trigger SizeReset4" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "and Trait ArmySize = 4" + nl;
		str += "" + nl;
		str += "Affects ArmySizeReset  5  Chance  100" + nl;
		str += "Affects ArmySize  1  Chance  100" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;

		str += "Trigger SizeReset3" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "and Trait ArmySize = 3" + nl;
		str += "" + nl;
		str += "Affects ArmySizeReset  4  Chance  100" + nl;
		str += "Affects ArmySize  1  Chance  100" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;

		str += "Trigger SizeReset2" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "and Trait ArmySize = 2" + nl;
		str += "" + nl;
		str += "Affects ArmySizeReset  3  Chance  100" + nl;
		str += "Affects ArmySize  1  Chance  100" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;

		str += "Trigger SizeReset1" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "and Trait ArmySize = 1" + nl;
		str += "" + nl;
		str += "Affects ArmySizeReset  2  Chance  100" + nl;
		str += "Affects ArmySize  1  Chance  100" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;

		str += "Trigger SizeSet1" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "	and InEnemyLands" + nl;
		if (onlyForLocal)
			str += isLocalCondition;
		str += "	and not EndedInSettlement" + nl;
		str += "and PercentageUnitAttribute general_unit >= 20" + nl;
		str += "" + nl;
		str += "Affects ArmySize  1  Chance  100" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;

		str += "Trigger SizeSet2" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "	and InEnemyLands" + nl;
		if (onlyForLocal)
			str += isLocalCondition;
		str += "	and not EndedInSettlement" + nl;
		str += "and PercentageUnitAttribute general_unit >= 10" + nl;
		str += "and PercentageUnitAttribute general_unit < 20" + nl;
		str += "" + nl;
		str += "Affects ArmySize  2  Chance  100" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;

		str += "Trigger SizeSet3" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "	and InEnemyLands" + nl;
		if (onlyForLocal)
			str += isLocalCondition;
		str += "	and not EndedInSettlement" + nl;
		str += "and PercentageUnitAttribute general_unit >= 6.5" + nl;    // 7
		str += "and PercentageUnitAttribute general_unit < 10" + nl;
		str += "" + nl;
		str += "Affects ArmySize  3  Chance  100" + nl;
		str += "" + nl;
		str += ";------------------------------------------" + nl;

		str += "Trigger SizeSet4" + nl;
		str += "WhenToTest " + characterEventType + nl;
		str += "" + nl;
		str += "Condition IsGeneral" + nl;
		str += "	and InEnemyLands" + nl;
		if (onlyForLocal)
			str += isLocalCondition;
		str += "	and not EndedInSettlement" + nl;
		str += "and PercentageUnitAttribute general_unit < 6.5" + nl;    // 7
		str += "" + nl;
		str += "Affects ArmySize  4  Chance  100" + nl;
		str += ";----- TM Patcher Added : Army Size Triggers END --" + nl;

		index = lines.count();
		lines.insertAt(index, str);
	}

	// Removes SSHIP Besieging -1000 Script from campaign_script
	private int removeSSHIPBesiegeUpkeepScript() throws PatcherLibBaseEx {
		LinesProcessor lines = campaignScript.getLines();

		// ### Remove Existing Besiege upkeep ###
		// ;================== BESIEGE UPKEEP ======================
		int indexStart = lines.findFirstRegexLine("^\\s*;=+ BESIEGE UPKEEP ");
		if (indexStart < 0) throw new PatcherLibBaseEx("== BESIEGE UPKEEP == script not found");
		int indexEnd = lines.findFirstRegexLine("^\\s*end_monitor", indexStart + 1);
		if (indexEnd < 0) throw new PatcherLibBaseEx("== BESIEGE UPKEEP == end script not found");

		lines.removeRange(indexStart, indexEnd);

		return indexStart;
	}

	// ## Parameters api for GUI App ##
	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		parIds.add(new ParamIdInteger("FullSizeFullCost", "Full Size Full Cost",
				feature -> ((ArmySuppliesCosts) feature).getFullStackFullCost(),
				(feature, value) -> ((ArmySuppliesCosts) feature).setFullStackFullCost(value)));

		parIds.add(new ParamIdDouble("AICostsMultiplier", "AI Costs Multiplier",
				feature -> ((ArmySuppliesCosts) feature).getAiMultiplier(),
				(feature, value) -> ((ArmySuppliesCosts) feature).setAiMultiplier(value)));

		parIds.add(new ParamIdBoolean("IsCumansExcluded", "Is Cumans Excluded",
				feature -> ((ArmySuppliesCosts) feature).isCumansExcluded(),
				(feature, value) -> ((ArmySuppliesCosts) feature).setCumansExcluded(value)));

		parIds.add(new ParamIdBoolean("IsLoggingEnabled", "Is Logging Enabled",
				feature -> ((ArmySuppliesCosts) feature).isLoggingEnabled(),
				(feature, value) -> ((ArmySuppliesCosts) feature).setLoggingEnabled(value)));

		parIds.add(new ParamIdBoolean("IsSiegeCostDisabled", "Is SSHIP Siege Cost Disabled",
				feature -> ((ArmySuppliesCosts) feature).isSiegeCostDisabled(),
				(feature, value) -> ((ArmySuppliesCosts) feature).setSiegeCostDisabled(value)));

		return parIds;
	}

	@Getter @Setter private int fullStackFullCost;
	@Getter @Setter private double aiMultiplier;
	@Getter @Setter private boolean loggingEnabled;
	@Getter @Setter private boolean cumansExcluded;
	@Getter @Setter private boolean siegeCostDisabled;

	protected ExportDescrCharacterTraits edCharacterTraits;
	protected CampaignScript campaignScript;
	protected DescrStratSectioned dStrat;
	protected ExportVnvs eVnvs;

	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.fromString("f10312bd-99a3-424e-800c-9ebc83930cf5");

	/** Additional amry supplies costs when in enemy lands 	 */
	public ArmySuppliesCosts() {
		super("Army Supplies Costs");

		addCategory("Economy");
		addCategory("Campaign");

		setDescriptionShort("Army Supplies Costs when in Enemy Territory");
		setDescriptionUrl("http://tmsship.wikidot.com/army-supplies-costs");

		addOverrideTask(new OverrideDeleteFilesTask("data\\text\\export_VnVs.txt.strings.bin"));
	}
}
