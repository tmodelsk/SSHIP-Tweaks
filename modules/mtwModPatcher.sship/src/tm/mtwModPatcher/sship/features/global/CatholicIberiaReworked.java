package tm.mtwModPatcher.sship.features.global;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.entities.UnitReplenishRate;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.unitModels.BattleModels;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.sship.lib.Provinces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static tm.mtwModPatcher.lib.managers.FactionsDefs.ARAGON;
import static tm.mtwModPatcher.lib.managers.FactionsDefs.PISA;
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
		addCaballerosVillanosToAragon();
		replaceInitialTroops();

		zaragozaMineResource();
		zaragozaToFortress();
	}

	private void zaragozaToFortress() {
		val prov = Provinces.Zaragoza;
		descrStrat.removeAllBuildings(prov);

		descrStrat.setToCastle(prov);
		// ToDO :  	faction_creator aragon - ladne mury
		descrStrat.insertSettlementBuilding(prov, WallsCastle.Name, WallsCastle.L4_Fortress);
		descrStrat.insertSettlementBuilding(prov, BarracksCastle, BarracksCastleLevels.get(1));

		descrStrat.insertSettlementBuilding(prov, MarketCastle, MarketCastleLevels.get(2));
		descrStrat.insertSettlementBuilding(prov, PortCastle, PortCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, SeaTradeCastle, SeaTradeCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, RiverAccess, RiverAccessLevel);
		descrStrat.insertSettlementBuilding(prov, TempleCatholicCastle, TempleCatholicCastleLevels.get(1));
		descrStrat.insertSettlementBuilding(prov, MonasteryCatholicCastle, MonasteryCatholicCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, TavernCastle, TavernCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, WaterSupply, WaterSupplyLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, Health, HealthLevels.get(0));

		// ToDO: dodac budynki
		// type stonemason sitehut
		// type logging_camp carpenter
		// type smith blacksmith
		// type bakery bakery

		descrStrat.insertSettlementBuilding(prov, RoadCastle, RoadCastleLevels.get(0));
		descrStrat.insertSettlementBuilding(prov, Farms, FarmsLevels.get(2));

		descrStrat.insertSettlementBuilding(prov, MinesCastle, MinesCastleLevels.get(1));
	}

	private void zaragozaMineResource() {
		val lines = descrStrat.Resources.getContent().getLines();

		val zaragozaProvIndex = lines.findExpFirstRegexLine("^;Zaragoza_Province");
		lines.insertAt(zaragozaProvIndex+1, "resource\tiron,\t73, 152");
		requestForMapRemoval();
		//resource	iron,	73, 152
	}

	private void replaceInitialTroops() throws PatcherLibBaseEx {
		LinesProcessor lines = descrStrat.Factions.getContent().getLines();

		int aragonIndex = lines.findExpFirstRegexLine("^;## ARAGON ##\\s*");
		// Zaragoza
		replaceInitialUnit(PEASANT_ARCHERS,PRUSSIAN_ARCHERS , aragonIndex);
		replaceInitialUnit(PEASANT_ARCHERS,CROSSBOW_MILITIA , aragonIndex);
		replaceInitialUnit(PEASANT_ARCHERS,CROSSBOW_MILITIA , aragonIndex);

		replaceInitialUnit(SPEAR_MILITIA, CRUSADER_SERGEANTS , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, SERGEANT_SPEARMEN , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex , 1);
		replaceInitialUnit(SPEAR_MILITIA, null , aragonIndex);

		replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);
		replaceInitialUnit(ALFORRATS, CABALLEROS_VILLANOS , aragonIndex);

		// Barcelona
		replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex);
		replaceInitialUnit(SPEAR_MILITIA, URBAN_SPEAR_MILITIA , aragonIndex);
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
		LinesProcessor lines = descrStrat.Factions.getContent().getLines();

		int unitIndex = lines.findExpFirstRegexLine("^unit\\s+"+ oldUnitName + ".+" , factionStartIndex+1);

		if(newUnitName != null)
			lines.replaceLine(unitIndex , "unit\t\t" + newUnitName + "\t\t\t\texp " + experienceLevel + " armour 0 weapon_lvl 0");
		else
			lines.remove(unitIndex);
	}

	private void aragonRemoveSpearMilitiaAddUrbanMilitia() {
		edb.removeUnitRecruitment(SPEAR_MILITIA , ARAGON);

		edb.removeUnitRecruitment(URBAN_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(URBAN_SPEAR_MILITIA, ARAGON, Arrays.asList(PISA));

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

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b59df073-1de3-4358-b2bf-a88d781343e1");
}
