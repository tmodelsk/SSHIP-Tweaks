package tm.mtwModPatcher.lib.managers.garrisons;

import lombok.val;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.UnitDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tomek on 2016-11-13.
 */
public class GarrisonMetaManager {


	private void initialize() throws PatcherLibBaseEx {
		_Dict = new UnitTypeFactionDictionary();
		
		configureLevyInfantry();
		configureInfantry();
		configureLevyMissle();
		configureMissle();
		configureHeavyInfantry();
	}

	private void configureHeavyInfantry() throws PatcherLibBaseEx {
		val type = UnitType.HeavyInfantry;

		// Western Catholics
		_Dict.add(type,
				Arrays.asList(	"aragon", "spain", "portugal" , "france", "hre",
						"hungary" , "pisa", "venice"),
				"Dismounted Sword Mailed Knights");

		_Dict.add(type , Arrays.asList("scotland" , "denmark") , "Dismounted Mailed Knights");
		_Dict.add(type , "norway" , "Norse Axemen");
		_Dict.add(type , Arrays.asList("england", "sicily") , "Dismounted Norman Cavalry");

		_Dict.add(type , "poland" , "Dismounted Polish Nobles");
		_Dict.add(type , "papal_states" , "Papal Guard");

		_Dict.add(type , "jerusalem" , "Dismounted Knights Of Jerusalem");

		// ## Orthodox : ##
		_Dict.add(type , Arrays.asList( "russia", "kievan_rus", "") , "Dismounted Druzhina");
		_Dict.add(type , "teutonic_order" , "Dismounted Vlastelcici");
		_Dict.add(type , "timurids" , "georgian medium swordsmen");
		_Dict.add(type , "byzantium" , "English Huscarls"); // old : Castrophilakae


		// #### Islam - Muslims
		_Dict.add(type , Arrays.asList("turks") , "Dismounted Ghulams");

		_Dict.add(type , Arrays.asList("moors", "rum", "kwarezm", "milan", "egypt") , "Dismounted Fari Lancers");

		_Dict.add(type , "mongols" , "Disamounted Turhagut");
		_Dict.add(type , "cumans" , new UnitGarrisonInfo("Dismounted Bekh Druzhina"));
		_Dict.add(type , "lithuania" , new UnitGarrisonInfo("Ducal Axe"));

		_Dict.add(type, "slave" , "Macemen");

	}

	private void configureMissle() throws PatcherLibBaseEx {
		UnitType type = UnitType.Missle;

		// #### Berber Archers - as aor Local Troop ###
		List<String> berberArchersAllowedFactions = FactionsDefs.islamFactionsList();
		berberArchersAllowedFactions.remove("mongols");
		berberArchersAllowedFactions.add("scotland");
		berberArchersAllowedFactions.add("byzantium");
		berberArchersAllowedFactions.add("lithuania");

		_Dict.add(type , berberArchersAllowedFactions , new UnitGarrisonInfo("Berber Archers", 1 , "berber"));

		_Dict.add(type,
				Arrays.asList( "aragon", "portugal", "spain" , "hre" , "france", "denmark", "norway",
								"hungary", "pisa", "venice" , "papal_states", "sicily" , "jerusalem",
								"teutonic_order", "timurids"),
				"Crossbow Militia");

		_Dict.add(type , "england" , "Archer Militia");
		_Dict.add(type , "scotland" , "Peasant Archers", 2);	// and access to Berber Archers

		_Dict.add(type , Arrays.asList("poland", "russia", "kievan_rus") , "EE Crossbow Militia");

		_Dict.add(type , "byzantium" , "Toxotae" , 1);

		_Dict.add(type , Arrays.asList("moors", "rum", "turks", "kwarezm", "milan", "egypt") , "ME Crossbowmen");

		_Dict.add(type , "lithuania" , new UnitGarrisonInfo("Hunters", 2 , "lithuania"));
		_Dict.add(type , "mongols" , "Khabutu Archers");
		_Dict.add(type , "cumans" , "Dismounted Cuman Militia",1);
	}

	private void configureLevyMissle() throws PatcherLibBaseEx {
		UnitType type = UnitType.LevyMissle;

		// #### Berber Archers - as aor Local Troop ###
		List<String> berberArchersAllowedFactions = FactionsDefs.allFactionsList();
		berberArchersAllowedFactions.remove("mongols");
		_Dict.add(type , berberArchersAllowedFactions , new UnitGarrisonInfo("Berber Archers" , "berber"));
		
		// Western Catholics & Ortodox Christians
		_Dict.add(type,
				Arrays.asList("england" , "norway", "scotland"), "Peasant Archers", 1);

		_Dict.add(type , "byzantium" , "Toxotae");

		_Dict.add(type , "lithuania" , new UnitGarrisonInfo("Hunters", 1 , "lithuania"));
		_Dict.add(type , "cumans" , new UnitGarrisonInfo("EE Peasant Archers", 1));
		_Dict.add(type , "mongols" , new UnitGarrisonInfo("ME Peasant Archers", 1));

		_Dict.add(type, "slave", "Peasant Archers");
	}

	private void configureInfantry() throws PatcherLibBaseEx {
		// Western Catholics & Ortodox Christians
		_Dict.add(UnitType.Spearmen,
				Arrays.asList(	"england", "spain", "aragon", "portugal"),
				new UnitGarrisonInfo("Spear Militia", 2));

		_Dict.add(UnitType.Spearmen, "scotland" , new UnitGarrisonInfo("Axe Militia", 2, Arrays.asList("scotland")));
		_Dict.add(UnitType.Spearmen, "scotland" , new UnitGarrisonInfo("Spear Militia", 2));

		_Dict.add(UnitType.Spearmen, Arrays.asList("norway" ,"denmark") , new UnitGarrisonInfo("Landevearnmenn", Arrays.asList("norway","denmark")));
		_Dict.add(UnitType.Spearmen, Arrays.asList("norway" ,"denmark") , "Spear Militia" , 2);


		_Dict.add(UnitType.Spearmen, Arrays.asList("france", "hre", "jerusalem" , "pisa", "venice", "papal_states", "sicily"), "Sergeant Spearmen");

		_Dict.add(UnitType.Spearmen, "poland" , new UnitGarrisonInfo("Smolensk Infantry", "poland"));
		_Dict.add(UnitType.Spearmen, "poland" , new UnitGarrisonInfo("EE Spear Militia", 2));

		_Dict.add(UnitType.Spearmen , Arrays.asList("russia", "kievan_rus") , new UnitGarrisonInfo("Rus Senior Militia", Arrays.asList("russia" , "kievan_rus")));
		_Dict.add(UnitType.Spearmen , Arrays.asList("russia", "kievan_rus", "hungary") , "EE Spear Militia", 2);	// default

		_Dict.add(UnitType.Spearmen, "teutonic_order" , new UnitGarrisonInfo("Spear Militia", 2));
		_Dict.add(UnitType.Spearmen , "byzantium" , "Contaratoi" , 1);
		_Dict.add(UnitType.Spearmen , "timurids" , "georgian light spearmen");

		// #### Islam - Muslims
		_Dict.add(UnitType.Spearmen , Arrays.asList("moors", "rum", "turks", "kwarezm", "milan", "egypt") , "Ahdath Militia" , 1);

		// #### Pagand & barbs & rebels

		_Dict.add(UnitType.Spearmen , "cumans" , "EE Spear Militia", 2);

		_Dict.add(UnitType.Spearmen , "lithuania" , "Baltic Spearmen", 2);
		_Dict.add(UnitType.Spearmen , "mongols" , "Sibiryaki Senior Militia");
		_Dict.add(UnitType.Spearmen , "slave" , "Mercenary Spearmen");
	}

	private void configureLevyInfantry() throws PatcherLibBaseEx {
		// Western Catholics & Ortodox Christians
		_Dict.add(UnitType.LevyInfantry,
				Arrays.asList(	"france", "hre",
						"spain", "aragon", "portugal", "jerusalem"),
				new UnitGarrisonInfo("Spear Militia"));

		_Dict.add(UnitType.LevyInfantry, "scotland" , "Axe Militia" );

		_Dict.add(UnitType.LevyInfantry, "england" , new UnitGarrisonInfo("Fyrd Spearmen" , "england") );
		_Dict.add(UnitType.LevyInfantry, "england" , new UnitGarrisonInfo("Spear Militia" ) );	//  default

		_Dict.add(UnitType.LevyInfantry, "norway" , new UnitGarrisonInfo("Drengjar" , "norway") );	// Viking Drengjar
		_Dict.add(UnitType.LevyInfantry, "norway" , new UnitGarrisonInfo("Spear Militia" ) );	//  default

		_Dict.add(UnitType.LevyInfantry, "denmark" , new UnitGarrisonInfo("Axe Militia" , Arrays.asList("norway", "denmark")) );
		_Dict.add(UnitType.LevyInfantry, "denmark" , new UnitGarrisonInfo("Spear Militia" ) );	//  default

		_Dict.add(UnitType.LevyInfantry, "teutonic_order" , new UnitGarrisonInfo("Slav Levies" , Arrays.asList("serbia")) );	// Viking Drengjar
		_Dict.add(UnitType.LevyInfantry, "teutonic_order" , new UnitGarrisonInfo("Spear Militia" ) );	//  default


		_Dict.add(UnitType.LevyInfantry, Arrays.asList("pisa", "venice", "papal_states", "sicily") , new UnitGarrisonInfo("Urban Spear Militia"));

		_Dict.add(UnitType.LevyInfantry, Arrays.asList("poland", "hungary") , "EE Spear Militia");

		_Dict.add(UnitType.LevyInfantry, "russia" , new UnitGarrisonInfo("Woodsmen", Arrays.asList("russia")));
		_Dict.add(UnitType.LevyInfantry, Arrays.asList("kievan_rus" , "russia") , new UnitGarrisonInfo("Junior Militia" , 1 ) );

		_Dict.add(UnitType.LevyInfantry, "byzantium" , "Contaratoi");	// OK+
		_Dict.add(UnitType.LevyInfantry, "timurids" , "aor trasc javelin");

		_Dict.add(UnitType.LevyInfantry,  "cumans" , new UnitGarrisonInfo("Steppe Alans" , Arrays.asList("cumans")));
		_Dict.add(UnitType.LevyInfantry,  "cumans" , "EE Spear Militia");

		// #### Islam - Muslims

		// Desert Raiders
		_Dict.add(UnitType.LevyInfantry,  Arrays.asList("moors" , "rum", "turks" , "kwarezm", "milan", "egypt") , new UnitGarrisonInfo("Desert Raiders", "caravan"));

		_Dict.add(UnitType.LevyInfantry, Arrays.asList("moors", "rum", "turks") , "Ahdath Militia");
		_Dict.add(UnitType.LevyInfantry, Arrays.asList("kwarezm", "milan", "egypt") , "Shurtah Militia", 1);

		// #### Pagand & barbs & rebels
		_Dict.add(UnitType.LevyInfantry, "lithuania" , "Baltic Spearmen");	 // default

		_Dict.add(UnitType.LevyInfantry, "mongols" , "Kashtim Thralls");

		_Dict.add(UnitType.LevyInfantry, "slave" , "Peasants");
	}

	/** Method should always return some unit. If unit in category is not found, some base unit cagegory will be returned */
	public UnitGarrisonInfo getFactionImplementation(UnitMetaDef unitMeta, String facionName, SettlementInfo settlement) throws Exception {
		UnitGarrisonInfo unit = null;

		unit = getFactionStrictImplementation(unitMeta, facionName, settlement);

		if(unit == null ) {
			// we need replacement !

			UnitType type = unitMeta.Type;
			UnitMetaDef metaTmp = unitMeta.clone();
			switch (type) {
				case Infantry:
					metaTmp.Type = UnitType.LevyInfantry;
					break;
				case LevyMissle:
					metaTmp.Type = UnitType.LevyInfantry;
					break;
				case Missle:
					metaTmp.Type = UnitType.LevyMissle;
					break;
				case HeavyInfantry:
					break;
				default:
					throw new PatcherLibBaseEx("Unable to determine replacamant !");
			}
			unit = getFactionImplementation(metaTmp, facionName, settlement);
		}

		if(unit == null)
			throw new PatcherLibBaseEx("Can't find unit & replacamants for "+unitMeta.Type + ", "+facionName+", "+settlement.Name);

		return unit;
	}

	public UnitGarrisonInfo getFactionStrictImplementation(UnitMetaDef unitMeta, String facionName, SettlementInfo settlement) throws Exception {
		UnitGarrisonInfo unitResult = new UnitGarrisonInfo();

		List<UnitGarrisonInfo> units;
		UnitGarrisonInfo unitTmp = null;

		// #### For rebels determine unit by faction that created settlement ####
		if(facionName.equals("slave")) {
			units = _Dict.load(unitMeta.Type, settlement.CreatedByFaction);
			unitTmp = getUnitByRequirements(units, settlement);

			if(unitTmp == null) {	// unit not found !
				// ok, lets take Rebel implementation :
				units = _Dict.load(unitMeta.Type, facionName);
				unitTmp = getUnitByRequirements(units, settlement);

				if(unitTmp == null)
					throw new PatcherLibBaseEx("UnitClass "+unitMeta.Type + " Settl="+settlement.Name+" faction slave not found in dictionary");
			}
			else {
				// ## Local Replecament for Rebels is FOUND ! ##
				// need to determine if slaves has access to it
				UnitDef unitDef = getExportDescrUnit().loadUnit(unitTmp.Name);

				if(! (unitDef.Ownership != null && unitDef.Ownership.contains("slave"))) {
					unitTmp = _Dict.getFirst(unitMeta.Type, facionName);

					if(unitTmp == null)
						throw new PatcherLibBaseEx("UnitClass "+unitMeta.Type + " Settl="+settlement.Name+" faction slave not found in dictionary");
				}
			}
		}
		else {
			units = _Dict.load(unitMeta.Type, facionName);
			unitTmp = getUnitByRequirements(units, settlement);
		}

		if(unitTmp == null) return null;	// Implementation not Found

		unitResult.Name = unitTmp.Name;
		unitResult.Type = unitMeta.Type;
		unitResult.Quantity = unitMeta.Quantity;

		// Add Meta Data with Dictionary result : Experience, Armor, Weapon
		unitResult.Experience = unitMeta.Experience + unitTmp.Experience;
		unitResult.Armor = unitMeta.Armor + unitTmp.Armor;
		unitResult.Weapon = unitMeta.Weapon + unitTmp.Weapon;

		return unitResult;
	}

	private UnitGarrisonInfo getUnitByRequirements(List<UnitGarrisonInfo> units , SettlementInfo settlement) {
		UnitGarrisonInfo unitTmp = null;
		for (UnitGarrisonInfo unitLoop : units) {
			if(unitLoop.RequiredResources == null || unitLoop.RequiredResources.size() == 0) {
				// ### Ok - Found, no requirements, it fits
				unitTmp = unitLoop;
				break;
			}
			else {
				// Lets check the requirements -> any requirement  True is good
				boolean isFitsRequirements = false;
				List<String> requirements = unitLoop.RequiredResources;

				for(String requirement : requirements) {
					if( settlement.Resources.contains(requirement)) {
						// blad - nie spelnia !!
						isFitsRequirements = true;
						break;
					}
				}

				if(isFitsRequirements) {
					// OK, this is it !!!
					unitTmp = unitLoop;
					break;
				}
			}
		}

		return unitTmp;
	}


	public List<UnitGarrisonInfo> getFactionImplementations(List<UnitMetaDef> unitMetaList, String facionName, SettlementInfo settlement) throws Exception {
		List<UnitGarrisonInfo> units = new ArrayList<>();

		for (UnitMetaDef unitMeda : unitMetaList) {
			UnitGarrisonInfo unit = getFactionImplementation(unitMeda, facionName, settlement);
			units.add(unit);
		}

		return units;
	}

	private ExportDescrUnitTyped getExportDescrUnit() throws Exception {
		if(exportDescrUnit == null)
			exportDescrUnit = fileEntityFactory.getFile(ExportDescrUnitTyped.class);

		return exportDescrUnit;
	}

	private ExportDescrUnitTyped exportDescrUnit;
	private FileEntityFactory fileEntityFactory;

	private UnitTypeFactionDictionary _Dict;

	public GarrisonMetaManager(FileEntityFactory fileEntityFactory) throws PatcherLibBaseEx {

		this.fileEntityFactory = fileEntityFactory;

		initialize();
	}
}
