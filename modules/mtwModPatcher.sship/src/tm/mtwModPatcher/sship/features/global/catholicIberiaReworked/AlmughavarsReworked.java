package tm.mtwModPatcher.sship.features.global.catholicIberiaReworked;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Building;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.StatMental;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.data.exportDescrUnit.WeaponAttributes;
import tm.mtwModPatcher.lib.data.text.ExportUnits;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.sship.lib.Buildings;

import java.util.List;

import static tm.mtwModPatcher.lib.common.entities.UnitReplenishRate.*;
import static tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef.MERCENARY_UNIT;
import static tm.mtwModPatcher.lib.data.exportDescrUnit.WeaponStat.SOUND_TYPE_SPEAR;
import static tm.mtwModPatcher.lib.managers.FactionsDefs.SLAVE;
import static tm.mtwModPatcher.sship.lib.Units.*;

public class AlmughavarsReworked {
	private UnitDef footUnit;
	private UnitDef mountedUnit;
	/* http://medieval2.heavengames.com/m2tw/mod_portal/tutorials/export_descr_units_file_guide/index.shtml  */

	public void execute() {
		footUnit = edu.loadUnit(ALMUGHAVARS);
		mountedUnit = edu.loadUnit(CABALLEROS_VILLANOS);

		unitStatFixes();

		// ## Add Almughavars recuitment ##
		footBarracksRecruitment();
		mountedStableRecruitment();

		mercenaryPools();
	}

	private void unitStatFixes() {

		footUnit.addOwnershipAll(FactionsDefs.christianFactionInfos());
		footUnit.addOwnershipAll(SLAVE);

		// # Adjust recuit cost
		val footKnights = edu.loadUnit(DISMOUNTED_SWORD_MAILED_KNIGHTS);

		footUnit.StatCost.Cost = footKnights.StatCost.Cost;
		footUnit.upkeepByHugeMenMulti(2.25);

		footUnit.StatSec.SoundType = SOUND_TYPE_SPEAR;		// Give spear
		footUnit.StatSecAttr.add(WeaponAttributes.SPEAR);	// Give spear

		footUnit.StatMental.Discipline = StatMental.DISCIPLINE_DISCIPLINED;	// Some better discipline
		footUnit.addAttribute(MERCENARY_UNIT);	// Mercenaries

		// #### Mounted ######
		mountedUnit.addOwnershipAll(iberiaChristianFactions);
		mountedUnit.addOwnershipAll(otherChristianAndSlaveFactions);
		mountedUnit.addAttribute(UnitDef.MERCENARY_UNIT);
		mountedUnit.StatMental.Discipline = StatMental.DISCIPLINE_DISCIPLINED;
		mountedUnit.StatMental.Morale = footUnit.StatMental.Morale;

		eUnits.replaceName(mountedUnit, "Mounted Almughavars");
		val almughavarsDescr = eUnits.loadDescription(footUnit);
		eUnits.replaceDescription(mountedUnit, almughavarsDescr);

		mountedUnit.upkeepByHugeMenMulti(8);
	}

	private void footBarracksRecruitment() {
		edb.removeUnitRecruitment(ALMUGHAVARS);
		val requireSuffix = " and hidden_resource aragon";

		val reqHigh = "factions { "+ FactionsDefs.toCsv(iberiaChristianFactions) +" }" + requireSuffix;
		val reqLow = "factions { "+ FactionsDefs.toCsv(otherChristianAndSlaveFactions) +" }" + requireSuffix;

		val barracksTree = Buildings.BarracksCastleTree;
		Building b = barracksTree.first();
		edb.addRecuitment(b, ALMUGHAVARS, 1, R8,1, 0 , reqHigh);
		edb.addRecuitment(b, ALMUGHAVARS, 0, R10,1, 0 , reqLow);

		b=b.next();
		edb.addRecuitment(b, ALMUGHAVARS, 1, R7,2, 0 , reqHigh);
		edb.addRecuitment(b, ALMUGHAVARS, 0, R9,1, 0 , reqLow);

		b=b.next();
		edb.addRecuitment(b, ALMUGHAVARS, 2, R6,3, 1 , reqHigh);
		edb.addRecuitment(b, ALMUGHAVARS, 1, R8,2, 0 , reqLow);

		b=b.next();
		edb.addRecuitment(b, ALMUGHAVARS, 2, R5,4, 2 , reqHigh);
		edb.addRecuitment(b, ALMUGHAVARS, 1, R7,3, 1 , reqLow);

		b=b.next();
		edb.addRecuitment(b, ALMUGHAVARS, 3, R4,5, 2 , reqHigh);
		edb.addRecuitment(b, ALMUGHAVARS, 1, R6,3, 1 , reqLow);
	}

	private void mountedStableRecruitment() throws PatcherLibBaseEx {
		val cabVillanos = CABALLEROS_VILLANOS;
		edb.removeUnitRecruitment(mountedUnit);

		val requireSuffix = " and hidden_resource aragon";

		val reqHigh = "factions { "+ FactionsDefs.toCsv(iberiaChristianFactions) +" }" + requireSuffix;
		val reqLow = "factions { "+ FactionsDefs.toCsv(otherChristianAndSlaveFactions) +" }" + requireSuffix;

		val stableTree = Buildings.StableCastle;

		Building b = stableTree.first();
		edb.addRecuitment(b , cabVillanos , 1, R11, 1, 0, reqHigh);
		edb.addRecuitment(b , cabVillanos , 0, R13, 1, 0, reqLow);

		b=b.next();
		edb.addRecuitment(b , cabVillanos , 1, R10, 1, 0, reqHigh);
		edb.addRecuitment(b , cabVillanos , 0, R12, 1, 0, reqLow);

		b=b.next();
		edb.addRecuitment(b , cabVillanos , 1, R9, 2, 1, reqHigh);
		edb.addRecuitment(b , cabVillanos , 0, R11, 1, 0, reqLow);

		b=b.next();
		edb.addRecuitment(b , cabVillanos , 1, R8, 2, 1, reqHigh);
		edb.addRecuitment(b , cabVillanos , 1, R10, 2, 0, reqLow);

		b=b.next();
		edb.addRecuitment(b , cabVillanos , 2, R7, 3, 2, reqHigh);
		edb.addRecuitment(b , cabVillanos , 1, R9, 2, 1, reqLow);
	}

	private void mercenaryPools() {
		val footHighReplenish = 	"exp 2 cost 650 replenish 0.067 - 0.10 max 2 initial 2 religions { catholic }";	// replenish 0.07 - 0.17
		val footLowReplenish  = 	"exp 1 cost 650 replenish 0.05 - 0.067 max 1 initial 1 religions { catholic }";	// replenish 0.07 - 0.17

		val mountedHighReplenish =	"exp 2 cost 1050 replenish 0.05 - 0.09 max 1 initial 1 religions { catholic }";	// replenish 0.07 - 0.17
		val mountedLowReplenish  = 	"exp 1 cost 1050 replenish 0.03 - 0.05 max 1 initial 0 religions { catholic }";	// replenish 0.07 - 0.17

		descrMercenaries.addUnitRecruitmentLine(DescrMercenaries.PYRENAES , footUnit, footHighReplenish);
		descrMercenaries.addUnitRecruitmentLine(DescrMercenaries.SPAIN , footUnit, footLowReplenish);

		descrMercenaries.addUnitRecruitmentLine(DescrMercenaries.PYRENAES , mountedUnit, mountedHighReplenish);
		descrMercenaries.addUnitRecruitmentLine(DescrMercenaries.SPAIN , mountedUnit, mountedLowReplenish);
	}


	public AlmughavarsReworked(SharedContext sharedContext) {
		this.sharedContext = sharedContext;

		val ctx = sharedContext;

		edb = ctx.edb;
		edu = ctx.edu;
		eUnits = ctx.eUnits;
		descrMercenaries = ctx.descrMercenaries;

		iberiaChristianFactions = ctx.iberiaChristianFactions;
		otherChristianAndSlaveFactions = ctx.otherChristianAndSlaveFactions;
	}

	private final ExportDescrBuilding edb;
	private final ExportDescrUnitTyped edu;
	private ExportUnits eUnits;
	private final DescrMercenaries descrMercenaries;

	private final List<FactionInfo> iberiaChristianFactions;
	private final List<FactionInfo> otherChristianAndSlaveFactions;

	private SharedContext sharedContext;
}
