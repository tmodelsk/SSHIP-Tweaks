package tm.mtwModPatcher.sship.features.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.Tuple2;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.managers.UnitsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Smaller factions get survival bonuses
 */
public class FightForSurvival extends Feature {

	@Getter @Setter
	private double warningReplenishMult = 1.0;
	@Getter @Setter
	private double dangerReplenishMult = 1.20;
	@Getter @Setter
	private double criticalReplenishMult = 1.33;
	@Getter @Setter
	private int switchOffMoneyLevel = 20000;
	@Getter @Setter
	private int warningLevel = 6;
	@Getter @Setter
	private int dangerLevel = 4;
	@Getter @Setter
	private int criticalLevel = 2;


	@Override
	public void executeUpdates() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		createMonitorScripts();

		createBenefits();
	}

	protected void createBenefits() throws PatcherLibBaseEx {
		String requires, condition;
		List<Pattern> unitsToExclude = null;
		val unitReplenishBonusConditionList = new ArrayList<Tuple2<Double, String>>();

		// ###### WARNING Level #######
		condition = "event_counter " + EVENT_WARNING_NAME + " 1";
		requires = " requires " + condition;

		addFlatMoneyBonus(0.75, EVENT_WARNING_NAME, requires); // last : 0.5
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        law_bonus bonus 1" + requires);
		insertConstructionCostBonus(5, requires);
		insertConstructionTimeBonus(10, requires);

		val warningRepl = warningReplenishMult - 1.0;
		if(warningRepl > 0.0)
			unitReplenishBonusConditionList.add(new Tuple2<>(warningRepl, condition));


		// ###### DANGER Level #######
		condition = "event_counter " + EVENT_DANGER_NAME + " 1";
		requires = " requires " + condition;

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        law_bonus bonus 2" + requires);                    // +20 % Law
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        population_growth_bonus bonus 2" + requires);    // +0.5 % Pop Growth
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        trade_base_income_bonus bonus 1" + requires);    // +1 Trade bonus
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        religion_level bonus 1" + requires);            // +1 Religion
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_morale_bonus bonus 1" + requires);        // +1 Morale
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_exp_bonus bonus 1" + requires);        // +1 Experience

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        free_upkeep bonus 1" + requires);                // +2 Free Upkeep
		insertConstructionCostBonus(10, requires);
		insertConstructionTimeBonus(6, requires);

		val dangerRepl = dangerReplenishMult -1.0;
		if(dangerRepl > 0.0) unitReplenishBonusConditionList.add(new Tuple2<>(dangerRepl, condition));

		addFlatMoneyBonus(1.25, EVENT_DANGER_NAME, requires);	// last 1.0

		// ###### CRITICAL Level #######
		condition = "event_counter " + EVENT_CRITICAL_NAME + " 1";
		requires = " requires " + condition;

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        law_bonus bonus 4" + requires);                    // +20 % Law
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        population_growth_bonus bonus 3" + requires);    // +1 % Pop Growth
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        trade_base_income_bonus bonus 2" + requires);    // +2 Trade bonus
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        religion_level bonus 2" + requires);            // +2 Religion
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_morale_bonus bonus 2" + requires);        // +2 Morale
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_exp_bonus bonus 2" + requires);        // +2 Experience
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        free_upkeep bonus 2" + requires);                // +2 Free Upkeep
		// +1 Recruitment slot
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruitment_slots 1 requires not event_counter freeze_recr_pool 1 and event_counter " + EVENT_CRITICAL_NAME + " 1");
		insertConstructionCostBonus(20, requires);
		insertConstructionTimeBonus(4, requires);

		val criticalRepl = criticalReplenishMult - 1.0;
		if(criticalRepl > 0.0) unitReplenishBonusConditionList.add(new Tuple2<>(criticalRepl, condition));

		addFlatMoneyBonus(2.0 , EVENT_CRITICAL_NAME , requires);

		// ## Final processing ##
		unitsManager.enableFreeUpkeepAllUnits(null, exportDescrUnit);
		unitsManager.addReplenishBonusEntryWithCondition(unitReplenishBonusConditionList, unitsToExclude, exportDescrBuilding);
	}

	private void addFlatMoneyBonus(double factor, String eventName, String requires) {
		val moneyBonuses = exportDescrBuilding.addFlatCityCastleIncome(requires, factor);    // Bonuses : [250 , 375 , 562 , 843 , 1264] with factor 1.0
		consoleLogger.writeLine(Ctm.msgFormat("[{0}]: {1} Flat money bonus: {2}[{3}], {4}[{5}]",
				FEATURE_NAME, eventName,
				moneyBonuses.get(0).getItem1(), Ctm.toCsv(moneyBonuses.get(0).getItem2()),
				moneyBonuses.get(1).getItem1(), Ctm.toCsv(moneyBonuses.get(1).getItem2())));
	}
	private void insertConstructionCostBonus(int bonus, String requires) {
		//construction_cost_bonus_stone bonus 90 requires event_counter is_the_ai 1
		//construction_cost_bonus_wooden bonus 90 requires event_counter is_the_ai 1
		String constructionStoneBonus = "        construction_cost_bonus_stone bonus ";
		String constructionWoodenBonus = "        construction_cost_bonus_wooden bonus ";

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities(constructionStoneBonus + bonus + requires);
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities(constructionWoodenBonus + bonus + requires);
	}
	private void insertConstructionTimeBonus(int turnsBonus, String requires) {

		int bonus = (int) ((100.0 / (double)turnsBonus) +1);

		// #### CONSTRUCTION TIMES ######
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_time_bonus_other bonus " + bonus + requires);
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_time_bonus_religious bonus " + bonus + requires);
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("construction_time_bonus_defensive bonus " + bonus + requires);
	}

	protected void createMonitorScripts() throws PatcherLibBaseEx {
		int insertLine = campaignScript.getLines().findExpFirstRegexLine("^\\s*wait_monitors") - 1;

		String str = "";

		str += nl + nl;
		str += "; ### Fight for survival - faction scripts ###" + nl + nl;

		str += getMonitorForFactions(FactionsDefs.allFactionsList(), warningLevel, dangerLevel, criticalLevel);

		campaignScript.getLines().insertAt(insertLine, str);
	}

	protected String getMonitorForFactions(List<String> factionNames,
										   	int settlementsCountWarning, int settlementCountDanger, int settlementCountCritical) {
		String str = "";

		for (String facionName : factionNames) {
			str += getMonitorForFaction(facionName, settlementsCountWarning, settlementCountDanger, settlementCountCritical);
		}

		return str;
	}

	protected String getMonitorForFaction(String factionName,
										  	int settlementsCountWarning, int settlementCountDanger, int settlementCountCritical) {
		String str = "";

		str += "monitor_event PreFactionTurnStart FactionType " + factionName  +nl+nl;
		// + " and Treasury < " + switchOffMoneyLevel // - not working

		str += "    set_event_counter " + EVENT_WARNING_NAME + " 0" + nl;
		str += "    set_event_counter " + EVENT_DANGER_NAME + " 0" + nl;
		str += "    set_event_counter " + EVENT_CRITICAL_NAME + " 0" + nl + nl;

		str += "  if I_NumberOfSettlements " + factionName + " <= " + settlementsCountWarning + nl;
		str += "   and I_NumberOfSettlements " + factionName + " > " + settlementCountDanger + nl;
		str += "    set_event_counter " + EVENT_WARNING_NAME + " 1" + nl;
		str += "  end_if" + nl;

		str += "  if I_NumberOfSettlements " + factionName + " <= " + settlementCountDanger + nl;
		str += "   and I_NumberOfSettlements " + factionName + " > " + settlementCountCritical + nl;
		str += "    set_event_counter " + EVENT_DANGER_NAME + " 1" + nl;
		str += "  end_if" + nl;

		str += "  if I_NumberOfSettlements " + factionName + " <= " + settlementCountCritical + nl;
		str += "    set_event_counter " + EVENT_CRITICAL_NAME + " 1" + nl;
		str += "  end_if" + nl;

		str += "end_monitor" + nl;

		return str;
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdDouble("WarningReplenishMult" , "Warning Replenish Mult",
				feature -> ((FightForSurvival)feature).getWarningReplenishMult(),
				(feature, value) -> ((FightForSurvival)feature).setWarningReplenishMult(value)));

		pars.add(new ParamIdDouble("DangerReplenishMult" , "Danger Replenish Mult",
				feature -> ((FightForSurvival)feature).getDangerReplenishMult(),
				(feature, value) -> ((FightForSurvival)feature).setDangerReplenishMult(value)));

		pars.add(new ParamIdDouble("CriticalReplenishMult" , "Critical Replenish Mult",
				feature -> ((FightForSurvival)feature).getCriticalReplenishMult(),
				(feature, value) -> ((FightForSurvival)feature).setCriticalReplenishMult(value)));

		pars.add(new ParamIdInteger("SwitchOffMoneyLevel" , "Switch Off Money Level",
				feature -> ((FightForSurvival)feature).getSwitchOffMoneyLevel(),
				(feature, value) -> ((FightForSurvival)feature).setSwitchOffMoneyLevel(value)));

		pars.add(new ParamIdInteger("WarningSettlementsCount" , "Warning Settlements Count",
				feature -> ((FightForSurvival)feature).getWarningLevel(),
				(feature, value) -> ((FightForSurvival)feature).setWarningLevel(value)));

		pars.add(new ParamIdInteger("DangerSettlementsCount" , "Danger Settlements Count",
				feature -> ((FightForSurvival)feature).getDangerLevel(),
				(feature, value) -> ((FightForSurvival)feature).setDangerLevel(value)));

		pars.add(new ParamIdInteger("CriticalSettlementsCount" , "Critical Settlements Count",
				feature -> ((FightForSurvival)feature).getCriticalLevel(),
				(feature, value) -> ((FightForSurvival)feature).setCriticalLevel(value)));



		return pars;
	}

	protected CampaignScript campaignScript;
	protected ExportDescrBuilding exportDescrBuilding;
	private ExportDescrUnitTyped exportDescrUnit;
	private UnitsManager unitsManager;

	protected String nl = System.lineSeparator();

	protected static final String EVENT_WARNING_NAME = "fight_for_survival_warning";
	protected static final String EVENT_DANGER_NAME = "fight_for_survival_danger";
	protected static final String EVENT_CRITICAL_NAME = "fight_for_survival_critical";
	protected static final String FEATURE_NAME = "FightForSurvival";

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public FightForSurvival(UnitsManager unitsManager) {
		super("Fight for survival");

		addCategory("Economy");
		addCategory("Campaign");

		setDescriptionShort("Fight for survival - Faction gets bonuses when they need to fight for survival (is very small)");

		setDescriptionUrl("http://tmsship.wikidot.com/fight-for-survival");

		this.unitsManager = unitsManager;
	}
}
