package tm.mtwModPatcher.sship.features.garrisons;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdString;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.AddMoney;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.LogLevel;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character.GovernorInResidence;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.FactionExcommunicated;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.FactionType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.IsNotLocalFaction;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.DeclareVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.SetVariable;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.managers.SettlementManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonManager;
import tm.mtwModPatcher.lib.managers.garrisons.UnitGarrisonInfo;

import java.util.*;
import java.util.stream.Collectors;

public class GarrisonOnSiegeRaising  extends Feature {
	@Override
	public void setParamsCustomValues() {
		populationRecoveryTurns = 15;
		minimumLoyaltyLevel = 1;
		createUnitsLogging = true;
		garrisonSize = GarrisonSize.SMALL;
		version = "1.00";
	}

	@Override
	public void executeUpdates() throws Exception {
		initFileEntities();

		List<SettlementInfo> settlementInfos = settlementManager.getAllSettlements();
		if (garrisonSize == GarrisonSize.SMALL)
			settlementInfos = settlementInfos.stream()
					.filter(si -> si.Resources.contains("capital")
							|| !(si.Level.equals(SettlementLevel.L1_Village) || si.Level.equals(SettlementLevel.L2_Town)))
					.collect(Collectors.toList());

		RegionBlock rootRegion = new RegionBlock(Ctm.format("{0} v.{1} MAIN REGION -----", garrisonScriptCommentPrefix, version));

		// ### Create Settlements Variables Declarations & Initializations ###
		rootRegion.add(createSettlementsVariablesInitializations(settlementInfos));

		// ### Create Global Monitor : Increment Settlement Variables - once per turn
		rootRegion.add(createIncrementSettlVariablesMonitor(settlementInfos));

		// ### Create Monitors for each individual Settlement ###

		rootRegion.add(createSettlementsTroopRisingMonitors(settlementInfos));

		// ## Insert all generated monitors ##
		campaignScript.insertAtEndOfFile(rootRegion.getScriptBlock().getLines());
	}

	private RegionBlock createSettlementsTroopRisingMonitors(List<SettlementInfo> settlementInfos) throws Exception {
		RegionBlock region = new RegionBlock(garrisonScriptCommentPrefix + ": Settlements individual monitors");

		for (SettlementInfo settlement : settlementInfos) {
			val settMonitor = createSettlementTroopRisingMonitor(settlement);
			if (settMonitor != null) region.add(settMonitor);
		}

		return region;
	}

	private MonitorEventBlock createSettlementTroopRisingMonitor(SettlementInfo settlement) throws Exception {
		String settlRecoveryVar = getSettlementRecoveryVariableName(settlement);

		MonitorEventBlock monitor = new MonitorEventBlock(EventType.SettlementTurnEnd, new SettlementName(settlement.Name));
		monitor.andCondition(new ISettlementUnderSiege(settlement.Name));
		monitor.andCondition(new GovernorInResidence());
		monitor.andCondition(new SettlementLoyaltyLevel(">=", getMinimumLoyaltyLevelLiteral(minimumLoyaltyLevel)));
		monitor.andCondition(new FactionExcommunicated().not());
		monitor.andCondition(new SettlementHasPlague().not());
		monitor.andCondition(new CompareCounter(settlRecoveryVar, ">", populationRecoveryTurns));

		for (String factionName : FactionsDefs.allFactionsList()) {
			val ifBLock = createFactionIfBlock(factionName, settlement);
			if (ifBLock != null) monitor.add(ifBLock);
		}

		if (monitor.isBodyEmpty())
			return null;

		monitor.add(new SetVariable(settlRecoveryVar, 0)); // reset siege mobilization counter

		return monitor;
	}

	private IfBlock createFactionIfBlock(String factionName, SettlementInfo settlement) throws Exception {
		IfBlock iff = new IfBlock(new SettlementOwner(settlement.Name, factionName));

		val units = garrisonManager.getUnits(settlement, factionName);
		if (units == null || units.size() == 0)
			throw new PatcherLibBaseEx("No units for " + settlement.Level + " Faction: " + factionName);

		for (UnitGarrisonInfo unit : units) {
			validateUnitInFaction(unit.Name, factionName, settlement);
		}

		UnitGarrisonInfo unit1 = units.get(0);

		val isMedium = garrisonSize == GarrisonSize.MEDIUM || settlement.Resources.contains("capital");

		if (settlement.Level == SettlementLevel.L1_Village) {    // SMALL = 0 , MEDIUM = 1

			if (isMedium)    // medium only
				iff.add(createRaiseUnits(factionName, settlement, unit1));
		} else if (settlement.Level == SettlementLevel.L2_Town) {    // SMALL = 0 , MEDIUM = 2

			if (isMedium) {    // medium only
				UnitGarrisonInfo unit2 = units.get(1);
				iff.add(createRaiseUnits(factionName, settlement, unit1, unit2));
			}
		} else if (settlement.Level == SettlementLevel.L3_LargeTown) {    // SMALL = 1 , MEDIUM = 3
			UnitGarrisonInfo unit2 = units.get(1);
			UnitGarrisonInfo unit3 = units.get(2);

			iff.add(createRaiseUnits(factionName, settlement, unit1));    // small & medium

			// ## Unit 1
			if (isMedium) {    // medium only
				// Czy defender to AI ??
				IfBlock ifDefenderAI = new IfBlock(new IsNotLocalFaction(factionName));
				ifDefenderAI.add(createRaiseUnits(factionName, settlement, unit2, unit3));    // ## Unit 2 & 3

				iff.add(ifDefenderAI);
			}
		} else if (settlement.Level == SettlementLevel.L4_City) {    // SMALL = 2 , MEDIUM = 4
			UnitGarrisonInfo unit2 = units.get(1);
			UnitGarrisonInfo unit3 = units.get(2);
			UnitGarrisonInfo unit4 = units.get(3);

			// ## Unit 1 i 2
			if (isMedium)    // Medium only
				iff.add(createRaiseUnits(factionName, settlement, unit1, unit2));

			// Czy defender to AI ??
			IfBlock ifDefenderAI = new IfBlock(new IsNotLocalFaction(factionName));
			ifDefenderAI.add(createRaiseUnits(factionName, settlement, unit3, unit4));    // ## Unit 3

			iff.add(ifDefenderAI);
		} else if (settlement.Level == SettlementLevel.L5_LargeCity) {        // SMALL = 3 , MEDIUM = 5
			UnitGarrisonInfo unit2 = units.get(1);
			UnitGarrisonInfo unit3 = units.get(2);
			UnitGarrisonInfo unit4 = units.get(3);
			UnitGarrisonInfo unit5 = units.get(4);

			// ## Unit 1 i 2 i 3
			if (isMedium)        // Medium only
				iff.add(createRaiseUnits(factionName, settlement, unit1, unit2, unit3));
			else
				iff.add(createRaiseUnits(factionName, settlement, unit3));

			// Czy defender to AI ??
			IfBlock ifDefenderAI = new IfBlock(new IsNotLocalFaction(factionName));
			ifDefenderAI.add(createRaiseUnits(factionName, settlement, unit4, unit5));    // ## Unit 4

			iff.add(ifDefenderAI);
		} else if (settlement.Level == SettlementLevel.L6_HugeCity) {    // SMALL = 4 , MEDIUM = 6
			UnitGarrisonInfo unit2 = units.get(1);
			UnitGarrisonInfo unit3 = units.get(2);
			UnitGarrisonInfo unit4 = units.get(3);
			UnitGarrisonInfo unit5 = units.get(4);
			UnitGarrisonInfo unit6 = units.get(5);

			// ## Unit 1 i 2 i 3 i 4
			if (isMedium)
				iff.add(createRaiseUnits(factionName, settlement, Arrays.asList(unit1, unit2, unit3, unit4)));
			else
				iff.add(createRaiseUnits(factionName, settlement, unit3, unit4));

			// Czy defender to AI ??
			IfBlock ifDefenderAI = new IfBlock(new IsNotLocalFaction(factionName));
			ifDefenderAI.add(createRaiseUnits(factionName, settlement, unit5, unit6));    // ## Unit 5 & 6

			iff.add(ifDefenderAI);
		} else throw new PatcherLibBaseEx(Ctm.format("Settlement level {0} not supported", settlement.Level));

		if (iff.isBodyEmpty()) iff = null;

		return iff;
	}

	private ContainerBlock createRaiseUnits(String factionName, SettlementInfo settlement, UnitGarrisonInfo unit) throws PatcherLibBaseEx {
		return createRaiseUnits(factionName, settlement, Collections.singletonList(unit));
	}
	private ContainerBlock createRaiseUnits(String factionName, SettlementInfo settlement, UnitGarrisonInfo unit1, UnitGarrisonInfo unit2)
		throws PatcherLibBaseEx {
		val list = new ArrayList<UnitGarrisonInfo>();
		list.add(unit1);
		list.add(unit2);

		return createRaiseUnits(factionName, settlement, list);
	}
	private ContainerBlock createRaiseUnits(String factionName, SettlementInfo settlement, UnitGarrisonInfo unit1, UnitGarrisonInfo unit2, UnitGarrisonInfo unit3)
			throws PatcherLibBaseEx {
		val list = new ArrayList<UnitGarrisonInfo>();
		list.add(unit1);
		list.add(unit2);
		list.add(unit3);

		return createRaiseUnits(factionName, settlement, list);
	}
	@SuppressWarnings("StringConcatenationInLoop")
	private ContainerBlock createRaiseUnits(String factionName, SettlementInfo settlement, List<UnitGarrisonInfo> units) throws PatcherLibBaseEx {
		ContainerBlock block = new ContainerBlock();

		String unitsNamesCsv = "";
		int unitsCostSum = 0;

		UnitGarrisonInfo unitLast = units.get(0);
		for (int i = 1; i < units.size(); i++) {
			UnitGarrisonInfo unit = units.get(i);

			if (unitLast.equals(unit)) {
				val experienceTag = unitLast.Experience > 0 ? "+" + unitLast.Experience : "";
				unitLast.Quantity += unit.Quantity;
				unitsNamesCsv += unit.Name + experienceTag + ",";
			} else {
				block.add("create_unit " + settlement.Name + ", " + unitLast.Name + ", num " +
						unitLast.Quantity + ", exp " + unitLast.Experience + ", arm " + unitLast.Armor + ", wep " + unitLast.Weapon);

				val experienceTag = unitLast.Experience > 0 ? "+" + unitLast.Experience : "";
				unitsNamesCsv += unitLast.Name + experienceTag + ",";

				if (!factionName.equals("slave"))
					unitsCostSum += calculateUnitCost(unitLast.Name) * unitLast.Quantity;

				unitLast = unit;
			}
		}
		block.add("create_unit " + settlement.Name + ", " + unitLast.Name + ", num " +
				unitLast.Quantity + ", exp " + unitLast.Experience + ", arm " + unitLast.Armor + ", wep " + unitLast.Weapon);
		val experienceTag = unitLast.Experience > 0 ? "+" + unitLast.Experience : "";
		unitsNamesCsv += unitLast.Name + experienceTag + ",";

		if (!factionName.equals("slave"))
			unitsCostSum += calculateUnitCost(unitLast.Name) * unitLast.Quantity;

		if (unitsCostSum > 0)
			block.add(new AddMoney(factionName, -unitsCostSum));

		if (createUnitsLogging) {
			val debugEntry = Ctm.format("### {0}: {1} [{2}]: {3}: Creating ({4}fl),[{5}]:{6}",
					garrisonScriptCommentPrefix, settlement.Name, settlement.Level, factionName, unitsCostSum, units.size(), unitsNamesCsv);
			block.add(new WriteToLog(LogLevel.Always, debugEntry));
		}

		return block;
	}

	private ScriptBlock createIncrementSettlVariablesMonitor(List<SettlementInfo> settlementInfos) {
		RegionBlock region = new RegionBlock(garrisonScriptCommentPrefix + ": Increment All settlement variables, once per turn");
		MonitorEventBlock monitor = new MonitorEventBlock(EventType.PreFactionTurnStart, new FactionType("slave"));

		for (SettlementInfo sInfo : settlementInfos) {
			monitor.add(new IncrementVariable(getSettlementRecoveryVariableName(sInfo), 1));
		}
		region.add(monitor);

		return region;
	}
	private ScriptBlock createSettlementsVariablesInitializations(List<SettlementInfo> settlementInfos) {
		String recoveryTurnsVar, conditionVar;

		RegionBlock region = new RegionBlock(garrisonScriptCommentPrefix + ": Settlement Recovery Variables", true);

		for (SettlementInfo settl : settlementInfos) {
			recoveryTurnsVar = getSettlementRecoveryVariableName(settl);

			region.add(new DeclareVariable(recoveryTurnsVar));
			region.add(new SetVariable(recoveryTurnsVar, populationRecoveryTurns * 2));
		}
		return region;
	}

	private String getSettlementRecoveryVariableName(SettlementInfo settlementInfo) {
		return settlementInfo.Name + "_LastSiegeMobilizationTurnsCount";
	}
	private int calculateUnitCost(String unitName) throws PatcherLibBaseEx {
		return (int) (edu.loadUnit(unitName).StatCost.Cost * 0.5);
	}

	private void validateUnitInFaction(String unitName, String factionName, SettlementInfo settlement) throws PatcherLibBaseEx {
		String ownershipCsv = edu.loadUnit(unitName).Ownership;

		if (!ownershipCsv.contains("all") && !ownershipCsv.contains(factionName))
			throw new PatcherLibBaseEx("Unit " + unitName + " don't belongs to " + factionName + " Settl: " + settlement.Name + " .Level=" + settlement.Level + " .Creator=" + settlement.CreatedByFaction);
	}

	private String getMinimumLoyaltyLevelLiteral(int level) {
		String res;

		if (level == 1) res = SettlementLoyaltyLevel.Happy1;
		else if (level == 2) res = SettlementLoyaltyLevel.Content2;
		else if (level == 3) res = SettlementLoyaltyLevel.Disillusioned3;
		else if (level == 4) res = SettlementLoyaltyLevel.Rioting4;
		else if (level == 5) res = SettlementLoyaltyLevel.Revolting5;
		else throw new PatcherLibBaseEx("Unexpected");

		return res;
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		parIds.add(new ParamIdInteger("PopulationRecoveryTurns", "Population Recovery Turns",
				feature -> ((GarrisonOnSiegeRaising) feature).getPopulationRecoveryTurns(),
				(feature, value) -> ((GarrisonOnSiegeRaising) feature).setPopulationRecoveryTurns(value)));

		parIds.add(new ParamIdInteger("MinimumLoyaltyLevel", "Minimum Loyalty Level",
				feature -> ((GarrisonOnSiegeRaising) feature).getMinimumLoyaltyLevel(),
				(feature, value) -> ((GarrisonOnSiegeRaising) feature).setMinimumLoyaltyLevel(value)));

		parIds.add(new ParamIdBoolean("CreateUnitsLogging", "Create Units Logging",
				feature -> ((GarrisonOnSiegeRaising) feature).isCreateUnitsLogging(),
				(feature, value) -> ((GarrisonOnSiegeRaising) feature).setCreateUnitsLogging(value)));

		parIds.add(new ParamIdString("GarrisonSize", "Garrison Size",
				feature -> ((GarrisonOnSiegeRaising) feature).getGarrisonSize().toString(),
				(feature, value) -> ((GarrisonOnSiegeRaising) feature).setGarrisonSize(value)));

		return parIds;
	}

	private void initFileEntities() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		settlementManager = new SettlementManager(descrStrat, descrRegions);
	}

	@Getter @Setter private int populationRecoveryTurns = 15;
	@Getter @Setter private int minimumLoyaltyLevel = 1;
	@Getter @Setter private boolean createUnitsLogging;
	@Getter private GarrisonSize garrisonSize = GarrisonSize.SMALL;
	private void setGarrisonSize(String strSize) {
		strSize = strSize.toUpperCase();
		if (strSize.equals("SMALL")) garrisonSize = GarrisonSize.SMALL;
		else if (strSize.equals("MEDIUM")) garrisonSize = GarrisonSize.MEDIUM;

		else throw new PatcherLibBaseEx(Ctm.format("Garrison Size param value {0} not supported", strSize));
	}

	private static String garrisonScriptCommentPrefix = "Garrison Script on Siege";
	@SuppressWarnings("unused")
	private static String nl = System.lineSeparator();
	private CampaignScript campaignScript;
	private DescrStratSectioned descrStrat;
	private DescrRegions descrRegions;
	private ExportDescrUnitTyped edu;
	private SettlementManager settlementManager;
	private GarrisonManager garrisonManager;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("a52478cc-dd5f-4295-943b-3941c7047d77");

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(GarrisonOnAssaultRaising.Id);
		conflicts.add(GarrisonOnButton.Id);
		conflicts.add(GarrisonOnAssaultRaisingPlayerVsAi.Id);

		return conflicts;
	}

	public GarrisonOnSiegeRaising(GarrisonManager garrisonManager) {
		super("Garrison script : raise unit on siege");

		addCategory("Campaign");
		setDescriptionShort(Ctm.format("Garrison script : raise unit on siege v.{0}", version));
		setDescriptionUrl("http://tmsship.wikidot.com/garrison-script");

		this.garrisonManager = garrisonManager;

		createUnitsLogging = ConfigurationSettings.isDevEnvironment();
	}
}
