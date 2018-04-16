package tm.mtwModPatcher.sship.features.global.catholicIberiaReworked;

import lombok.val;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.StatMental;
import tm.mtwModPatcher.lib.data.exportDescrUnit.WeaponAttributes;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;
import tm.mtwModPatcher.lib.managers.FactionsDefs;

import java.util.List;

import static tm.mtwModPatcher.lib.common.entities.UnitReplenishRate.*;
import static tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef.MERCENARY_UNIT;
import static tm.mtwModPatcher.lib.data.exportDescrUnit.WeaponStat.SOUND_TYPE_SPEAR;
import static tm.mtwModPatcher.lib.managers.FactionsDefs.*;
import static tm.mtwModPatcher.sship.lib.Buildings.BarracksCastle;
import static tm.mtwModPatcher.sship.lib.Buildings.BarracksCastleLevels;
import static tm.mtwModPatcher.sship.lib.Units.ALMUGHAVARS;
import static tm.mtwModPatcher.sship.lib.Units.DISMOUNTED_SWORD_MAILED_KNIGHTS;

public class AlmughavarsReworked {
/* http://medieval2.heavengames.com/m2tw/mod_portal/tutorials/export_descr_units_file_guide/index.shtml  */

	public void execute() {
		unitStatFixes();

		// ## Add Almughavars recuitment ##
		barracksRecruitment();

		mercenaryPools();
	}

	private void unitStatFixes() {
		val unit = edu.loadUnit(ALMUGHAVARS);

		unit.addOwnershipAll(ARAGON);
		unit.addOwnershipAll(SPAIN);
		unit.addOwnershipAll(PORTUGAL);
		unit.addOwnershipAll(SLAVE);

		// # Adjust recuit cost
		val footKnights = edu.loadUnit(DISMOUNTED_SWORD_MAILED_KNIGHTS);

		unit.StatCost.Cost = footKnights.StatCost.Cost;
		unit.StatCost.Upkeep = unit.Soldier.NumberOfMen * 5;

		// Give spear
		unit.StatSec.SoundType = SOUND_TYPE_SPEAR;
		unit.StatSecAttr.add(WeaponAttributes.SPEAR);

		// Some better discipline
		unit.StatMental.Discipline = StatMental.DISCIPLINE_DISCIPLINED;

		// Mercenaries
		unit.addAttribute(MERCENARY_UNIT);
	}

	private void barracksRecruitment() {
		edb.removeUnitRecruitment(ALMUGHAVARS);
		val requireSuffix = " and hidden_resource aragon";

		val reqHigh = "factions { "+ FactionsDefs.toCsv(iberiaChristianFactions) +" }" + requireSuffix;
		val reqLow = "factions { "+ FactionsDefs.toCsv(otherChristianAndSlaveFactions) +" }" + requireSuffix;

		val building = new BuildingLevel(BarracksCastle, BarracksCastleLevels, SettlType.Castle);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R8,1, 0 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 0, R10,1, 0 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R7,2, 0 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 0, R9,1, 0 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 2, R6,3, 1 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R8,2, 0 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 2, R5,4, 2 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R7,3, 1 , reqLow);

		building.nextLevel();
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 3, R4,5, 2 , reqHigh);
		edb.insertRecruitmentBuildingCapabilities(building, ALMUGHAVARS, 1, R6,3, 1 , reqLow);
	}

	private void mercenaryPools() {
		val unitName = ALMUGHAVARS;
		val highReplenish = "	unit "+ unitName +"			exp 2 cost 550 replenish 0.067 - 0.10 max 1 initial 1 religions { catholic }";	// replenish 0.07 - 0.17
		val lowwReplenish = "	unit "+ unitName +"			exp 1 cost 550 replenish 0.05 - 0.067 max 1 initial 1 religions { catholic }";	// replenish 0.07 - 0.17

		descrMercenaries.addUnitRecruitmentLine("Pyrenaes" , highReplenish);
		descrMercenaries.addUnitRecruitmentLine("Spain" , lowwReplenish);
	}


	public AlmughavarsReworked(SharedContext sharedContext) {
		this.sharedContext = sharedContext;

		val ctx = sharedContext;

		edb = ctx.edb;
		edu = ctx.edu;
		descrMercenaries = ctx.descrMercenaries;

		iberiaChristianFactions = ctx.iberiaChristianFactions;
		otherChristianAndSlaveFactions = ctx.otherChristianAndSlaveFactions;
	}

	private final ExportDescrBuilding edb;
	private final ExportDescrUnitTyped edu;
	private final DescrMercenaries descrMercenaries;

	private final List<FactionInfo> iberiaChristianFactions;
	private final List<FactionInfo> otherChristianAndSlaveFactions;

	private SharedContext sharedContext;
}
