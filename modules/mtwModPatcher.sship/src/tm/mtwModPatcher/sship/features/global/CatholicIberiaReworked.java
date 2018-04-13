package tm.mtwModPatcher.sship.features.global;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
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
import tm.mtwModPatcher.sship.lib.Provinces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
		removePeasantArchers();
		addCaballerosVillanosToAragon();
		replaceInitialTroops();

		zaragozaToFortress();

		barcelonaUpgrade();
		pamplonaUpgrade();

		rebelPamplona();

		// Merchant - x162 y158 kolo Florencji
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
		GarciaRamirez += "character	Garcia Ramirez, named character, male, age 20, x 71, y 145 ; Pamplona" +nl;
		GarciaRamirez += "traits LoyaltyStarter 1 , Intelligent 1 , GoodBuilder 1, MilitaryInclination 1 , Military_Edu 1 , GoodCommander 1 , Royal_Blood_Aragonese 1 , ReligionStarter 1"+nl;
		GarciaRamirez += "army" +nl;
		GarciaRamirez += "unit\t\tSE Bodyguard\t\t\t\texp 1 armour 0 weapon_lvl 0" +nl;

		String AlfonsoRamirezRegex = "^character\\s+Alfonso\\s+Ramirez";
		val generalAlfonsoIndex = lines.findExpFirstRegexLine(AlfonsoRamirezRegex);
		lines.insertAt(generalAlfonsoIndex, GarciaRamirez);

		String garrison = "\n"; // Felipe Tejedor , Wilfredo Lucio Alejandro
		garrison += "character\tsub_faction france, Felipe Alejandro, general, male, age 29, x 66, y 153 ;Pamplona\n" +
				"traits NaturalMilitarySkill 1 , GoodCommander 1 , ReligiousActivity 4\n" +
				"army\n" +
				"unit\t\tNE Bodyguard\t\t\t\texp 1 armour 0 weapon_lvl 0\n" +
				"unit\t\tMailed Knights\t\t\t\texp 0 armour 0 weapon_lvl 0  \n" +
				"unit\t\tDismounted Sword Mailed Knights\t\t\t\texp 0 armour 0 weapon_lvl 0\n" +
				"unit\t\tDismounted Sword Mailed Knights\t\t\t\texp 0 armour 0 weapon_lvl 0\n" +
				"unit\t\tMercenary Spearmen\t\t\texp 2 armour 0 weapon_lvl 0\n" +
				"unit\t\tSergeant Spearmen\t\t\texp 1 armour 0 weapon_lvl 0\n" +
				"unit\t\tSergeant Spearmen\t\t\texp 2 armour 0 weapon_lvl 0\n" +
				"unit\t\tPeasant Archers\t\t\t\texp 1 armour 0 weapon_lvl 0\n" +
				"unit\t\tPeasant Archers\t\t\t\texp 0 armour 0 weapon_lvl 0    \n" +
				"unit\t\tPeasants\t\t\t\texp 0 armour 0 weapon_lvl 0";

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
	}

	private void barcelonaUpgrade() {
		val prov = Provinces.Barcelona;

		descrStrat.insertSettlementBuilding(prov, WaterSupply, WaterSupplyLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SeaTradeCity, SeaTradeCityLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, MerchantGuild, MerchantGuildLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, ItalianTraders, ItalianTradersLevels.get(0));


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

		replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex , 1);
		replaceInitialUnit(SPEAR_MILITIA, null , aragonIndex);

		replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		replaceInitialUnit(ALFORRATS, null , aragonIndex);

		// Barcelona
		replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, CROSSBOW_MILITIA , aragonIndex);
		replaceInitialUnit(PEASANT_ARCHERS,CROSSBOW_MILITIA , aragonIndex);

		// Pamlpona
		replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);

		int spainIndex = lines.findExpFirstRegexLine(";## CASTILE & LEON ##\\s*");
		replaceInitialUnit("Javelinmen","Prussian Archers" , spainIndex);
		replaceInitialUnit("Javelinmen","Crossbow Militia" , spainIndex);
		replaceInitialUnit("Spear Militia","Crusader Sergeants" , spainIndex , 1);
		replaceInitialUnit("Spear Militia","Crusader Sergeants" , spainIndex , 1);
		replaceInitialUnit("Spear Militia","Sergeant Spearmen" , spainIndex , 1);

		int portugalIndex = lines.findExpFirstRegexLine(";## PORTUGAL ##\\s*");
		replaceInitialUnit("Peasant Archers","Prussian Archers" , portugalIndex);
		replaceInitialUnit("Lusitanian Javelinmen","Crossbow Militia" , portugalIndex);
		replaceInitialUnit("Spear Militia","Crusader Sergeants" , portugalIndex , 1);

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

	private void removePeasantArchers() {
		edb.removeUnitRecruitment(PEASANT_ARCHERS , ARAGON);
		edb.removeUnitRecruitment(PEASANT_ARCHERS , SPAIN);
		edb.removeUnitRecruitment(PEASANT_ARCHERS , PORTUGAL);
	}
	private void aragonRemoveSpearMilitiaAddUrbanMilitia() {
		edb.removeUnitRecruitment(SPEAR_MILITIA , ARAGON);

		edb.removeUnitRecruitment(URBAN_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(URBAN_SPEAR_MILITIA, ARAGON, Arrays.asList(PISA));


		//edu.addOwnershipAll(NE_URBAN_MILITIA, ARAGON.symbol);
		edb.removeUnitRecruitment(NE_URBAN_MILITIA , ARAGON);
		edb.addToUnitRecruitment(NE_URBAN_MILITIA, ARAGON, Arrays.asList(PISA));

		edb.removeUnitRecruitment(PAVISE_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(PAVISE_SPEAR_MILITIA, ARAGON, Arrays.asList(PISA));
	}
	private void addCaballerosVillanosToAragon() throws PatcherLibBaseEx {
		String caballerosVillanos = "Caballeros Villanos";

		edu.addOwnership(caballerosVillanos, "aragon");
		edu.addOwnership(caballerosVillanos, "aragon", 0);
		edu.addOwnership(caballerosVillanos, "aragon", 1);

		// levels stables knights_stables barons_stables earls_stables kings_stables
		String reqs = "factions { aragon, } and not event_counter ENGLISH_ARCHERS 1";

		edb.insertRecruitmentBuildingCapabilities("equestrian" , "stables" , "castle" , caballerosVillanos , 0, UnitReplenishRate.R10, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities("equestrian" , "knights_stables" , "castle" , caballerosVillanos , 0, UnitReplenishRate.R9, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities("equestrian" , "barons_stables" , "castle" , caballerosVillanos , 0, UnitReplenishRate.R8, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities("equestrian" , "earls_stables" , "castle" , caballerosVillanos , 0, UnitReplenishRate.R7, 1, 0, reqs);
		edb.insertRecruitmentBuildingCapabilities("equestrian" , "kings_stables" , "castle" , caballerosVillanos , 0, UnitReplenishRate.R6, 1, 0, reqs);
	}

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
	}

	private ExportDescrBuilding edb;
	private ExportDescrUnitTyped edu;
	private DescrMercenaries descrMercenaries;
	private DescrStratSectioned descrStrat;
	private BattleModels battleModels;
	private DescrRegions descrRegions;

	private String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b59df073-1de3-4358-b2bf-a88d781343e1");
}
