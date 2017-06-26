package tm.mtwModPatcher.sship.features.map;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by tomek on 24.06.2017.
 */
public class RimlandHeartland extends Feature {

	private String eventAtlanticTrade = "first_magnetic_compass"; //"omar_khayyam" - turn 2
	private List<String> cityPortLevels;
	private List<String> castlePortLevels;

	@Override
	public void executeUpdates() throws Exception {
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);

		atlanticHarderToTrade();
		rimlandTrade();
	}

	private void rimlandTrade() {
		rimlandProvincesHiddenResources();
		rimlandSeaTradeBonuses();
	}

	private void rimlandSeaTradeBonuses() {
		val rimlandLow = "\t\ttrade_base_income_bonus bonus 1 requires hidden_resource "+ HR_RIMLAND_LOW;
		val rimlandHigh = "\t\ttrade_base_income_bonus bonus 2 requires hidden_resource "+ HR_RIMLAND_HIGH;


		for(val portLevel : cityPortLevels) {
			edb.insertIntoBuildingCapabilities("port", portLevel, "city", rimlandLow);
			edb.insertIntoBuildingCapabilities("port", portLevel, "city", rimlandHigh);
		}
		for(val portLevel : castlePortLevels) {
			edb.insertIntoBuildingCapabilities("castle_port", portLevel, "castle", rimlandLow);
			edb.insertIntoBuildingCapabilities("castle_port", portLevel, "castle", rimlandHigh);
		}



	}

	private void rimlandProvincesHiddenResources() {
		val p = new RimlandProvinceList();

		p.add("Novgorod_Province", H); 	// dsaf
		p.add("Kolyvan_Province");	// Estonia
		p.add("Riga_Province");
		p.add("Kernave_Province");	// Litwa
		p.add("Twangste_Province", H);	// Prussia
		p.add("Gdansk_Province", H);
		p.add("Szczecin_Province", H);
		p.add("Lubeck_Province", H);
		p.add("Hamburg_Province", H);
		p.add("Bremen_Province", H);
		p.add("Utrecht_Province", H);
		p.add("Leuven_Province", H);
		p.add("Ghent_Province", H);
		p.add("Rouen_Province");	// La Manche
		p.add("Rennes_Province");
		p.add("Poitiers_Province");
		p.add("Bordeaux_Province");
		p.add("Pamplona_Province");	// no heartland behind, but traderoute Atlantic - Mediterian Sea
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
		p.add("Zaragoza_Province");
		// Barcelona - no Heartland behind
		p.add("Toulouse_Province");
		p.add("Arles_Province", H);
		// Nizza / Nice - mountains blocks heartleand
		p.add("Genoa_Province", H);
		p.add("Verona_Province");
		p.add("Venice_Province", H);
		p.add("Freiberg_Province", H);
		p.add("Zagreb_Province", H);
		p.add("Zara_Province");
		// Bosnia - no access to sea
		p.add("Ragusa_Province");	// Mountains block
		p.add("Skadar_Province");	// Mountains block
		p.add("Durazzo_Province");
		p.add("Thessalonica_Province"); // Macedon ?? maybe low or high ?
		// Adrianopolis - no, now going around Black Sea
		p.add("Tarnovo_Province", H);
		p.add("Iasi_Province", H);
		p.add("Azaq_Province", H);
		p.add("Tmutarakan_Province", H);
		p.add("Sarkel_Province", H);
		p.add("Kutaisi_Province", H);
		p.add("Trebizond_Province");	// Mountains
		p.add("Sinop_Province");	// Mountains
		p.add("Nicaea_Province");	// High ?? Asia Minor
		p.add("Smyrna_Province");
		p.add("Attaleia_Province");
		p.add("Sis_Province", H);
		p.add("Antioch_Province", H);
		p.add("Tripoli_Province", H);
		p.add("Acre_Province", H);
		p.add("Jerusalem_Province", H);
		p.add("Ascalon_Province");
		p.add("Damietta_Province", H);
		p.add("Alexandria_Province");
		p.add("Tlemcen_Province");	// Algieria / Maroko
		p.add("Fes_Province");
		p.add("Marrakesh_Province");
		// Mediterrian END, next Azow Sea
		p.add("Saqsin_Province", H);
		p.add("Aktobe_Province", H);
		p.add("Urgench_Province");
		p.add("Konjikala_Province");
		p.add("Ray_Province", H);
		p.add("Tabriz_Province");
		p.add("Baku_Province");
		// End Azow Sea, Start Persian Bay
		p.add("Kerman_Province");
		p.add("Shiraz_Province");
		p.add("Basra_Province", H);
		// End Persian Bay, Start Red Sea south
		p.add("Mecca_Province", H);	// Also sea trade from India
		p.add("Tayma_Province");
		p.add("Al_Aqaba_Province", H);	// Aqaba : Red Sea - Jerusalem/Askalon - Mediterian Sea
		p.add("Cairo_Province", H);	// No heartland but Red Sea - Mediterian Sea trade route
		p.add("Qus_Province");	// Southern Nile Delta, southern Africa trande routes
		//p.add("");

		edb.addHiddenResourceDef(HR_RIMLAND_LOW);
		edb.addHiddenResourceDef(HR_RIMLAND_HIGH);
		for(val prov : p.getList()) {
			if(prov.getItem2() == 1) descrRegions.addResource(prov.getItem1(), HR_RIMLAND_LOW);
			else if(prov.getItem2() == 2) descrRegions.addResource(prov.getItem1(), HR_RIMLAND_HIGH);
			else throw new PatcherLibBaseEx("Not supoorted rimland type: "+prov.getItem2());
		}
	}

	private void atlanticHarderToTrade() {
		// ## Add requirements to ports
		val reqAdditional = Ctm.msgFormat("and not hidden_resource atlantic or event_counter {0} 1", eventAtlanticTrade);

		cityPortLevels = Arrays.asList("port", "shipwright" ,"dockyard" ,"naval_drydock");
		cityPortLevels.forEach(cp -> edb.addBuildingRequirement("port", cp, "city", reqAdditional) );

		castlePortLevels = Arrays.asList("c_port", "c_shipwright", "c_dockyard", "c_naval_drydock");
		castlePortLevels.forEach(cp -> edb.addBuildingRequirement("castle_port", cp, "castle", reqAdditional) );

		// ## Add Hidden Resource atlantic
		edb.addHiddenResourceDef(HR_ATLANTIC);
		val provinces = Arrays.asList(
				"Rennes_Province"	// Brittany, west-sout of English channel
				,"Poitiers_Province"
				,"Bordeaux_Province"
				,"Pamplona_Province"
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
			cityPortLevels.forEach(cp -> descrStrat.removeSettlementBuilding(province, "port", cp) );
			castlePortLevels.forEach(cp -> descrStrat.removeSettlementBuilding(province, "castle_port", cp) );
		}
	}

	private static final int H = 2;
	private static final int L = 1;
	public static final String HR_ATLANTIC = "atlantic";
	public static final String HR_RIMLAND_LOW = "rimlandLow";
	public static final String HR_RIMLAND_HIGH = "rimlandHigh";

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
