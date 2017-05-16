package tm.m2twModPatcher.sship.global;

import tm.common.Ctm;
import tm.common.Tuple2;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.managers.UnitsManager;

import java.util.List;
import java.util.UUID;

/** Smaller factions get survival bonuses */
public class FightForSurvival extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		createMonitorScripts();

		createBenefits();
	}

	protected void createBenefits() throws PatcherLibBaseEx {
		List<Tuple2<String, List<Integer>>> moneyBonuses;

		String requires;
		// ###### WARNING Level #######
		requires = " requires event_counter "+ EVENT_WARNING_NAME +" 1";
		// last : 0.5
		moneyBonuses = exportDescrBuilding.addFlatCityCastleIncome(requires , 0.75);	// Bonuses : [250 , 375 , 562 , 843 , 1264] with factor 1.0
		consoleLogger.writeLine(Ctm.msgFormat("[{0}]: {1} Flat money bonus: {2}[{3}], {4}[{5}]",
				FEATURE_NAME, EVENT_WARNING_NAME,
				moneyBonuses.get(0).getItem1(), Ctm.toCsv(moneyBonuses.get(0).getItem2()),
				moneyBonuses.get(1).getItem1(), Ctm.toCsv(moneyBonuses.get(1).getItem2())));

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        law_bonus bonus 1" + requires);

		// ###### DANGER Level #######
		requires = " requires event_counter "+ EVENT_DANGER_NAME +" 1";

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        law_bonus bonus 2" + requires);					// +20 % Law
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        population_growth_bonus bonus 2" + requires);	// +0.5 % Pop Growth
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        trade_base_income_bonus bonus 1" + requires);	// +1 Trade bonus
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        religion_level bonus 1" + requires);			// +1 Religion
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_morale_bonus bonus 1" + requires);		// +1 Morale
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_exp_bonus bonus 1" + requires);		// +1 Experience

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        free_upkeep bonus 1" + requires);				// +2 Free Upkeep

		moneyBonuses = exportDescrBuilding.addFlatCityCastleIncome(requires , 1.25 );	// last 1.0
		consoleLogger.writeLine(Ctm.msgFormat("[{0}]: {1} Flat money bonus: {2}[{3}], {4}[{5}]",
				FEATURE_NAME, EVENT_DANGER_NAME,
				moneyBonuses.get(0).getItem1(), Ctm.toCsv(moneyBonuses.get(0).getItem2()),
				moneyBonuses.get(1).getItem1(), Ctm.toCsv(moneyBonuses.get(1).getItem2())));

		// ###### CRITICAL Level #######
		requires = " requires event_counter "+ EVENT_CRITICAL_NAME +" 1";

		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        law_bonus bonus 4" + requires);					// +20 % Law
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        population_growth_bonus bonus 3" + requires);	// +1 % Pop Growth
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        trade_base_income_bonus bonus 2" + requires);	// +2 Trade bonus
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        religion_level bonus 2" + requires);			// +2 Religion
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_morale_bonus bonus 2" + requires);		// +2 Morale
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruits_exp_bonus bonus 2" + requires);		// +2 Experience
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        free_upkeep bonus 2" + requires);				// +2 Free Upkeep
		// +1 Recruitment slot
		exportDescrBuilding.insertIntoCityCastleWallsCapabilities("        recruitment_slots 1 requires not event_counter freeze_recr_pool 1 and event_counter "+ EVENT_CRITICAL_NAME +" 1");

		moneyBonuses = exportDescrBuilding.addFlatCityCastleIncome(requires , 2.0);
		consoleLogger.writeLine(Ctm.msgFormat("[{0}]: {1} Flat money bonus: {2}[{3}], {4}[{5}]",
				FEATURE_NAME, EVENT_CRITICAL_NAME,
				moneyBonuses.get(0).getItem1(), Ctm.toCsv(moneyBonuses.get(0).getItem2()),
				moneyBonuses.get(1).getItem1(), Ctm.toCsv(moneyBonuses.get(1).getItem2())));

		unitsManager.enableFreeUpkeepAllUnits(null, exportDescrUnit);
	}

	protected void createMonitorScripts() throws PatcherLibBaseEx {
		int insertLine = campaignScript.getLines().findExpFirstRegexLine("^wait_monitors")-1;

		String str="";

		str += nl+nl;
		str += "; ### Fight for survival - faction scripts ###" + nl+nl;

		str += getMonitorForFactions(FactionsDefs.allFactionsList() , 5 , 3 , 1);

		campaignScript.getLines().insertAt(insertLine, str);
	}

	protected String getMonitorForFactions(List<String> factionNames, int settlementsCountWarning, int settlementCountDanger, int settlementCountCritical) {
		String str = "";

		for (String facionName : factionNames) {
			str += getMonitorForFaction(facionName, settlementsCountWarning, settlementCountDanger, settlementCountCritical);
		}

		return str;
	}

	protected String getMonitorForFaction(String factionName, int settlementsCountWarning, int settlementCountDanger , int settlementCountCritical) {
		String str="";

		str += "monitor_event PreFactionTurnStart FactionType " + factionName +nl+nl;

		str += "    set_event_counter " + EVENT_WARNING_NAME + " 0" +nl;
		str += "    set_event_counter " + EVENT_DANGER_NAME + " 0" +nl;
		str += "    set_event_counter " + EVENT_CRITICAL_NAME + " 0" +nl+nl;

		str += "  if I_NumberOfSettlements "+factionName + " <= " + settlementsCountWarning +nl;
		str += "   and I_NumberOfSettlements "+factionName + " > " + settlementCountDanger +nl;
		str += "    set_event_counter " + EVENT_WARNING_NAME + " 1" +nl;
		str += "  end_if" +nl;

		str += "  if I_NumberOfSettlements "+factionName + " <= " + settlementCountDanger +nl;
		str += "   and I_NumberOfSettlements "+factionName + " > " + settlementCountCritical +nl;
		str += "    set_event_counter " + EVENT_DANGER_NAME + " 1" +nl;
		str += "  end_if" +nl;

		str += "  if I_NumberOfSettlements "+factionName + " <= " + settlementCountCritical +nl;
		str += "    set_event_counter " + EVENT_CRITICAL_NAME + " 1" +nl;
		str += "  end_if" +nl;

		str += "end_monitor" +nl;

		return str;
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
