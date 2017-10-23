package tm.mtwModPatcher.sship.features.global.rimlandHeartland;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.Tuple2;
import tm.common.Tuple3;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.*;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ContainerBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.IfBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.RegionBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.DeclareVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.SetEventCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.SetVariable;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.FactionAiEcId;
import tm.mtwModPatcher.lib.managers.CampaignScriptManager;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.managers.SettlementManager;
import tm.mtwModPatcher.sship.lib.Buildings;
import tm.mtwModPatcher.sship.lib.HiddenResources;
import tm.mtwModPatcher.sship.lib.Provinces;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by tomek on 24.06.2017.
 */
public class RimlandHeartland extends Feature {

	@Getter @Setter
	private boolean islandHiddenResource = true;

	private int seaTradeNothing = 0;
	private int seaTradeSmall = 4;
	private int seaTradeMedium = 8;
	private int seaTradeLarge = 12;
	private int noSeaTradePenalty = -3;

	private int logLevel = 2;
	private boolean debugMode = false;

	@Override
	public void executeUpdates() throws Exception {
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);

		atlanticHarderToTrade();
		rimlandTrade();
		seaTradeBonusesForHeartland();
		landGates();
	}

	private void seaTradeBonusesForHeartland() {

		createSeaTradeBonusesScripts();

		val city = new ArrayList<Tuple3<Integer, Integer, Integer>>();
		// 1 - Corn Exchange
		city.add(new Tuple3<>(1, 1, 1));
		city.add(new Tuple3<>(1, 2, 1));
		city.add(new Tuple3<>(1, 3, 2));

		// 2 - Market
		city.add(new Tuple3<>(2, 1, 1));
		city.add(new Tuple3<>(2, 2, 2));
		city.add(new Tuple3<>(2, 3, 3));

		// 3 - Fairground
		city.add(new Tuple3<>(3, 1, 1));
		city.add(new Tuple3<>(3, 2, 2));
		city.add(new Tuple3<>(3, 3, 3));

		// 4 - Great Market
		city.add(new Tuple3<>(4, 1, 2));
		city.add(new Tuple3<>(4, 2, 3));
		city.add(new Tuple3<>(4, 3, 4));

		// 5 - Merchant Quarter
		city.add(new Tuple3<>(5, 1, 2));
		city.add(new Tuple3<>(5, 2, 3));
		city.add(new Tuple3<>(5, 3, 4));

		val castle = new ArrayList<Tuple3<Integer, Integer, Integer>>();
		// 1 - Corn Exchange
		castle.add(new Tuple3<>(1, 1, 1));
		castle.add(new Tuple3<>(1, 2, 1));
		castle.add(new Tuple3<>(1, 3, 1));

		// 2 - Market
		castle.add(new Tuple3<>(2, 1, 1));
		castle.add(new Tuple3<>(2, 2, 1));
		castle.add(new Tuple3<>(2, 3, 2));

		// 3 - Fairground
		castle.add(new Tuple3<>(3, 1, 1));
		castle.add(new Tuple3<>(3, 2, 2));
		castle.add(new Tuple3<>(3, 3, 3));

		// Level 0 - some minus
		for (int i = 1; i <= 5; i++)
			addSingleSeaTradeMinus(SettlType.City, i, noSeaTradePenalty);
		for (int i = 1; i <= 3; i++)
			addSingleSeaTradeMinus(SettlType.Castle, i, noSeaTradePenalty);

		// # Insert into Market's capabilities - Levels 1 - 3
		city.forEach(t -> addSingleSeaTradeBonus(SettlType.City, t.getItem1(), t.getItem2(), t.getItem3()));
		castle.forEach(t -> addSingleSeaTradeBonus(SettlType.Castle, t.getItem1(), t.getItem2(), t.getItem3()));
	}

	private void addSingleSeaTradeBonus(SettlType settlType, int marketLevel, int seaTradeLevel, int bonusValue) {

		val bonusType = debugMode ? ExportDescrBuilding.IncomeBonus : ExportDescrBuilding.TradeBonus;

		addSingleSeaTradeBonus(settlType, marketLevel, seaTradeLevel, bonusValue, bonusType);
	}

	private void addSingleSeaTradeBonus(SettlType settlType, int marketLevel, int seaTradeLevel, int bonusValue, String bonusTypeStr) {
		val islandCondition = islandHiddenResource ? Ctm.msgFormat(" and not hidden_resource {0}", HR_ISLAND) : "";

		String bonusBaseTemplate = bonusTypeStr + "{0}";
		bonusBaseTemplate += " requires event_counter {1} 1" + islandCondition;
		bonusBaseTemplate += " and not building_present_min_level {2} {3}";    // not port
		bonusBaseTemplate += " and hidden_resource " + HiddenResources.River;    // building_present_min_level river_access river_port
		bonusBaseTemplate += " or building_present_min_level {4} {5}";    // or roads

		val bonusCityTemplate = Ctm.msgFormat(bonusBaseTemplate, "{0}", "{1}", "port", "port", "hinterland_roads", "roads");
		val bonusCastleTemplate = Ctm.msgFormat(bonusBaseTemplate, "{0}", "{1}", "castle_port", "c_port", "hinterland_castle_roads", "c_roads");

		String bonusTemplate;
		String buildingName;
		List<String> marketLevels;

		if (settlType == SettlType.City) {
			bonusTemplate = bonusCityTemplate;
			buildingName = "market";
			marketLevels = Buildings.MarketCityLevels;
		} else if (settlType == SettlType.Castle) {
			bonusTemplate = bonusCastleTemplate;
			buildingName = "market_castle";
			marketLevels = Buildings.MarketCastleLevels;
		} else throw new PatcherLibBaseEx("Unexpected!: " + settlType);

		val bonusLine = Ctm.msgFormat(bonusTemplate, bonusValue, getSeaTradeBonusEventName(seaTradeLevel));

		edb.insertIntoBuildingCapabilities(buildingName, marketLevels.get(marketLevel - 1), settlType, bonusLine);
	}

	private void addSingleSeaTradeMinus(SettlType settlType, int marketLevel, int bonusNerfOffset) {
		val islandCondition = islandHiddenResource ? Ctm.msgFormat("hidden_resource {0}", HR_ISLAND) : "";
		val portCondition = "building_present_min_level " + (settlType.equals(SettlType.City) ? "port port" : "castle_port c_port");
		val seaTradeEvent = getSeaTradeBonusEventName(0);

		String bonusBaseTemplate="";
		bonusBaseTemplate += debugMode ? ExportDescrBuilding.IncomeBonus : ExportDescrBuilding.TradeBonus;
		bonusBaseTemplate += "{0}";
		bonusBaseTemplate += " requires {1} event_counter " + seaTradeEvent + " {2}";

		String bonusLine;
		String buildingName;
		List<String> marketLevels;

		if (settlType == SettlType.City) {
			buildingName = "market";
			marketLevels = Buildings.MarketCityLevels;
		} else if (settlType == SettlType.Castle) {
			buildingName = "market_castle";
			marketLevels = Buildings.MarketCastleLevels;
		} else throw new PatcherLibBaseEx("Unexpected!: " + settlType);
		val marketLevelName = marketLevels.get(marketLevel-1);

		// ## Find original bonus
		val lines = edb.getLines();
		val capabilitiesIndexes = edb.getBuildingCapabilitiesStartEnd(buildingName, marketLevels.get(marketLevel-1), settlType.toLabelString());
		//         trade_base_income_bonus bonus 4
		val pattern = Pattern.compile("^\\s*trade_base_income_bonus\\s+bonus\\s+(\\d+)\\s*$");
		val lineIndex = lines.findExpFirstRegexLine(pattern, capabilitiesIndexes);
		val orgLine = lines.getLine(lineIndex);
		val matcher = pattern.matcher(orgLine);
		if(matcher.find()) {
			val bonusOrg = Integer.parseInt(matcher.group(1));

			int bonusNerfed = bonusOrg + bonusNerfOffset;
			if(bonusNerfed <= 0) bonusNerfed = 1;
			int bonusValue;

			// Original bonus when SeaTradeEvent0 = 0
			bonusLine = bonusBaseTemplate;
			bonusLine = Ctm.msgFormat(bonusLine, bonusOrg, "not" , 1);
			edb.insertIntoBuildingCapabilities(buildingName, marketLevelName, settlType, bonusLine);

			// Original bonus when SeaTradeEvent0 = 1 and island or port
			bonusLine = bonusBaseTemplate + " and " + islandCondition + " or " + portCondition;
			bonusValue = debugMode ? bonusOrg * 10 : bonusOrg;
			bonusLine = Ctm.msgFormat(bonusLine,bonusValue , "" , 1);
			edb.insertIntoBuildingCapabilities(buildingName, marketLevelName, settlType, bonusLine);

			// Nerfed bonus when SeaTradeEvent0 = 1 and not island and not port
			bonusLine = bonusBaseTemplate + " and not " + islandCondition + " and not " + portCondition;
			bonusValue = debugMode ? bonusNerfed * 100 : bonusNerfed;
			bonusLine = Ctm.msgFormat(bonusLine, bonusValue, "" , 1);
			edb.insertIntoBuildingCapabilities(buildingName, marketLevelName, settlType, bonusLine);

			lines.remove(lineIndex);

		} else throw new PatcherUnexpectedEx("WTF ?");
	}

	private void createSeaTradeBonusesScripts() {
		val csManager = new CampaignScriptManager(campaignScript);
		val factions = FactionsDefs.allFactionsExceptRebelsList();

		// ### Declare port counter variables
		val variabledDeclarations = new RegionBlock("Ports heartland trade boost - declarations");
		factions.forEach(f -> variabledDeclarations.add(new DeclareVariable(getSeaTradeCounterVar(f))));
		campaignScript.insertAtEndOfFile(variabledDeclarations);

		// ### Pre Faction Turn Start - decide about bonuses depending on conuters from previous turn
		val factionChooseBlock = new RegionBlock("Ports heartland trade boost - decisions");

		// ## Reset Bonuses Flags EventCounters
		factionChooseBlock.add(new SetEventCounter(getSeaTradeBonusEventName(0), 0));
		factionChooseBlock.add(new SetEventCounter(getSeaTradeBonusEventName(1), 0));
		factionChooseBlock.add(new SetEventCounter(getSeaTradeBonusEventName(2), 0));
		factionChooseBlock.add(new SetEventCounter(getSeaTradeBonusEventName(3), 0));

		val factionsAiIds = campaignScript.FactionsAiEcIds;
		// ## Process Player
		IfBlock ifLocalPlayer = new IfBlock(new CompareCounter("ai_ec_id", "=", 0));
		for (FactionAiEcId factionId : factionsAiIds) {
			IfBlock ifPlayerFactionIs = new IfBlock(new CompareCounter("pl_ec_id", "=", factionId.Id));

			ifPlayerFactionIs.add(getSeaTradeBonusesScriptBodyForFaction(factionId.Name));
			ifLocalPlayer.add(ifPlayerFactionIs);
		}
		factionChooseBlock.add(ifLocalPlayer);

		// ## Process AIs
		for (val factionAiId : factionsAiIds) {
			val ifFaction = new IfBlock(new CompareCounter("ai_ec_id", "=", factionAiId.Id));

			ifFaction.add(getSeaTradeBonusesScriptBodyForFaction(factionAiId.Name));
			factionChooseBlock.add(ifFaction);
		}
		csManager.insertAtEndOfPreFactionNotSlaveTurnStart(factionChooseBlock);

		// ### Settlement End Turn Monitor - counts ports for faction
		val factionsVariables = new HashMap<String, String>();
		factionsAiIds.forEach(f -> factionsVariables.put(f.Name, getSeaTradeCounterVar(f.Name)));

		val settlementManager = new SettlementManager(descrStrat, descrRegions);
		val monitorBuilder = new PortMonitorCounterBuilder(factionsVariables, campaignScript, settlementManager);
		monitorBuilder.setSeashoreProvinces(seashoreProvinces);
		monitorBuilder.setIslandProvinces(islands.getList().stream().map(i -> i.getItem1()).collect(Collectors.toList()));
		monitorBuilder.setLogLevel(logLevel);

		val portCityLevels = Buildings.PortCityLevels;
		val portCastleLevels = Buildings.PortCastleLevels;
		val seaTradeCityLevels = Buildings.SeaTradeCityLevels;

		val monitorsBlock = new RegionBlock("Ports heartland trade boost: Port counting monitors");

		monitorsBlock.add(monitorBuilder.createRoadOrRiverPortFlagMonitors());

		// Ports
		for (int i = 0; i <= 2; i++) {
			val operator = i < 2 ? "=" : ">=";
			monitorsBlock.add(monitorBuilder.createMainCounter(portCityLevels.get(i), operator, i + 1));
			monitorsBlock.add(monitorBuilder.createMainCounter(portCastleLevels.get(i), operator, i + 1));
		}

		// Sea Trade - Merchant Stores - First level same for castle / city
		monitorsBlock.add(monitorBuilder.createMainCounter(seaTradeCityLevels.get(0), "=", 1));
		monitorsBlock.add(monitorBuilder.createMainCounter(seaTradeCityLevels.get(1), "=", 2));
		monitorsBlock.add(monitorBuilder.createMainCounter(seaTradeCityLevels.get(2), ">=", 3));

		campaignScript.insertAtEndOfFile(monitorsBlock);
	}

	private ContainerBlock getSeaTradeBonusesScriptBodyForFaction(String factionName) {
		val body = new ContainerBlock();
		val factionVar = getSeaTradeCounterVar(factionName);

		IfBlock ifBlock;

		ifBlock = new IfBlock(new CompareCounter(factionVar, "=", seaTradeNothing));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(0), 1));
		if(logLevel >= 1)
			ifBlock.add(new WriteToLog(Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 0)));
		body.add(ifBlock);

		ifBlock = new IfBlock(new CompareCounter(factionVar, ">=", seaTradeSmall));
		ifBlock.andCondition(new CompareCounter(factionVar, "<", seaTradeMedium));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(1), 1));
		if(logLevel >= 1)
			ifBlock.add(new WriteToLog(Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 1)));
		body.add(ifBlock);

		ifBlock = new IfBlock(new CompareCounter(factionVar, ">=", seaTradeMedium));
		ifBlock.andCondition(new CompareCounter(factionVar, "<", seaTradeLarge));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(2), 1));
		if(logLevel >= 1)
			ifBlock.add(new WriteToLog(Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 2)));
		body.add(ifBlock);

		ifBlock = new IfBlock(new CompareCounter(factionVar, ">=", seaTradeLarge));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(3), 1));
		if(logLevel >= 1)
			ifBlock.add(new WriteToLog(Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 3)));
		body.add(ifBlock);

		body.add(new SetVariable(factionVar, 0));

		return body;
	}

	private String getSeaTradeCounterVar(String factionName) {
		return "SeaTradePoints_" + factionName;
	}

	private String getSeaTradeBonusEventName(int l) {
		return SEA_TRADE_BONUS + l;
	}

	private void rimlandTrade() {
		rimlandProvincesHiddenResources();
		rimlandSeaTradeBonuses();
	}

	private void rimlandSeaTradeBonuses() {
		//		Level , Low , Medium
		val portCity = new ArrayList<Tuple3<Integer, Integer, Integer>>();

		portCity.add(new Tuple3<>(1, 1, 2));
		portCity.add(new Tuple3<>(2, 1, 2));
		portCity.add(new Tuple3<>(3, 2, 3));
		portCity.add(new Tuple3<>(4, 3, 4));

		val portCastle = new ArrayList<Tuple3<Integer, Integer, Integer>>();
		portCastle.add(new Tuple3<>(1, 1, 2));
		portCastle.add(new Tuple3<>(2, 1, 2));
		portCastle.add(new Tuple3<>(3, 1, 3));
		portCastle.add(new Tuple3<>(4, 2, 3));

		portCity.forEach( p -> insertRimlandPortBonus(SettlType.City, p.getItem1(), p.getItem2(), p.getItem3()));
		portCastle.forEach( p -> insertRimlandPortBonus(SettlType.Castle, p.getItem1(), p.getItem2(), p.getItem3()));
	}

	private void insertRimlandPortBonus(SettlType settlType, int portLevel, int lowBonus, int mediumBonus) {
		val bonusStr = debugMode ? ExportDescrBuilding.IncomeBonus : ExportDescrBuilding.TradeBonus;
		val template = "{0}{1} requires hidden_resource {2}";
		val portRimlandLow = Ctm.msgFormat(template,bonusStr, lowBonus, HR_RIMLAND_LOW);
		val portRimlandHigh = Ctm.msgFormat(template,bonusStr, mediumBonus, HR_RIMLAND_MEDIUM);

		String building="", levelStr="";
		if(settlType == SettlType.City) {
			building = Buildings.PortCity;
			levelStr = Buildings.PortCityLevels.get(portLevel-1);
		} else if(settlType == SettlType.Castle) {
			building = Buildings.PortCastle;
			levelStr = Buildings.PortCastleLevels.get(portLevel-1);
		} else throw new PatcherNotSupportedEx("SettlementType: "+settlType);

		edb.insertIntoBuildingCapabilities(building, levelStr, settlType, portRimlandLow);
		edb.insertIntoBuildingCapabilities(building, levelStr, settlType, portRimlandHigh);
	}

	private void rimlandProvincesHiddenResources() {
		edb.addHiddenResourceDef(HR_RIMLAND_LOW);
		edb.addHiddenResourceDef(HR_RIMLAND_MEDIUM);

		addRimlandHiddenResources(seashoreProvinces);
		addRimlandHiddenResources(islands);

		if (islandHiddenResource) {
			edb.addHiddenResourceDef(HR_ISLAND);
			islands.getList().forEach(is -> descrRegions.addResource(is.getItem1(), HR_ISLAND));
		}
	}

	private void addRimlandHiddenResources(RimlandProvinceList provinceList) {
		for (val prov : provinceList.getList()) {
			val level = prov.getItem2();
			switch (level) {
				case 0:
					// Do nothing
					break;
				case 1:
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_LOW);
					break;
				case 2:
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_MEDIUM);
					break;
				case 3:
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_LOW);
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_MEDIUM);
					break;
				default:
					throw new PatcherLibBaseEx("Not supoorted rimland type: " + prov.getItem2());
			}
		}
	}

	private void atlanticHarderToTrade() {
		// ## Add requirements to ports
		val reqAdditional = Ctm.msgFormat("and not hidden_resource {0} or event_counter {1} 1", HR_ATLANTIC, EVENT_ATLANTIC_TRADE);

		Buildings.PortCityLevels.forEach(cp -> edb.addBuildingRequirement("port", cp, "city", reqAdditional));
		Buildings.PortCastleLevels.forEach(cp -> edb.addBuildingRequirement("castle_port", cp, "castle", reqAdditional));

		// ## Add Hidden Resource atlantic
		edb.addHiddenResourceDef(HR_ATLANTIC);
		val provinces = Arrays.asList(
				Provinces.Brittany_Rennes    // Brittany, west-south of English channel
				, "Poitiers_Province"
				, Provinces.Bordeaux
				, Provinces.Pamplona
				, "Burgos_Province"
				, "Leon_Province"
				, "Santiago_Province"
				, "Coimbra_Province"
				, "Lisbon_Province"
				, "Silves_Province"    // south to Lisbon
				, "Marrakesh_Province",
				"Sijilmasa_Province"
		);
		provinces.forEach(p -> descrRegions.addResource(p, HR_ATLANTIC));

		// ## Remove existing ports : Leon & Coimbra
		for (val province : provinces) {
			Buildings.PortCityLevels.forEach(cp -> descrStrat.removeSettlementBuilding(province, "port", cp));
			Buildings.PortCastleLevels.forEach(cp -> descrStrat.removeSettlementBuilding(province, "castle_port", cp));
		}
	}

	private void landGates() {
		edb.addHiddenResourceDef(HR_LAND_GATE);

		val provinces = Arrays.asList(
				// Iberia - France Land Gate
				Provinces.Bordeaux, Provinces.Pamplona, Provinces.Zaragoza, Provinces.Barcelona, Provinces.Toulouse,
				// Alps Passes
				Provinces.Swabia_Lower, Provinces.Bavaria,
				// Middle Europe:
				Provinces.Moravia, Provinces.Smolensk,
				// Levant - Egypt pass
				Provinces.Egypt_Lower, Provinces.Nile_Delta, Provinces.Ascalon, Provinces.Jerusalem, Provinces.Aqaba,
				// Constantinopole - Minor Asia pass
				Provinces.Constantinopole, Provinces.Bythynia_Nicea

		);
		provinces.forEach(p -> descrRegions.addResource(p, HR_LAND_GATE));

		val requiresLandGate = " requires hidden_resource " + HR_LAND_GATE;
		val tradeBonusLandGateTemplate = ExportDescrBuilding.TradeBonus + "{0}" + requiresLandGate;
		val orderLawMinusTemplate = ExportDescrBuilding.LawBonus + "{0}" + requiresLandGate;
		String tradeBonus, lawMinus;

		// #### Trade Bonuses : Levels 1 & 2 - City & Castle = BONUS 1
		for (int i = 1; i <= 2; i++) {
			tradeBonus = Ctm.msgFormat(tradeBonusLandGateTemplate, 1);
			edb.insertIntoBuildingCapabilities(Buildings.MarketCity, Buildings.MarketCityLevels.get(i - 1), SettlType.City, tradeBonus);
			edb.insertIntoBuildingCapabilities(Buildings.MarketCastle, Buildings.MarketCastleLevels.get(i - 1), SettlType.Castle, tradeBonus);
		}

		// Level 3  - City & Castle = BONUS 2
		tradeBonus = Ctm.msgFormat(tradeBonusLandGateTemplate, 2);
		edb.insertIntoBuildingCapabilities(Buildings.MarketCity, Buildings.MarketCityLevels.get(2), SettlType.City, tradeBonus);
		edb.insertIntoBuildingCapabilities(Buildings.MarketCastle, Buildings.MarketCastleLevels.get(2), SettlType.Castle, tradeBonus);

		// Level 4 & 5  - City BONUS 3
		for (int i = 4; i <= 5; i++) {
			tradeBonus = Ctm.msgFormat(tradeBonusLandGateTemplate, 3);
			edb.insertIntoBuildingCapabilities(Buildings.MarketCity, Buildings.MarketCityLevels.get(i - 1), SettlType.City, tradeBonus);
		}

		// ### Law Minuses - always - addded into Walls
		lawMinus = Ctm.msgFormat(orderLawMinusTemplate, -1);
		edb.insertIntoCityCastleWallsCapabilities(lawMinus);

	}

	private void initializeSeashoreProvinceList() {
		seashoreProvinces = new RimlandProvinceList();
		val p = seashoreProvinces;

		p.add(Provinces.Norway_Western, None);
		p.add(Provinces.Norway_Oslo, None, PortWithRiver);
		p.add(Provinces.Norway_Sigtuna, None);
		p.add(Provinces.Norway_Skara, Low, PortWithRiver);
		p.add(Provinces.Norway_Kalmar, None, PortWithRiver);
		p.add(Provinces.Norway_Lund, None);

		p.add(Provinces.Finland, None);
		p.add(Provinces.Novgorod, Medium);
		p.add(Provinces.Estonia_Kolyvan, Low);    // Estonia
		p.add(Provinces.Riga, PortWithRiver);
		p.add("Kernave_Province");    // Litwa
		p.add("Twangste_Province", Medium);    // Prussia
		p.add(Provinces.Gdansk, Medium, PortWithRiver);
		p.add(Provinces.Szczecin, Medium, PortWithRiver);
		p.add("Lubeck_Province", Medium);
		p.add(Provinces.Jutland_Pennisula, None);
		p.add("Hamburg_Province", Medium, PortWithRiver);
		p.add("Bremen_Province", Medium, PortWithRiver);
		p.add("Utrecht_Province", Medium, PortWithRiver);
		p.add("Leuven_Province", Medium, PortWithRiver);
		p.add("Ghent_Province", Medium, PortWithRiver);
		p.add("Rouen_Province", PortWithRiver);    // La Manche
		p.add(Provinces.Brittany_Rennes);
		p.add(Provinces.Poitiers);
		p.add(Provinces.Bordeaux, Medium, PortWithRiver);
		p.add(Provinces.Pamplona);    // no heartland behind, but traderoute Atlantic - Mediterian Sea
		p.add("Burgos_Province");
		p.add("Leon_Province");
		p.add(Provinces.Santiago, None);    // Santiago_Province  No heartland behind
		p.add("Coimbra_Province", PortWithRiver);
		p.add("Lisbon_Province", PortWithRiver);
		p.add("Silves_Province");
		p.add("Sevilla_Province", PortWithRiver);
		p.add("Granada_Province");
		p.add("Murcia_Province", PortWithRiver);
		p.add("Valencia_Province", PortWithRiver);
		p.add(Provinces.Zaragoza);
		p.add(Provinces.Barcelona);    // Barcelona - no Heartland behind but traderoute Atlantic - Mediterian Sea
		p.add(Provinces.Toulouse, Medium);
		p.add(Provinces.Arles, Medium);
		p.add(Provinces.Nizza, None);    // Nizza / Nice - mountains blocks heartleand
		p.add("Genoa_Province", Medium);

		p.add(Provinces.Pisa, None);
		p.add(Provinces.Florence, None);
		p.add(Provinces.Rome, None);
		p.add(Provinces.Neapol, None);
		p.add(Provinces.Reggio, None);
		p.add(Provinces.Bari, None);
		p.add(Provinces.Ancona, None);
		p.add(Provinces.Bologna, None, PortWithRiver);

		// Ion See, Italy North East corner
		p.add("Verona_Province");
		p.add("Venice_Province", Medium);
		p.add("Freiberg_Province", Medium);
		p.add("Zagreb_Province", Medium);
		p.add("Zara_Province");
		// Bosnia - no access to sea
		p.add("Ragusa_Province");    // Mountains block
		p.add("Skadar_Province");    // Mountains block
		p.add("Durazzo_Province");
		p.add(Provinces.Arta, None);
		p.add(Provinces.Corinth, None);
		p.add(Provinces.Macedon, PortWithRiver); // Macedon ?? maybe low or high ?
		p.add(Provinces.Adrianopolis, Low);
		p.add(Provinces.Constantinopole, High);

		// Now going around BLACK SEA
		p.add("Tarnovo_Province", Medium);
		p.add("Iasi_Province", Medium);
		p.add("Azaq_Province", Medium, PortWithRiver);
		p.add("Tmutarakan_Province", Medium, PortWithRiver);
		p.add("Sarkel_Province", Medium);
		p.add("Kutaisi_Province", Medium, PortWithRiver);
		p.add("Trebizond_Province");    // Mountains
		p.add("Sinop_Province", PortWithRiver);    // Mountains
		p.add(Provinces.Bythynia_Nicea, High);    // High ?? Asia Minor

		// Black Sea south Constantinolole
		p.add("Smyrna_Province", PortWithRiver);
		p.add("Attaleia_Province");
		p.add("Sis_Province", Medium);
		p.add("Antioch_Province", Medium, PortWithRiver);
		p.add("Tripoli_Province", Medium);
		p.add("Acre_Province", Medium);
		p.add(Provinces.Jerusalem, Medium);
		p.add(Provinces.Ascalon);

		// Africa North Coast
		p.add(Provinces.Nile_Delta, Medium, PortWithRiver);
		p.add("Alexandria_Province", PortWithRiver);
		p.add(Provinces.Tripolis, None);
		p.add(Provinces.Ifrygia_Southern, None);
		p.add(Provinces.Tunis, None, PortWithRiver);
		p.add(Provinces.Maghreb, None);
		p.add("Tlemcen_Province");    // Algieria / Maroko
		p.add("Fes_Province");
		p.add("Marrakesh_Province");

		// Mediterrian END, next Azow Sea
		p.add("Saqsin_Province", Medium, PortWithRiver);
		p.add("Aktobe_Province", Medium, PortWithRiver);
		p.add("Urgench_Province");
		p.add("Konjikala_Province");
		p.add("Ray_Province", Medium, PortWithRiver);
		// Alborz Province ??
		p.add("Tabriz_Province");
		p.add("Baku_Province");
		// End Azow Sea, Start Persian Bay
		p.add("Kerman_Province");
		p.add("Shiraz_Province", PortWithRiver);
		p.add("Basra_Province", Medium);
		p.add(Provinces.Arabia, None);
		// End Persian Bay, Start Red Sea south
		p.add("Mecca_Province", Medium);    // Also sea trade from India
		p.add("Tayma_Province");
		p.add(Provinces.Aqaba, Medium);    // Aqaba : Red Sea - Jerusalem/Askalon - Mediterian Sea
		p.add(Provinces.Egypt_Lower, Medium);    // No heartland but Red Sea - Mediterian Sea trade route
		p.add("Qus_Province");    // Southern Nile Delta, southern Africa trade routes
	}

	private void initializeIslandProvinceList() {
		islands = new RimlandProvinceList();
		// # Mediterian ISLANDS
		islands.add(Provinces.Balearics_Palma);    // Baleary
		islands.add(Provinces.Sardinia);    // Sardinia
		islands.add(Provinces.Sicily_Palermo);    // Sicily
		islands.add(Provinces.Corinth);    // Korinth, Peloponez
		islands.add(Provinces.Crete);    // Crete
		islands.add(Provinces.Cyprus);    // Cypr

		// # Baltic/North Sea Islands
		islands.add(Provinces.Gotland);
		islands.add(Provinces.Zealand_Danish_Straits);
		//p.add("");	//

		// # British Isles # - Rimland None
		islands.add(Provinces.Wessex, None);
		islands.add(Provinces.Essex_London, None, PortWithRiver);
		islands.add(Provinces.Norwich, None);
		islands.add(Provinces.Mercia, None, PortWithRiver);
		islands.add(Provinces.Wales, None);
		islands.add(Provinces.Northumbria, None);
		islands.add(Provinces.Edinburg_Scottish_Low, None);
		islands.add(Provinces.Scottish_Highlands, None);
		islands.add(Provinces.Ireland, None);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdBoolean("IslandHiddenResource", "Island Hidden Resource",
				feature -> ((RimlandHeartland) feature).isIslandHiddenResource(),
				(feature, value) -> ((RimlandHeartland) feature).setIslandHiddenResource(value)));

		return pars;
	}

	private static final int High = 3;
	private static final int Medium = 2;
	private static final int Low = 1;
	private static final int None = 0;
	private static final boolean PortWithRiver = true;
	private static final String EVENT_ATLANTIC_TRADE = "first_magnetic_compass"; //"omar_khayyam" - turn 2
	public static final String HR_ATLANTIC = "atlantic";
	public static final String HR_ISLAND = "island";
	public static final String HR_RIMLAND_LOW = "rimlandLow";
	public static final String HR_RIMLAND_MEDIUM = "rimlandMedium";
	public static final String HR_LAND_GATE = "landgate";

	public static final String SEA_TRADE_BONUS = "seaTradeBonus";


	private RimlandProvinceList seashoreProvinces;
	private RimlandProvinceList islands;

	private DescrRegions descrRegions;
	private ExportDescrBuilding edb;
	private DescrStratSectioned descrStrat;
	private CampaignScript campaignScript;

	public RimlandHeartland() {
		super("Rimland & Heartland");

		addCategory("Global");
		addCategory("Map");
		addCategory("Economy");

		if (!ConfigurationSettings.isDevEnvironment())
			addOverrideTask(new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm"));

		initializeSeashoreProvinceList();
		initializeIslandProvinceList();
	}

	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.fromString("868d5667-390b-4963-a586-540d351c95ae");
}
