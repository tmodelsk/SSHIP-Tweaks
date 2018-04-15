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
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character.CharacterIsLocal;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.character.IsTargetRegionOneOf;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.FactionType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.ISettlementUnderSiege;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement.SettlementOwner;
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

public class GarrisonOnAssaultRaisingPlayerVsAi extends Feature {
	@Override
	public void setParamsCustomValues() {
		populationRecoveryTurns = 15;
		minimumLoyaltyLevel = 1;
		createUnitsLogging = ConfigurationSettings.isDevEnvironment();
		garrisonSize = GarrisonSize.SMALL;
		version = "0.5";
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

		MonitorEventBlock monitor = new MonitorEventBlock(EventType.GeneralAssaultsResidence, new IsTargetRegionOneOf(settlement.ProvinceName));
		monitor.andCondition(new ISettlementUnderSiege(settlement.Name));
		monitor.andCondition(new CharacterIsLocal());
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

		for (UnitGarrisonInfo unit : units) validateUnitInFaction(unit.Name, factionName, settlement);

		val isMedium = garrisonSize == GarrisonSize.MEDIUM || settlement.Resources.contains("capital");

		if (settlement.Level == SettlementLevel.L1_Village) {    // SMALL = 0 , MEDIUM = 1
			if (isMedium)    // medium only
				iff.add(createRaiseUnits(factionName, settlement, units, 1) );

		} else if (settlement.Level == SettlementLevel.L2_Town) {    // SMALL = 0 , MEDIUM = 2
			if (isMedium)
				iff.add(createRaiseUnits(factionName, settlement, units, 1, 2));	// 1, 2

		} else if (settlement.Level == SettlementLevel.L3_LargeTown) {    // SMALL = 1 , MEDIUM = 3
			if (isMedium) 	iff.add(createRaiseUnits(factionName, settlement, units, 1, 3 ));	// 1, 2, 3
			else 			iff.add(createRaiseUnits(factionName, settlement, units, 1 ));    		// 1

		} else if (settlement.Level == SettlementLevel.L4_City) {    // SMALL = 2 , MEDIUM = 4
			if (isMedium) 	iff.add(createRaiseUnits(factionName, settlement, units, 1, 4 ));	// 1, 2, 3, 4
			else			iff.add(createRaiseUnits(factionName, settlement, units,3, 4 ));	// 3, 4

		} else if (settlement.Level == SettlementLevel.L5_LargeCity) {        // SMALL = 3 , MEDIUM = 5
			if (isMedium)	iff.add(createRaiseUnits(factionName, settlement, units,1, 5)); // 1, 2, 3, 4 ,5
			else 			iff.add(createRaiseUnits(factionName, settlement, units,3, 5)); // 3, 4, 5

		} else if (settlement.Level == SettlementLevel.L6_HugeCity) {    // SMALL = 4 , MEDIUM = 6
			if (isMedium)	iff.add(createRaiseUnits(factionName, settlement, units, 1, 6));	// 1 - 6
			else			iff.add(createRaiseUnits(factionName, settlement, units, 3, 6));	// 3, 4, 5, 6

		} else
			throw new PatcherLibBaseEx(Ctm.format("Settlement level {0} not supported", settlement.Level));

		if (iff.isBodyEmpty()) iff = null;
		return iff;
	}

	@SuppressWarnings("unused")
	private ScriptBlock createRaiseUnits(String factionName, SettlementInfo settlement, UnitGarrisonInfo ... units) throws PatcherLibBaseEx {
		return createRaiseUnits(factionName, settlement, Arrays.asList(units));
	}
	private ScriptBlock createRaiseUnits(String factionName, SettlementInfo settlement, List<UnitGarrisonInfo> units, int unitNo) {
		return createRaiseUnits(factionName, settlement, units.subList(unitNo-1, unitNo));
	}
	private ScriptBlock createRaiseUnits(String factionName, SettlementInfo settlement, List<UnitGarrisonInfo> units, int startNo, int endNo) throws PatcherLibBaseEx {
		return createRaiseUnits(factionName, settlement, units.subList(startNo-1, endNo));
	}
	@SuppressWarnings("StringConcatenationInLoop")
	private ScriptBlock createRaiseUnits(String factionName, SettlementInfo settlement, List<UnitGarrisonInfo> units) throws PatcherLibBaseEx {
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

		if (createUnitsLogging)
			block.add(new WriteToLog(LogLevel.Always, "### "+ garrisonScriptCommentPrefix +": " + settlement.Name + "[" + settlement.Level + "] : " + factionName + " : Creating (" + unitsCostSum + " fl) [" + units.size() + "]:" + unitsNamesCsv));

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
		String recoveryTurnsVar;

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

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		parIds.add(new ParamIdInteger("PopulationRecoveryTurns", "Population Recovery Turns",
				feature -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).getPopulationRecoveryTurns(),
				(feature, value) -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).setPopulationRecoveryTurns(value)));

		parIds.add(new ParamIdInteger("MinimumLoyaltyLevel", "Minimum Loyalty Level",
				feature -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).getMinimumLoyaltyLevel(),
				(feature, value) -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).setMinimumLoyaltyLevel(value)));

		parIds.add(new ParamIdBoolean("CreateUnitsLogging", "Create Units Logging",
				feature -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).isCreateUnitsLogging(),
				(feature, value) -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).setCreateUnitsLogging(value)));

		parIds.add(new ParamIdString("GarrisonSize", "Garrison Size",
				feature -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).getGarrisonSize().toString(),
				(feature, value) -> ((GarrisonOnAssaultRaisingPlayerVsAi) feature).setGarrisonSize(value)));

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

	private static String garrisonScriptCommentPrefix = "Garrison Script on Assault PL-AI";
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
	public static UUID Id = UUID.fromString("dfa002ed-eb35-4da0-ae1a-6f96ee91d844");

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(GarrisonOnSiegeRaising.Id);
		conflicts.add(GarrisonOnAssaultRaising.Id);
		conflicts.add(GarrisonOnButton.Id);

		return conflicts;
	}
	public GarrisonOnAssaultRaisingPlayerVsAi(GarrisonManager garrisonManager) {
		super("Garrison script : raise unit on Player-AI assault");

		addCategory("Campaign");
		setDescriptionShort(Ctm.format("Garrison script : raise unit on Player vs AI assault v.{0}", version));
		setDescriptionUrl("http://tmsship.wikidot.com/garrison-script");

		this.garrisonManager = garrisonManager;
	}
}
