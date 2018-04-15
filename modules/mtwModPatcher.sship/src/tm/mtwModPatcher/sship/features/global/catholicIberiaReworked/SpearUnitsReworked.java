package tm.mtwModPatcher.sship.features.global.catholicIberiaReworked;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.unitModels.BattleModels;

import java.util.Arrays;
import java.util.List;

import static tm.mtwModPatcher.lib.managers.FactionsDefs.*;
import static tm.mtwModPatcher.sship.lib.Buildings.*;
import static tm.mtwModPatcher.sship.lib.Units.*;

public class SpearUnitsReworked {

	public void execute() {
		aragonAddUrbanAndPaviseMilitiaAsPisa();
		spearmenForIberians();
		armoredSpearmen();
		aragonRemoveSpearJavelinRecruitment();
	}

	private void aragonAddUrbanAndPaviseMilitiaAsPisa() {

		edb.removeUnitRecruitment(URBAN_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(URBAN_SPEAR_MILITIA, ARAGON, BarracksCity, PISA);

		edu.addOwnershipAll(NE_URBAN_MILITIA, ARAGON.symbol);
		edb.removeUnitRecruitment(NE_URBAN_MILITIA , ARAGON);
		edb.addToUnitRecruitment(NE_URBAN_MILITIA, ARAGON, BarracksCity, PISA);

		// TODO: model for aragon for NE URBAN MILITIA ?

		edu.addOwnershipAll(PAVISE_SPEAR_MILITIA, ARAGON.symbol);
		edb.removeUnitRecruitment(PAVISE_SPEAR_MILITIA , ARAGON);
		edb.addToUnitRecruitment(PAVISE_SPEAR_MILITIA, ARAGON, BarracksCity, PISA);

		// TODO: model for aragon for PAVISE_SPEAR_MILITIA missing
	}
	private void spearmenForIberians() {
		// ## SERGEANT_SPEARMEN recuitment ##
		val unit = edu.loadUnit(SERGEANT_SPEARMEN);

		unit.addOwnershipAll(ARAGON);
		unit.addOwnershipAll(SPAIN);
		unit.addOwnershipAll(PORTUGAL);

		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , ARAGON);
		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , SPAIN);
		edb.removeUnitRecruitment(SERGEANT_SPEARMEN , PORTUGAL);

		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, ARAGON, BarracksCastle, allSpainHiddenResList, FRANCE);
		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, SPAIN, BarracksCastle, FRANCE);
		edb.addToUnitRecruitment(SERGEANT_SPEARMEN, PORTUGAL, BarracksCastle, FRANCE);

		// add Spearmen recruitment for 1st level barracks only for Aragon and in aragon, replenish 50% as in 2nd level.
		edb.insertRecruitmentBuildingCapabilities(BarracksCastle, BarracksCastleLevels.get(0), CastleType, SERGEANT_SPEARMEN,
				1, 0.08,1, 0 , "factions { aragon, } and not event_counter FULL_PLATE_ARMOR 1 and hidden_resource aragon");

		// battleModels : Spearmen
		val models = Arrays.asList("sergeant_spearmen" , "sergeant_spearmen_ug1");
		battleModels.copyModelBlocksData(models, iberiaChristianFactions, FRANCE);

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

		// dodac battleModels : Armored Spearmen
		val models = Arrays.asList("armored_sergeants" , "armored_sergeants_ug1");
		battleModels.copyModelBlocksData(models, iberiaChristianFactions, FRANCE);
	}

	private void aragonRemoveSpearJavelinRecruitment() {
		edb.removeUnitRecruitment(JAVELINMEN , ARAGON);
		edb.removeUnitRecruitment(LUSITANIAN_JAVELINMEN , ARAGON);
		edb.removeUnitRecruitment(PEASANTS , ARAGON);
		edb.removeUnitRecruitment(SPEAR_MILITIA , ARAGON);
	}

	private void addSergeantSpearmanModels(List<FactionInfo> factions, String copyFromFaction) throws PatcherLibBaseEx {

		for(val faction : factions) {
			battleModels.copyModelBlocksData("sergeant_spearmen", faction.symbol , copyFromFaction );
			battleModels.copyModelBlocksData("sergeant_spearmen_ug1", faction.symbol , copyFromFaction );
		}
	}
	private void addArmoredSpearmanModels(List<FactionInfo> factions, String copyFromFaction) throws PatcherLibBaseEx {

		for(val faction : factions) {
			battleModels.copyModelBlocksData("armored_sergeants", faction.symbol , copyFromFaction );
			battleModels.copyModelBlocksData("armored_sergeants_ug1", faction.symbol , copyFromFaction );
		}
	}

	public SpearUnitsReworked(SharedContext sharedContext) {
		this.sharedContext = sharedContext;

		val ctx = sharedContext;

		edb = ctx.edb;
		edu = ctx.edu;
		battleModels = ctx.battleModels;

		iberiaChristianFactions = ctx.iberiaChristianFactions;
		otherChristianAndSlaveFactions = ctx.otherChristianAndSlaveFactions;
		allSpainHiddenResList = ctx.allSpainHiddenResList;
	}

	private final ExportDescrBuilding edb;
	private final ExportDescrUnitTyped edu;
	private final BattleModels battleModels;

	private final List<FactionInfo> iberiaChristianFactions;
	private final List<FactionInfo> otherChristianAndSlaveFactions;
	private final List<String> allSpainHiddenResList;

	private SharedContext sharedContext;
}
