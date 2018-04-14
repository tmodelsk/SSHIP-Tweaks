package tm.mtwModPatcher.sship.features.global;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.entities.UnitReplenishRate;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.unitModels.BattleModels;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.sship.lib.HiddenResources;
import tm.mtwModPatcher.sship.lib.Provinces;

import java.util.*;

import static tm.mtwModPatcher.lib.managers.FactionsDefs.*;
import static tm.mtwModPatcher.sship.lib.Buildings.*;
import static tm.mtwModPatcher.sship.lib.Units.*;


public class CatholicIberiaReworked extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		initFileEntities();

		aragonRemoveSpearMilitiaAddUrbanMilitia();
		spearmenForIberians();
		armoredSpearmen();
		archersIberiaReworked();
		lightCavalryReworked();


		caballerosVillanosReworked();
		replaceInitialTroops();

		zaragozaToFortress();

		barcelonaUpgrade();
		pamplonaUpgrade();

		rebelPamplona();
		almughavarsReworked();
	}

	private void almughavarsReworked() {

		// # Adjust recuit cost
		val footKnights = edu.loadUnit(DISMOUNTED_SWORD_MAILED_KNIGHTS);
		val almughavars = edu.loadUnit(DISMOUNTED_SWORD_MAILED_KNIGHTS);
		almughavars.StatCost.Cost = footKnights.StatCost.Cost;

		// ## Add Almughavars recuitment ##
		/* Org: Barracks Castle level 3
		    recruit_pool    "Almughavars"  1   0.25   1  0  requires factions { aragon, spain, portugal, } and hidden_resource aragon and event_counter HEAVY_MAIL_ARMOR 1
            recruit_pool    "Almughavars"  0   0.17   1  0  requires factions { aragon, spain, portugal, } and not hidden_resource aragon and event_counter HEAVY_MAIL_ARMOR 1
		* */
		//edb.removeUnitRecruitment(ALMUGHAVARS , ARAGON);
		val almughavarsRequire = "factions { aragon, } and hidden_resource aragon";
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(0), CastleType, ALMUGHAVARS,
				1, 0.08,1, 0 , almughavarsRequire);
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(1), CastleType, ALMUGHAVARS,
				1, 0.15,2, 0 , almughavarsRequire);
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(2), CastleType, ALMUGHAVARS,
				2, 0.25,3, 1 , almughavarsRequire);
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(3), CastleType, ALMUGHAVARS,
				2, 0.29,4, 2 , almughavarsRequire);
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(4), CastleType, ALMUGHAVARS,
				3, 0.33,5, 2 , almughavarsRequire);
	}

	private void spearmenForIberians() {
		// ## SERGEANT_SPEARMEN recuitment ##
		edu.addOwnershipAll(SERGEANT_SPEARMEN, ARAGON.symbol);
		edu.addOwnershipAll(SERGEANT_SPEARMEN, SPAIN.symbol);
		edu.addOwnershipAll(SERGEANT_SPEARMEN, PORTUGAL.symbol);

		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , ARAGON);
		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , SPAIN);
		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , PORTUGAL);

		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, ARAGON, BarracksCastle, allSpainHiddenResList, FRANCE);
		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, SPAIN, BarracksCastle, FRANCE);
		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, PORTUGAL, BarracksCastle, FRANCE);

		// add Spearmen recruitment for 1st level barracks only for Aragon and in aragon, replenish 50% as in 2nd level.
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(0), CastleType, SERGEANT_SPEARMEN,
				1, 0.08,1, 0 , "factions { aragon, } and not event_counter FULL_PLATE_ARMOR 1 and hidden_resource aragon");
	}

	private void armoredSpearmen() {
		/* Armored Sergeants
			(Castle) Castille: Armoured Serjeants
			At 1245, make Armoured Serjeants available for Aragón and Portugal,

			a wiec : dla Spain : Armoured Serjeants jak pisa / sicily . /  - dostepne od poczatku

			Aragon i Portugal, np: Armoured Serjeants jak england -> od HEAVY_MAIL_ARMOR
		 */

		edu.addOwnershipAll(ARMORED_SERGEANTS, ARAGON.symbol);
		edu.addOwnershipAll(ARMORED_SERGEANTS, SPAIN.symbol);
		edu.addOwnershipAll(ARMORED_SERGEANTS, PORTUGAL.symbol);

		edb.addToUnitRecruitment(ARMORED_SERGEANTS, SPAIN, BarracksCastle, allSpainHiddenResList, PISA);

		edb.addToUnitRecruitment(ARMORED_SERGEANTS, ARAGON, BarracksCastle, allSpainHiddenResList, ENGLAND);
		edb.addToUnitRecruitment(ARMORED_SERGEANTS, PORTUGAL, BarracksCastle, ENGLAND);
	}

	private void archersIberiaReworked() {
		edb.removeUnitRecruitment(PEASANT_ARCHERS , ARAGON);
		edb.removeUnitRecruitment(PEASANT_ARCHERS , SPAIN);
		edb.removeUnitRecruitment(PEASANT_ARCHERS , PORTUGAL);

		// TODO !!!!!
	}

	private void lightCavalryReworked() {
		// add Mounted Sergeants
		edu.addOwnershipAll(MOUNTED_SERGEANTS, ARAGON.symbol);
		edu.addOwnershipAll(MOUNTED_SERGEANTS, SPAIN.symbol);
		edu.addOwnershipAll(MOUNTED_SERGEANTS, PORTUGAL.symbol);

		edb.addToUnitRecruitment(MOUNTED_SERGEANTS, ARAGON, allSpainHiddenResList, PISA);
		edb.addToUnitRecruitment(MOUNTED_SERGEANTS, SPAIN, PISA);
		edb.addToUnitRecruitment(MOUNTED_SERGEANTS, PORTUGAL, PISA);

		removeAlforrats();
		// remove ...
	}
	private void removeAlforrats() {
		edb.removeUnitRecruitment(ALFORRATS);
	}

	public void rebelPamplona() {
		val prov = Provinces.Pamplona;
		val factionsSect = descrStrat.Factions;
		val lines = factionsSect.content().lines();

		// remove Garcia Ramirez general + cog
		val startIndex = lines.findExpFirstRegexLine("^\\s*character\\s+Garcia\\s+Ramirez,\\s+named\\s+character");
		val endIndex = lines.findExpFirstRegexLine("^\\s*unit\\s+cog\\s+", startIndex+1);
		lines.removeRange(startIndex, endIndex);

		factionsSect.moveSettlementBeforeSettl(prov, Provinces.Bordeaux);

		// put Garcia Ramirez in Zaragoza //  x 71, y 145 ;Zaragoza

		String GarciaRamirez = "";
		GarciaRamirez += "character	Garcia Ramirez, named character, male, age 20, x 69, y 139 ; near Zaragoza" +nl;	// x 71, y 144
		GarciaRamirez += "traits LoyaltyStarter 1 , Intelligent 1 , GoodBuilder 1, MilitaryInclination 1 , Military_Edu 1 , GoodCommander 1 , Royal_Blood_Aragonese 1 , ReligionStarter 1"+nl;
		GarciaRamirez += "army" +nl;
		GarciaRamirez += "unit\t\tSE Bodyguard\t\t\t\texp 1 armour 0 weapon_lvl 0" +nl;

		String AlfonsoRamirezRegex = "^character\\s+Alfonso\\s+Ramirez";
		val generalAlfonsoIndex = lines.findExpFirstRegexLine(AlfonsoRamirezRegex);
		lines.insertAt(generalAlfonsoIndex, GarciaRamirez);

		String garrison = ""; // Felipe Tejedor , Wilfredo Lucio Alejandro
		garrison += "\n" +
				"character	sub_faction aragon, Hernando Villaseca, named character, male, age 29, x 66, y 153 ;Pamplona\n" +
				"traits NaturalMilitarySkill 1 , GoodCommander 1 , ReligiousActivity 4\n" +
				"army	" +
				"unit		SE Bodyguard				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Mailed Knights				exp 0 armour 0 weapon_lvl 0  \n" +
				"unit		"+ CABALLEROS_VILLANOS +"				exp 1 armour 0 weapon_lvl 0  \n" +
				"unit		Dismounted Sword Mailed Knights				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		"+ URBAN_SPEAR_MILITIA +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		Sergeant Spearmen				exp 0 armour 0 weapon_lvl 0\n" +
				"unit		"+ BASQUE_ARCHERS +"				exp 1 armour 0 weapon_lvl 0\n" +
				"unit		"+ CROSSBOW_MILITIA +"				exp 0 armour 0 weapon_lvl 0    \n" +
				"unit		"+ CROSSBOW_MILITIA +"				exp 0 armour 0 weapon_lvl 0";

		val rebelSettlementsEndIndex = factionsSect.loadSettlemenBlockEndIndex("Tabriz_Province");
		// TODO : dodac go przed : character	sub_faction hre, Gottfried der_Bartige
		lines.insertAt(rebelSettlementsEndIndex+1, garrison);
	}

	private void zaragozaToFortress() {
		val prov = Provinces.Zaragoza;
		descrStrat.removeAllBuildings(prov);

		descrStrat.setToCastle(prov);
		descrStrat.setFactionCreator(prov, FactionsDefs.ARAGON.symbol);
		descrStrat.insertSettlementBuilding(prov, WallsCastle.Name, WallsCastle.L4_Fortress);
		descrStrat.insertSettlementBuilding(prov, BarracksCastle, BarracksCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, StablesCastle, StablesCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, MissileCastle, MissileCastleLevels.get(0));

		descrStrat.insertSettlementBuilding(prov, MarketCastle, MarketCastleLevels.get(2));
		descrStrat.insertSettlementBuilding(prov, PortCastle, PortCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SeaTradeCastle, SeaTradeCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, RiverAccess, RiverAccessLevel);
		descrStrat.insertSettlementBuilding(prov, TempleCatholicCastle, TempleCatholicCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, MonasteryCatholicCastle, MonasteryCatholicCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, TavernCastle, TavernCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, WaterSupply, WaterSupplyLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, Health, HealthLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, StoneMason, StoneMasonLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, LoggingCamp, LoggingCampLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SmithCastle, SmithCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, BakeryCastle, BakeryCastleLevel);

		descrStrat.insertSettlementBuilding(prov, RoadCastle, RoadCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, Farms, FarmsLevels.get(2));

		descrStrat.insertSettlementBuilding(prov, MinesCastle, MinesCastleLevels.get(0));

		// Order House
		descrRegions.addResource(prov, DescrRegions.KnightsOfSantiago, DescrRegions.StJohnKnights);
		descrStrat.insertSettlementBuilding(prov, HospitallersCastle, HospitallersCastleLevels.get(0));

		//resource	iron,	73, 152
		descrStrat.Resources.addResource(prov, "iron", 73, 152);
		requestForMapRemoval();

		// Merchant - move to top spot :  x162 y158 kolo Florencji
		val lines = descrStrat.Factions.content().lines();
		val merchantIndex = lines.findExpFirstRegexLine("character\\s+Berenguel\\s+Cano\\s*,\\s*merchant.+x\\s+67,\\s*y\\s+118");
		lines.replaceLine(merchantIndex, "character\tBerenguel Cano, merchant, male, age 38, x 162, y 158 ; near Florence");
		//character	Berenguel Cano, merchant, male, age 38, x 67, y 118
	}

	private void barcelonaUpgrade() {
		val prov = Provinces.Barcelona;

		descrStrat.insertSettlementBuilding(prov, WaterSupply, WaterSupplyLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SeaTradeCity, SeaTradeCityLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, MerchantGuild, MerchantGuildLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, ItalianTraders, ItalianTradersLevels.get(0));
		descrStrat.removeSettlementBuilding(prov, PortCity);
		descrStrat.insertSettlementBuilding(prov, PortCity, PortCityLevels.get(1));

	}
	private void pamplonaUpgrade() {
		val prov = Provinces.Pamplona;

		descrStrat.insertSettlementBuilding(prov, RoadCastle, RoadCastleLevels.get(0));
		descrRegions.addResource(prov, DescrRegions.KnightsOfSantiago );
	}

	private void replaceInitialTroops() throws PatcherLibBaseEx {
		LinesProcessor lines = descrStrat.Factions.content().lines();

		int aragonIndex = lines.findExpFirstRegexLine("^;## ARAGON ##\\s*");
		// Zaragoza
		replaceInitialUnit(PEASANT_ARCHERS,PRUSSIAN_ARCHERS , aragonIndex);
		replaceInitialUnit(PEASANT_ARCHERS,CROSSBOW_MILITIA , aragonIndex);
		replaceInitialUnit(PEASANT_ARCHERS,null , aragonIndex);

		replaceInitialUnit(JAVELINMEN,null , aragonIndex);
		replaceInitialUnit(JAVELINMEN,null , aragonIndex);

		replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, ALMUGHAVARS , aragonIndex , 1);
		replaceInitialUnit(SPEAR_MILITIA, ALMUGHAVARS , aragonIndex);

		replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		replaceInitialUnit(ALFORRATS, null , aragonIndex);

		// Barcelona
		replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, CROSSBOW_MILITIA , aragonIndex);
		replaceInitialUnit(PEASANT_ARCHERS, ALMUGHAVARS , aragonIndex);

		// Pamlpona
		replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);

		int spainIndex = lines.findExpFirstRegexLine(";## CASTILE & LEON ##\\s*");
		replaceInitialUnit(JAVELINMEN, PRUSSIAN_ARCHERS , spainIndex);
		replaceInitialUnit(JAVELINMEN, CROSSBOW_MILITIA , spainIndex);
		replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , spainIndex , 1);
		replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , spainIndex , 1);
		replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , spainIndex , 1);

		int portugalIndex = lines.findExpFirstRegexLine(";## PORTUGAL ##\\s*");
		replaceInitialUnit(PEASANT_ARCHERS, PRUSSIAN_ARCHERS , portugalIndex);
		replaceInitialUnit(LUSITANIAN_JAVELINMEN, CROSSBOW_MILITIA , portugalIndex);
		replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , portugalIndex , 1);

	}
	private void replaceInitialUnit(String oldUnitName , String newUnitName, int factionStartIndex) throws PatcherLibBaseEx {
		replaceInitialUnit(oldUnitName, newUnitName, factionStartIndex, 0);
	}
	private void replaceInitialUnit(String oldUnitName , String newUnitName, int factionStartIndex , int experienceLevel) throws PatcherLibBaseEx {
		LinesProcessor lines = descrStrat.Factions.content().lines();

		int unitIndex = lines.findExpFirstRegexLine("^unit\\s+"+ oldUnitName + ".+" , factionStartIndex+1);

		if(newUnitName != null)
			lines.replaceLine(unitIndex , "unit\t\t" + newUnitName + "\t\t\t\texp " + experienceLevel + " armour 0 weapon_lvl 0");
		else
			lines.remove(unitIndex);
	}

	private void aragonRemoveSpearMilitiaAddUrbanMilitia() {
		edb.removeUnitRecruitment(JAVELINMEN , ARAGON);
		edb.removeUnitRecruitment(LUSITANIAN_JAVELINMEN , ARAGON);
		edb.removeUnitRecruitment(PEASANTS , ARAGON);
		edb.removeUnitRecruitment(SPEAR_MILITIA , ARAGON);

		edb.removeUnitRecruitment(URBAN_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(URBAN_SPEAR_MILITIA, ARAGON, BarracksCity, PISA);

		edu.addOwnershipAll(NE_URBAN_MILITIA, ARAGON.symbol);
		edb.removeUnitRecruitment(NE_URBAN_MILITIA , ARAGON);
		edb.addToUnitRecruitment(NE_URBAN_MILITIA, ARAGON, BarracksCity, PISA);

		edu.addOwnershipAll(PAVISE_SPEAR_MILITIA, ARAGON.symbol);
		edb.removeUnitRecruitment(PAVISE_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(PAVISE_SPEAR_MILITIA, ARAGON, BarracksCity, PISA);
	}
	private void caballerosVillanosReworked() throws PatcherLibBaseEx {
		String cabVillanos = CABALLEROS_VILLANOS;

		edu.addOwnershipAll(cabVillanos, ARAGON.symbol);

		// TODO: Caballeros Villanos tylko w hidden_resource aragon

		String reqs = "factions { aragon, } and not event_counter ENGLISH_ARCHERS 1";
		edb.insertRecruitmentBuildingCapabilities(StablesCastle , StablesCastleLevels.get(0) , CastleType , cabVillanos , 0, UnitReplenishRate.R10, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities(StablesCastle , StablesCastleLevels.get(1) , CastleType , cabVillanos , 0, UnitReplenishRate.R9, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities(StablesCastle , StablesCastleLevels.get(2) , CastleType , cabVillanos , 0, UnitReplenishRate.R8, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities(StablesCastle , StablesCastleLevels.get(3) , CastleType , cabVillanos , 0, UnitReplenishRate.R7, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities(StablesCastle , StablesCastleLevels.get(4) , CastleType , cabVillanos , 0, UnitReplenishRate.R6, 1, 0, reqs);
	}

	private static final List<String> allSpainHiddenResList = Arrays.asList(HiddenResources.Aragon, HiddenResources.Spain, HiddenResources.Portugal, HiddenResources.Andalusia);

	private void initFileEntities() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		descrMercenaries = getFileRegisterForUpdated(DescrMercenaries.class);
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		battleModels = getFileRegisterForUpdated(BattleModels.class);
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
	}
	@Override
	public Set<UUID> getConflictingFeatures() {
		return new HashSet<>(Arrays.asList(CatholicIberiaUnitsRecruitmentIncreased.Id));
	}

	public CatholicIberiaReworked() {
		super("Catholic Iberia reworked");

		addCategory("Global");
		addCategory("Units");

		addOverrideTask(new OverrideCopyTask("UnitInfos"));
	}

	private ExportDescrBuilding edb;
	private ExportDescrUnitTyped edu;
	private DescrMercenaries descrMercenaries;
	private DescrStratSectioned descrStrat;
	private BattleModels battleModels;
	private DescrRegions descrRegions;

	private String nl = System.lineSeparator();
	private String nullStr = null;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b59df073-1de3-4358-b2bf-a88d781343e1");
}
