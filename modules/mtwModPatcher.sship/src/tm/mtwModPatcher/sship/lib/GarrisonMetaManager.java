package tm.mtwModPatcher.sship.lib;

import lombok.Setter;
import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.managers.garrisons.UnitGarrisonInfo;
import tm.mtwModPatcher.lib.managers.garrisons.UnitMetaDef;
import tm.mtwModPatcher.lib.managers.garrisons.UnitType;
import tm.mtwModPatcher.lib.managers.garrisons.UnitTypeFactionDictionary;
import tm.mtwModPatcher.sship.features.garrisons.GarrisonOnAssaultRaisingPlayerVsAi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tm.mtwModPatcher.lib.managers.FactionsDefs.*;
import static tm.mtwModPatcher.sship.lib.Units.*;

/**
 * Created by Tomek on 2016-11-13.
 */
public class GarrisonMetaManager {


	private void initialize() throws PatcherLibBaseEx {
		dict = new UnitTypeFactionDictionary();
		
		configureLevyInfantry();
		configureInfantry();
		configureLevyMissle();
		configureMissle();
		configureHeavyInfantry();
	}
	private void ensureInitiliazed() {
		if(!isInitialized) {
			initialize();
			isInitialized = true;
		}
	}

	private void configureHeavyInfantry() throws PatcherLibBaseEx {
		val type = UnitType.HeavyInfantry;

		// Western Catholics
		dict.add(type,
				Arrays.asList(	"aragon", "spain", "portugal" , "france", "hre",
						"hungary" , "pisa", "venice"),
				"Dismounted Sword Mailed Knights");

		dict.add(type , Arrays.asList("scotland" , "denmark") , "Dismounted Mailed Knights");
		dict.add(type , "norway" , "Norse Axemen");
		dict.add(type , Arrays.asList("england", "sicily") , "Dismounted Norman Cavalry");

		dict.add(type , "poland" , "Dismounted Polish Nobles");
		dict.add(type , "papal_states" , "Papal Guard");

		dict.add(type , "jerusalem" , "Dismounted Knights Of Jerusalem");

		// ## Orthodox : ##
		dict.add(type , Arrays.asList( "russia", "kievan_rus", "") , "Dismounted Druzhina");
		dict.add(type , "teutonic_order" , "Dismounted Vlastelcici");
		dict.add(type , "timurids" , "georgian medium swordsmen");
		dict.add(type , "byzantium" , "English Huscarls"); // old : Castrophilakae


		// #### Islam - Muslims
		dict.add(type , Arrays.asList("turks") , "Dismounted Ghulams");

		dict.add(type , Arrays.asList("moors", "rum", "kwarezm", "milan", "egypt") , "Dismounted Fari Lancers");

		dict.add(type , "mongols" , "Disamounted Turhagut");
		dict.add(type , "cumans" , new UnitGarrisonInfo("Dismounted Bekh Druzhina"));
		dict.add(type , "lithuania" , new UnitGarrisonInfo("Ducal Axe"));

		dict.add(type, "slave" , "Macemen");

	}

	private void configureMissle() throws PatcherLibBaseEx {
		UnitType type = UnitType.Missle;

		// #### Berber Archers - as aor Local Troop ###
		List<String> berberArchersAllowedFactions = FactionsDefs.islamFactionsList();
		berberArchersAllowedFactions.remove("mongols");
		berberArchersAllowedFactions.add("scotland");
		berberArchersAllowedFactions.add("byzantium");
		berberArchersAllowedFactions.add("lithuania");

		dict.add(type , berberArchersAllowedFactions , new UnitGarrisonInfo("Berber Archers", 1 , "berber"));

		dict.add(type,
				Arrays.asList( "aragon", "portugal", "spain" , "hre" , "france", "denmark", "norway",
								"hungary", "pisa", "venice" , "papal_states", "sicily" , "jerusalem",
								"teutonic_order", "timurids"),
				"Crossbow Militia");

		dict.add(type , "england" , "Archer Militia");
		dict.add(type , SCOTLAND, SCOTS_SKIRMISHER, HiddenResources.SCOTLAND);	// and access to Berber Archers

		dict.add(type , Arrays.asList("poland", "russia", "kievan_rus") , "EE Crossbow Militia");

		dict.add(type , "byzantium" , "Toxotae" , 1);

		dict.add(type , Arrays.asList("moors", "rum", "turks", "kwarezm", "milan", "egypt") , "ME Crossbowmen");

		dict.add(type , "lithuania" , new UnitGarrisonInfo("Hunters", 2 , "lithuania"));
		dict.add(type , "mongols" , "Khabutu Archers");
		dict.add(type , "cumans" , "Dismounted Cuman Militia",1);
	}

	private void configureLevyMissle() throws PatcherLibBaseEx {
		UnitType type = UnitType.LevyMissle;

		// #### Berber Archers - as aor Local Troop ###
		List<String> berberArchersAllowedFactions = FactionsDefs.allFactionsList();
		berberArchersAllowedFactions.remove("mongols");
		dict.add(type , berberArchersAllowedFactions , new UnitGarrisonInfo("Berber Archers" , "berber"));
		
		// Western Catholics & Ortodox Christians
		dict.add(type, ENGLAND, ARCHER_MILITIA, HiddenResources.ENGLAND );
		dict.add(type, ENGLAND, ARCHER_MILITIA, HiddenResources.SCOTLAND );
		dict.add(type, ENGLAND, ARCHER_MILITIA, HiddenResources.IRELAND );

		dict.add(type, SCOTLAND, SCOTS_SKIRMISHER, HiddenResources.SCOTLAND );
		dict.add(type, NORWAY,  BONDIR, HiddenResources.NORWAY);

		dict.add(type , "byzantium" , "Toxotae");

		dict.add(type , "lithuania" , new UnitGarrisonInfo("Hunters", 1 , "lithuania"));
		dict.add(type , "cumans" , new UnitGarrisonInfo("EE Peasant Archers", 1));
		dict.add(type , "mongols" , new UnitGarrisonInfo("ME Peasant Archers", 1));

		dict.add(type, "slave", SPEAR_MILITIA);	//
	}

	private void configureInfantry() throws PatcherLibBaseEx {
		// Western Catholics & Ortodox Christians
		dict.add(UnitType.Spearmen,
				Arrays.asList(	"england", "spain", "aragon", "portugal"),
				new UnitGarrisonInfo(SPEAR_MILITIA, 2));

		dict.add(UnitType.Spearmen, "scotland" , new UnitGarrisonInfo("Axe Militia", 2, Arrays.asList("scotland")));
		dict.add(UnitType.Spearmen, "scotland" , new UnitGarrisonInfo(SPEAR_MILITIA, 2));

		dict.add(UnitType.Spearmen, Arrays.asList("norway" ,"denmark") , new UnitGarrisonInfo("Landevearnmenn", Arrays.asList("norway","denmark")));
		dict.add(UnitType.Spearmen, Arrays.asList("norway" ,"denmark") , SPEAR_MILITIA , 2);


		dict.add(UnitType.Spearmen, Arrays.asList("france", "hre", "jerusalem" , "pisa", "venice", "papal_states", "sicily"), "Sergeant Spearmen");

		dict.add(UnitType.Spearmen, "poland" , new UnitGarrisonInfo("Smolensk Infantry", "poland"));
		dict.add(UnitType.Spearmen, "poland" , new UnitGarrisonInfo("EE Spear Militia", 2));

		dict.add(UnitType.Spearmen , Arrays.asList("russia", "kievan_rus") , new UnitGarrisonInfo("Rus Senior Militia", Arrays.asList("russia" , "kievan_rus")));
		dict.add(UnitType.Spearmen , Arrays.asList("russia", "kievan_rus", "hungary") , "EE Spear Militia", 2);	// default

		dict.add(UnitType.Spearmen, "teutonic_order" , new UnitGarrisonInfo(SPEAR_MILITIA, 2));
		dict.add(UnitType.Spearmen , "byzantium" , "Contaratoi" , 1);
		dict.add(UnitType.Spearmen , "timurids" , "georgian light spearmen");

		// #### Islam - Muslims
		dict.add(UnitType.Spearmen , Arrays.asList("moors", "rum", "turks", "kwarezm", "milan", "egypt") , "Ahdath Militia" , 1);

		// #### Pagand & barbs & rebels

		dict.add(UnitType.Spearmen , "cumans" , "EE Spear Militia", 2);

		dict.add(UnitType.Spearmen , "lithuania" , "Baltic Spearmen", 2);
		dict.add(UnitType.Spearmen , "mongols" , "Sibiryaki Senior Militia");
		dict.add(UnitType.Spearmen , "slave" , "Mercenary Spearmen");
	}

	private void configureLevyInfantry() throws PatcherLibBaseEx {
		// Western Catholics & Ortodox Christiansva
		val levyInf = UnitType.LevyInfantry;
		dict.add(levyInf,
				Arrays.asList(	"france", "hre",
						"spain", "portugal", "jerusalem"),
				new UnitGarrisonInfo(SPEAR_MILITIA));

		if(featureContainer != null && featureContainer.isFeatureEnabled(GarrisonOnAssaultRaisingPlayerVsAi.Id))
			dict.add(levyInf, ARAGON, URBAN_SPEAR_MILITIA);
		else
			dict.add(levyInf, ARAGON, SPEAR_MILITIA);


		dict.add(levyInf, SCOTLAND , "Axe Militia" );

		dict.add(levyInf, ENGLAND , new UnitGarrisonInfo("Fyrd Spearmen" , HiddenResources.ENGLAND) );
		dict.add(levyInf, ENGLAND , new UnitGarrisonInfo(SPEAR_MILITIA ) );	//  default

		dict.add(levyInf, NORWAY , new UnitGarrisonInfo("Drengjar" , "norway") );	// Viking Drengjar
		dict.add(levyInf, NORWAY , new UnitGarrisonInfo(SPEAR_MILITIA ) );	//  default

		dict.add(levyInf, "denmark" , new UnitGarrisonInfo("Axe Militia" , Arrays.asList("norway", "denmark")) );
		dict.add(levyInf, "denmark" , new UnitGarrisonInfo(SPEAR_MILITIA ) );	//  default

		dict.add(levyInf, "teutonic_order" , new UnitGarrisonInfo("Slav Levies" , Arrays.asList("serbia")) );	// Viking Drengjar
		dict.add(levyInf, "teutonic_order" , new UnitGarrisonInfo(SPEAR_MILITIA ) );	//  default


		dict.add(levyInf, Arrays.asList("pisa", "venice", "papal_states", "sicily") , new UnitGarrisonInfo(URBAN_SPEAR_MILITIA));

		dict.add(levyInf, Arrays.asList("poland", "hungary") , "EE Spear Militia");

		dict.add(levyInf, "russia" , new UnitGarrisonInfo("Woodsmen", Arrays.asList("russia")));
		dict.add(levyInf, Arrays.asList("kievan_rus" , "russia") , new UnitGarrisonInfo("Junior Militia" , 1 ) );

		dict.add(levyInf, "byzantium" , "Contaratoi");	// OK+
		dict.add(levyInf, "timurids" , "aor trasc javelin");

		dict.add(levyInf,  "cumans" , new UnitGarrisonInfo("Steppe Alans" , Arrays.asList("cumans")));
		dict.add(levyInf,  "cumans" , "EE Spear Militia");

		// #### Islam - Muslims

		// Desert Raiders
		dict.add(levyInf,  Arrays.asList("moors" , "rum", "turks" , "kwarezm", "milan", "egypt") , new UnitGarrisonInfo("Desert Raiders", "caravan"));

		dict.add(levyInf, Arrays.asList("moors", "rum", "turks") , "Ahdath Militia");
		dict.add(levyInf, Arrays.asList("kwarezm", "milan", "egypt") , "Shurtah Militia", 1);

		// #### Pagand & barbs & rebels
		dict.add(levyInf, "lithuania" , "Baltic Spearmen");	 // default

		dict.add(levyInf, "mongols" , "Kashtim Thralls");

		dict.add(levyInf, "slave" , SPEAR_MILITIA);
	}

	/** Method should always return some unit. If unit in category is not found, some base unit cagegory will be returned */
	public UnitGarrisonInfo getFactionImplementation(UnitMetaDef unitMeta, String facionName, SettlementInfo settlement) throws Exception {
		ensureInitiliazed();

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
		ensureInitiliazed();
		UnitGarrisonInfo unitResult = new UnitGarrisonInfo();

		List<UnitGarrisonInfo> units;
		UnitGarrisonInfo unitTmp = null;

		// #### For rebels determine unit by faction that created settlement ####
		if(facionName.equals("slave")) {
			units = dict.load(unitMeta.Type, settlement.CreatedByFaction);
			unitTmp = getUnitByRequirements(units, settlement);

			if(unitTmp == null) {	// unit not found !
				/* ok, lets take Rebel implementation : */
				units = dict.load(unitMeta.Type, facionName);
				unitTmp = getUnitByRequirements(units, settlement);

				if(unitTmp == null)
					throw new PatcherLibBaseEx("UnitClass "+unitMeta.Type + " Settl="+settlement.Name+" faction slave not found in dictionary");
			}
			else {
				/* ## Local Replecament for Rebels is FOUND ! ##
				// need to determine if slaves has access to it */
				UnitDef unitDef = edu().loadUnit(unitTmp.Name);

				if(! (unitDef.Ownership != null && unitDef.Ownership.contains("slave"))) {
					unitTmp = dict.getFirst(unitMeta.Type, facionName);

					if(unitTmp == null)
						throw new PatcherLibBaseEx("UnitClass "+unitMeta.Type + " Settl="+settlement.Name+" faction slave not found in dictionary");
				}
			}
		}
		else {
			units = dict.load(unitMeta.Type, facionName);
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
		ensureInitiliazed();

		List<UnitGarrisonInfo> units = new ArrayList<>();

		for (UnitMetaDef unitMeda : unitMetaList) {
			UnitGarrisonInfo unit = getFactionImplementation(unitMeda, facionName, settlement);
			units.add(unit);
		}

		return units;
	}

	private ExportDescrUnitTyped edu() throws Exception {
		if(edu == null)
			edu = fileEntityFactory.getFile(ExportDescrUnitTyped.class);

		return edu;
	}

	private ExportDescrUnitTyped edu;
	private FileEntityFactory fileEntityFactory;
	@Setter private FeatureList featureContainer;
	private boolean isInitialized = false;

	private UnitTypeFactionDictionary dict;

	public GarrisonMetaManager(FileEntityFactory fileEntityFactory) throws PatcherLibBaseEx {
		this.fileEntityFactory = fileEntityFactory;
	}
}
