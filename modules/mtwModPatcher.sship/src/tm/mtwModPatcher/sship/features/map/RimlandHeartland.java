package tm.mtwModPatcher.sship.features.map;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.sship.lib.Provinces;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by tomek on 24.06.2017.
 */
public class RimlandHeartland extends Feature {

	private String eventAtlanticTrade = "first_magnetic_compass"; //"omar_khayyam" - turn 2
	//private List<String> portCityLevels;
	//private List<String> portCastleLevels;

	@Override
	public void executeUpdates() throws Exception {
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);

		landGates();
		atlanticHarderToTrade();
		rimlandTrade();
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

		// # Mediterian ISLANDS
		p.add("Palma_Province");	// Baleary
		p.add("Cagliari_Province");	// Sardinia
		p.add("Palermo_Province");	// Sicily
		p.add("Corinth_Province");	// Korinth, Peloponez
		p.add("Chandax_Province");	// Crete
		p.add("Lefkosia_Province");	// Cypr

		// # Baltic/North Sea Islands

		p.add("Visby_Province");	// Gotland
		p.add("Roskilde_Province");	// Zealand - Danish straits
		//p.add("");	//


		edb.addHiddenResourceDef(HR_RIMLAND_LOW);
		edb.addHiddenResourceDef(HR_RIMLAND_HIGH);
		for(val prov : p.getList()) {
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
		val reqAdditional = Ctm.msgFormat("and not hidden_resource atlantic or event_counter {0} 1", eventAtlanticTrade);

		//portCityLevels = Arrays.asList("port", "shipwright" ,"dockyard" ,"naval_drydock");
		ExportDescrBuilding.PortCityLevels.forEach(cp -> edb.addBuildingRequirement("port", cp, "city", reqAdditional) );

		//portCastleLevels = Arrays.asList("c_port", "c_shipwright", "c_dockyard", "c_naval_drydock");
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

	private static final int H = 3;
	private static final int M = 2;
	private static final int L = 1;
	public static final String HR_ATLANTIC = "atlantic";
	public static final String HR_RIMLAND_LOW = "rimlandLow";
	public static final String HR_RIMLAND_HIGH = "rimlandHigh";
	public static final String HR_LAND_GATE = "landgate";

	private DescrRegions descrRegions;
	private ExportDescrBuilding edb;
	private DescrStratSectioned descrStrat;

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
