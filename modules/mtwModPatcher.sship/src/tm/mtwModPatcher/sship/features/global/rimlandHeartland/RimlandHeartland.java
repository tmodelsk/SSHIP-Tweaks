package tm.mtwModPatcher.sship.features.global.rimlandHeartland;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
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
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.FactionAiEcId;
import tm.mtwModPatcher.lib.managers.CampaignScriptManager;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.sship.lib.Provinces;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by tomek on 24.06.2017.
 */
public class RimlandHeartland extends Feature {

	@Getter @Setter
	private boolean islandHiddenResource = true;

	private int seaTradeSmall = 4;
	private int seaTradeMedium = 6;
	private int seaTradeLarge = 8;

	private int seaTradePenaltyIncome = -10;

	@Override
	public void executeUpdates() throws Exception {
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);

		landGates();
		atlanticHarderToTrade();
		rimlandTrade();
		seaTradeBonusesForHeartland();
	}

	private void seaTradeBonusesForHeartland() {

		createSeaTradeBonusesScripts();

		for(int i=1; i<=3; i++) {
			String bonusTmp = Ctm.msgFormat("\t\tincome_bonus bonus {0} requires event_counter {1} {2} and not building_present_min_level ",
					i, getSeaTradeBonusEventName(i), 1);

			val roadReqBase=" and building_present_min_level ";
			val riverReq=" and building_present_min_level ";

			val islandCondition = islandHiddenResource ? Ctm.msgFormat(" and not hidden_resource {0}", HR_ISLAND) : "";

			val bonusPortCity = bonusTmp + "port port" + islandCondition + roadReqBase + "hinterland_roads roads";	// final
			val bonusPortCastle = bonusTmp + "castle_port c_port" + islandCondition + roadReqBase + "hinterland_castle_roads c_roads";	// final

			ExportDescrBuilding.MarketCityLevels.forEach(bl ->
					edb.insertIntoBuildingCapabilities("port", bl, "city", bonusPortCity) );

			ExportDescrBuilding.MarketCastleLevels.forEach(bl ->
					edb.insertIntoBuildingCapabilities("castle_port", bl, "castle", bonusPortCastle) );
		}
		ExportDescrBuilding.MarketCityLevels.forEach(bl ->
				edb.insertIntoBuildingCapabilities("port", bl, "city",
						Ctm.msgFormat("\t\tincome_bonus bonus {0} requires event_counter {1} {2} and not building_present_min_level port port",
								seaTradePenaltyIncome, getSeaTradeBonusEventName(0), 1)) );
	}

	private void createSeaTradeBonusesScripts() {
		val csManager = new CampaignScriptManager(campaignScript);
		val factions = FactionsDefs.allFactionsExceptRebelsList();

		// ### Declare port counter variables
		val variabledDeclarations = new RegionBlock("Ports heartland trade boost - declarations");
		factions.forEach( f -> variabledDeclarations.add(new DeclareVariable(getSeaTradeCounterVar(f))));
		campaignScript.insertAtEndOfFile(variabledDeclarations);

		// ### Pre Faction Turn Start - decide about bonuses depending on conuters from previous turn
		val factionChooseBlock = new RegionBlock("Ports heartland trade boost - decisions");
		val factionsAiIds = campaignScript.FactionsAiEcIds;

		// ## Process Player
		IfBlock ifLocalPlayer = new IfBlock(new CompareCounter("ai_ec_id", "=" , 0));
		for(FactionAiEcId factionId : factionsAiIds) {
			IfBlock ifPlayerFactionIs = new IfBlock(new CompareCounter("pl_ec_id" , "=" , factionId.Id));

			ifPlayerFactionIs.add(getSeaTradeBonusesScriptBodyForFaction(factionId.Name));
			ifLocalPlayer.add(ifPlayerFactionIs);
		}
		factionChooseBlock.add(ifLocalPlayer);

		// ## Process AIs
		for (val factionAiId : factionsAiIds) {
			val ifFaction = new IfBlock(new CompareCounter("ai_ec_id" , "=" , factionAiId.Id));

			ifFaction.add(getSeaTradeBonusesScriptBodyForFaction(factionAiId.Name));
			factionChooseBlock.add(ifFaction);
		}
		csManager.insertAtEndOfPreFactionNotSlaveTurnStart(factionChooseBlock);

		// ### Settlement End Turn Monitor - counts ports for faction
		val factionsVariables = new HashMap<String, String>();
		factionsAiIds.forEach( f-> factionsVariables.put(f.Name, getSeaTradeCounterVar(f.Name)) );

		val monitorFactory = new PortMonitorCounterFactory(factionsVariables, campaignScript);
		val portCityLevels = ExportDescrBuilding.PortCityLevels;
		val portCastleLevels = ExportDescrBuilding.PortCastleLevels;
		val seaTradeCityLevels = ExportDescrBuilding.SeaTradeCityLevels;

		val monitorsBlock = new RegionBlock("Ports heartland trade boost: Port counting monitors");

		// Ports
		for(int i=0; i<=2;i++) {
			val operator = i<2 ? "=" : ">=";
			monitorsBlock.add(monitorFactory.create(portCityLevels.get(i), operator, i+1));
			monitorsBlock.add(monitorFactory.create(portCastleLevels.get(i), operator, i+1));
		}

		// Sea Trade - Merchant Stores - First level same for castle / city
		monitorsBlock.add(monitorFactory.create(seaTradeCityLevels.get(0), "=", 1));
		monitorsBlock.add(monitorFactory.create(seaTradeCityLevels.get(1), ">=", 2));

		campaignScript.insertAtEndOfFile(monitorsBlock);
	}

	private ContainerBlock getSeaTradeBonusesScriptBodyForFaction(String factionName) {
		val body = new ContainerBlock();
		val factionVar = getSeaTradeCounterVar(factionName);

		body.add(new SetEventCounter(getSeaTradeBonusEventName(0), 0));
		body.add(new SetEventCounter(getSeaTradeBonusEventName(1), 0));
		body.add(new SetEventCounter(getSeaTradeBonusEventName(2), 0));
		body.add(new SetEventCounter(getSeaTradeBonusEventName(3), 0));

		IfBlock ifBlock;

		ifBlock = new IfBlock(new CompareCounter(factionVar, "<", seaTradeSmall));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(0), 1));
		ifBlock.add(new WriteToLog( Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 0)));
		body.add(ifBlock);

		ifBlock = new IfBlock(new CompareCounter(factionVar, ">=", seaTradeSmall));
		ifBlock.andCondition(new CompareCounter(factionVar, "<", seaTradeMedium));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(1), 1));
		ifBlock.add(new WriteToLog( Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 1)));
		body.add(ifBlock);

		ifBlock = new IfBlock(new CompareCounter(factionVar, ">=", seaTradeMedium));
		ifBlock.andCondition(new CompareCounter(factionVar, "<", seaTradeLarge));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(2), 1));
		ifBlock.add(new WriteToLog( Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 2)));
		body.add(ifBlock);

		ifBlock = new IfBlock(new CompareCounter(factionVar, ">=", seaTradeLarge));
		ifBlock.add(new SetEventCounter(getSeaTradeBonusEventName(3), 1));
		ifBlock.add(new WriteToLog( Ctm.msgFormat("SeaTrade Bonus: {0} Setting event counter to {1}", factionName, 3)));
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
		val portRimlandLow = "\t\ttrade_base_income_bonus bonus 1 requires hidden_resource "+ HR_RIMLAND_LOW;
		val portRimlandHigh = "\t\ttrade_base_income_bonus bonus 2 requires hidden_resource "+ HR_RIMLAND_HIGH;


		for(val portLevel : ExportDescrBuilding.PortCityLevels) {
			edb.insertIntoBuildingCapabilities("port", portLevel, "city", portRimlandLow);
			edb.insertIntoBuildingCapabilities("port", portLevel, "city", portRimlandHigh);
		}
		for(val portLevel : ExportDescrBuilding.PortCastleLevels) {
			edb.insertIntoBuildingCapabilities("castle_port", portLevel, "castle", portRimlandLow);
			edb.insertIntoBuildingCapabilities("castle_port", portLevel, "castle", portRimlandHigh);
		}
	}

	private void rimlandProvincesHiddenResources() {
		val p = new RimlandProvinceList();

		p.add(Provinces.Novgorod, M);
		p.add(Provinces.Estonia_Kolyvan, L);	// Estonia
		p.add("Riga_Province");
		p.add("Kernave_Province");	// Litwa
		p.add("Twangste_Province", M);	// Prussia
		p.add("Gdansk_Province", M);
		p.add("Szczecin_Province", M);
		p.add("Lubeck_Province", M);
		p.add("Hamburg_Province", M);
		p.add("Bremen_Province", M);
		p.add("Utrecht_Province", M);
		p.add("Leuven_Province", M);
		p.add("Ghent_Province", M);
		p.add("Rouen_Province");	// La Manche
		p.add(Provinces.Brittany_Rennes);
		p.add("Poitiers_Province");
		p.add(Provinces.Bordeaux, M);
		p.add(Provinces.Pamplona);	// no heartland behind, but traderoute Atlantic - Mediterian Sea
		p.add("Burgos_Province");
		p.add("Leon_Province");
		// Santiago_Province  No heartland behind
		p.add("Coimbra_Province");
		p.add("Lisbon_Province");
		p.add("Silves_Province");
		p.add("Sevilla_Province");
		p.add("Granada_Province");
		p.add("Murcia_Province");
		p.add("Valencia_Province");
		p.add(Provinces.Zaragoza);
		p.add(Provinces.Barcelona);	// Barcelona - no Heartland behind but traderoute Atlantic - Mediterian Sea
		p.add(Provinces.Toulouse, M);
		p.add("Arles_Province", M);
		// Nizza / Nice - mountains blocks heartleand
		p.add("Genoa_Province", M);
		p.add("Verona_Province");
		p.add("Venice_Province", M);
		p.add("Freiberg_Province", M);
		p.add("Zagreb_Province", M);
		p.add("Zara_Province");
		// Bosnia - no access to sea
		p.add("Ragusa_Province");	// Mountains block
		p.add("Skadar_Province");	// Mountains block
		p.add("Durazzo_Province");
		p.add("Thessalonica_Province"); // Macedon ?? maybe low or high ?
		// Adrianopolis - no, now going around Black Sea
		p.add("Tarnovo_Province", M);
		p.add("Iasi_Province", M);
		p.add("Azaq_Province", M);
		p.add("Tmutarakan_Province", M);
		p.add("Sarkel_Province", M);
		p.add("Kutaisi_Province", M);
		p.add("Trebizond_Province");	// Mountains
		p.add("Sinop_Province");	// Mountains
		p.add("Nicaea_Province");	// High ?? Asia Minor
		p.add("Smyrna_Province");
		p.add("Attaleia_Province");
		p.add("Sis_Province", M);
		p.add("Antioch_Province", M);
		p.add("Tripoli_Province", M);
		p.add("Acre_Province", M);
		p.add("Jerusalem_Province", M);
		p.add("Ascalon_Province");
		p.add("Damietta_Province", M);
		p.add("Alexandria_Province");
		p.add("Tlemcen_Province");	// Algieria / Maroko
		p.add("Fes_Province");
		p.add("Marrakesh_Province");
		// Mediterrian END, next Azow Sea
		p.add("Saqsin_Province", M);
		p.add("Aktobe_Province", M);
		p.add("Urgench_Province");
		p.add("Konjikala_Province");
		p.add("Ray_Province", M);
		p.add("Tabriz_Province");
		p.add("Baku_Province");
		// End Azow Sea, Start Persian Bay
		p.add("Kerman_Province");
		p.add("Shiraz_Province");
		p.add("Basra_Province", M);
		// End Persian Bay, Start Red Sea south
		p.add("Mecca_Province", M);	// Also sea trade from India
		p.add("Tayma_Province");
		p.add("Al_Aqaba_Province", M);	// Aqaba : Red Sea - Jerusalem/Askalon - Mediterian Sea
		p.add("Cairo_Province", M);	// No heartland but Red Sea - Mediterian Sea trade route
		p.add("Qus_Province");	// Southern Nile Delta, southern Africa trande routes

		val islands = new RimlandProvinceList();
		// # Mediterian ISLANDS
		islands.add("Palma_Province");	// Baleary
		islands.add("Cagliari_Province");	// Sardinia
		islands.add("Palermo_Province");	// Sicily
		islands.add("Corinth_Province");	// Korinth, Peloponez
		islands.add("Chandax_Province");	// Crete
		islands.add("Lefkosia_Province");	// Cypr

		// # Baltic/North Sea Islands
		islands.add("Visby_Province");	// Gotland
		islands.add("Roskilde_Province");	// Zealand - Danish straits
		//p.add("");	//

		edb.addHiddenResourceDef(HR_RIMLAND_LOW);
		edb.addHiddenResourceDef(HR_RIMLAND_HIGH);

		addRimlandHiddenResources(p);
		addRimlandHiddenResources(islands);

		if(islandHiddenResource) {
			edb.addHiddenResourceDef(HR_ISLAND);
			islands.getList().forEach( is -> descrRegions.addResource(is.getItem1(), HR_ISLAND) );
		}
	}

	private void addRimlandHiddenResources(RimlandProvinceList provinceList) {
		for(val prov : provinceList.getList()) {
			val level = prov.getItem2();
			switch (level) {
				case 1:
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_LOW);
					break;
				case 2:
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_HIGH);
					break;
				case 3:
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_LOW);
					descrRegions.addResource(prov.getItem1(), HR_RIMLAND_HIGH);
					break;
				default:
					throw new PatcherLibBaseEx("Not supoorted rimland type: "+prov.getItem2());
			}
		}
	}

	private void atlanticHarderToTrade() {
		// ## Add requirements to ports
		val reqAdditional = Ctm.msgFormat("and not hidden_resource {0} or event_counter {1} 1", HR_ATLANTIC, EVENT_ATLANTIC_TRADE);

		ExportDescrBuilding.PortCityLevels.forEach(cp -> edb.addBuildingRequirement("port", cp, "city", reqAdditional) );
		ExportDescrBuilding.PortCastleLevels.forEach(cp -> edb.addBuildingRequirement("castle_port", cp, "castle", reqAdditional) );

		// ## Add Hidden Resource atlantic
		edb.addHiddenResourceDef(HR_ATLANTIC);
		val provinces = Arrays.asList(
				Provinces.Brittany_Rennes	// Brittany, west-south of English channel
				,"Poitiers_Province"
				,Provinces.Bordeaux
				,Provinces.Pamplona
				,"Burgos_Province"
				,"Leon_Province"
				,"Santiago_Province"
				,"Coimbra_Province"
				,"Lisbon_Province"
				,"Silves_Province"	// south to Lisbon
				,"Marrakesh_Province",
				"Sijilmasa_Province"
		);
		provinces.forEach( p ->  descrRegions.addResource(p, HR_ATLANTIC));

		// ## Remove existing ports : Leon & Coimbra
		for (val province : provinces) {
			ExportDescrBuilding.PortCityLevels.forEach(cp -> descrStrat.removeSettlementBuilding(province, "port", cp) );
			ExportDescrBuilding.PortCastleLevels.forEach(cp -> descrStrat.removeSettlementBuilding(province, "castle_port", cp) );
		}
	}

	private void landGates() {
		edb.addHiddenResourceDef(HR_LAND_GATE);

		val provinces = Arrays.asList(
				// Iberia Land Gate
				Provinces.Bordeaux, Provinces.Pamplona, Provinces.Zaragoza, Provinces.Barcelona, Provinces.Toulouse
		);
		provinces.forEach( p ->  descrRegions.addResource(p, HR_LAND_GATE));

		val landGate = "\t\ttrade_base_income_bonus bonus 1 requires hidden_resource "+ HR_LAND_GATE;

		edb.insertIntoBuildingCapabilities("market", ExportDescrBuilding.MarketCityLevels, "city", landGate);
		edb.insertIntoBuildingCapabilities("market_castle", ExportDescrBuilding.MarketCastleLevels, "castle", landGate);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdBoolean("IslandHiddenResource", "Island Hidden Resource",
				feature -> ((RimlandHeartland)feature).isIslandHiddenResource(),
				(feature, value) -> ((RimlandHeartland)feature).setIslandHiddenResource(value)));

		return pars;
	}

	private static final int H = 3;
	private static final int M = 2;
	private static final int L = 1;
	private static final String EVENT_ATLANTIC_TRADE = "first_magnetic_compass"; //"omar_khayyam" - turn 2
	public static final String HR_ATLANTIC = "atlantic";
	public static final String HR_ISLAND = "island";
	public static final String HR_RIMLAND_LOW = "rimlandLow";
	public static final String HR_RIMLAND_HIGH = "rimlandHigh";
	public static final String HR_LAND_GATE = "landgate";
	public static final String EVENT_FACTION_HAS_PORT = "faction_has_port_ec";

	public static final String SEA_TRADE_BONUS = "seaTradeBonus";



	private DescrRegions descrRegions;
	private ExportDescrBuilding edb;
	private DescrStratSectioned descrStrat;
	private CampaignScript campaignScript;

	public RimlandHeartland() {
		super("Rimland & Heartland");

		addCategory("Global");
		addCategory("Map");
		addCategory("Economy");

		if(!ConfigurationSettings.isDevEnvironment())
			addOverrideTask(new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm"));
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
