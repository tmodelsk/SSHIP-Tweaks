package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.sship.features.global.catholicIberiaReworked.CatholicIberiaReworked;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static tm.mtwModPatcher.lib.common.entities.UnitReplenishRate.*;
import static tm.mtwModPatcher.sship.lib.Buildings.*;
import static tm.mtwModPatcher.sship.lib.Units.*;

/**  */
public class ArchersCrossbomanRecruitmentBalancing extends Feature {

	@Override
	public void setParamsCustomValues() {
		replenishTurnsAddition = 4;
	}

	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);

		archersBalancing();

		crossbowmenBalancing();
	}

	private void crossbowmenBalancing() throws PatcherLibBaseEx {

		// ## Lower Recruitment Turns For Basic Crossbowmen ## : Crossbow Militia EE Crossbow Militia  ME Crossbowmen
		updateRecuitTurnsAndReplenishRates(CROSSBOW_MILITIA, 2, 1, 2.0, -2.0);
		updateRecuitTurnsAndReplenishRates(EE_CROSSBOW_MILITIA, 2, 1, 2.0, -2.0);
		updateRecuitTurnsAndReplenishRates(ME_CROSSBOWMEN, 2, 1, 2.0, -2.0);

		// ## Add Crossbow Militia to City Barracks ## - from city_watch - as Urban Crossbow Militia is
		String crossbowMilitiaFactionsCsv = "france, hre, denmark, spain, portugal, norway, jerusalem,";
		if(featuresContainer.isNotFeatureEnabled(CatholicIberiaReworked.Id))
			crossbowMilitiaFactionsCsv += "aragon,";

		String crossbowReguires = "factions { "+ crossbowMilitiaFactionsCsv +" } and not event_counter lateran_council3 1";
		edb.addRecuitment(BarracksCity, "town_guard", CityType, CROSSBOW_MILITIA, 1, R15, 1, 0, crossbowReguires);
		edb.addRecuitment(BarracksCity, "city_watch", CityType, CROSSBOW_MILITIA, 1, R8, 1, 0, crossbowReguires);
		edb.addRecuitment(BarracksCity, "militia_drill_square", CityType, CROSSBOW_MILITIA, 1, R7, 2, 0, crossbowReguires);
		edb.addRecuitment(BarracksCity, "militia_barracks", CityType, CROSSBOW_MILITIA, 1, R6, 2, 0, crossbowReguires);
		edb.addRecuitment(BarracksCity, "army_barracks", CityType, CROSSBOW_MILITIA, 1, R5, 2, 0, crossbowReguires);
		edb.addRecuitment(BarracksCity, "royal_armoury", CityType, CROSSBOW_MILITIA, 1, R4, 3, 0, crossbowReguires);

		edb.addRecuitment(MerchantGuild, "merchants_guild", CityType, CROSSBOW_MILITIA, 1, R15, 1, 0, crossbowReguires);
		edb.addRecuitment(MerchantGuild, "m_merchants_guild", CityType, CROSSBOW_MILITIA, 1, R13, 1, 0, crossbowReguires);
		edb.addRecuitment(MerchantGuild, "gm_merchants_guild", CityType, CROSSBOW_MILITIA, 1, R12, 1, 0, crossbowReguires);

		// ## Crossbow Militia & EE Crossbow Militia - bigger recruit_priority_offset
		edu.loadUnit(CROSSBOW_MILITIA).RecruitPriorityOffset += 10;		// 5
		edu.loadUnit(EE_CROSSBOW_MILITIA).RecruitPriorityOffset += 10;	// 5

		// ## ME Crossbowmen to Castle missiles practice_range % City barracks militia_drill_square
		String requiresCrossbowIslamHomelands = "factions { egypt, milan, moors, kwarezm, } and hidden_resource moors or hidden_resource kwarezm or hidden_resource egypt or hidden_resource milan";
		String requiresCrossbowIslamNotHomelands = "factions { egypt, milan, moors, kwarezm, } and not hidden_resource moors and not hidden_resource kwarezm and not hidden_resource egypt and not hidden_resource milan";

		// ## ME Crossbowmen to Castle missiles practice_range
		edb.addRecuitment(MissileCastle, "practice_range", CastleType, ME_CROSSBOWMEN, 1, R6, 1, 0, requiresCrossbowIslamHomelands);
		edb.addRecuitment(MissileCastle, "practice_range", CastleType, ME_CROSSBOWMEN, 1, R8, 1, 0, requiresCrossbowIslamNotHomelands);

		// ## ME Crossbowmen to City barracks militia_drill_square militia_barracks army_barracks royal_armoury
		edb.addRecuitment(BarracksCity, "city_watch", CityType, ME_CROSSBOWMEN, 1, R15, 1, 0, requiresCrossbowIslamHomelands);
		edb.addRecuitment(BarracksCity, "militia_drill_square", CityType, ME_CROSSBOWMEN, 1, R13, 1, 0, requiresCrossbowIslamHomelands);
		edb.addRecuitment(BarracksCity, "militia_barracks", CityType, ME_CROSSBOWMEN, 1, R12, 1, 0, requiresCrossbowIslamHomelands);
		edb.addRecuitment(BarracksCity, "army_barracks", CityType, ME_CROSSBOWMEN, 1, R10, 1, 0, requiresCrossbowIslamHomelands);
		edb.addRecuitment(BarracksCity, "royal_armoury", CityType, ME_CROSSBOWMEN, 1, R9, 1, 0, requiresCrossbowIslamHomelands);

		edb.addRecuitment(MerchantGuild, "merchants_guild", CityType, ME_CROSSBOWMEN, 1, R15, 1, 0, requiresCrossbowIslamHomelands);
		edb.addRecuitment(MerchantGuild, "m_merchants_guild", CityType, ME_CROSSBOWMEN, 1, R14, 1, 0, requiresCrossbowIslamHomelands);
		edb.addRecuitment(MerchantGuild, "gm_merchants_guild", CityType, ME_CROSSBOWMEN, 1, R13, 1, 0, requiresCrossbowIslamHomelands);

		// region  // Crossbows by RecruitTurns - Debug Mode
		//		List<UnitDef> crossbow1 = crossbowmens.stream().filter( c -> c.StatCost.RecruitTurns == 1).collect(Collectors.toList());
		//		List<UnitDef> crossbow2 = crossbowmens.stream().filter( c -> c.StatCost.RecruitTurns == 2).collect(Collectors.toList());
		//		List<UnitDef> crossbow3 = crossbowmens.stream().filter( c -> c.StatCost.RecruitTurns == 3).collect(Collectors.toList());
		//		List<UnitDef> crossbow4 = crossbowmens.stream().filter( c -> c.StatCost.RecruitTurns == 4).collect(Collectors.toList());
		//		List<UnitDef> crossbow5 = crossbowmens.stream().filter( c -> c.StatCost.RecruitTurns == 5).collect(Collectors.toList());
		//		List<UnitDef> crossbow6 = crossbowmens.stream().filter( c -> c.StatCost.RecruitTurns == 6).collect(Collectors.toList());
		// endregion
	}

	protected void updateRecuitTurnsAndReplenishRates(String unitName, int recruitTurnsMinEqual, int recruitTurnsBonus, double replenishRateMin, double replenishRateAddition) throws PatcherLibBaseEx {
		UnitDef unit;
		unit = edu.loadUnit(unitName);
		if (unit.StatCost.RecruitTurns >= recruitTurnsMinEqual) unit.StatCost.RecruitTurns -= recruitTurnsBonus;

		edb.updateUnitReplenishRates(unitName, replenishRateMin, replenishRateAddition);

	}

	protected void archersBalancing() throws PatcherLibBaseEx {

		val archers = edu.getUnits().stream().filter(u -> u.StatPri.IsParsed && u.StatPri.IsMissleUnit() && u.StatPri.IsArcherUnit()).collect(Collectors.toList());

		// ## Some Archers - bigger recruit_priority_offset
		edu.loadUnit(PRUSSIAN_ARCHERS).RecruitPriorityOffset += 20;			// 10
		edu.loadUnit("Berber Archers").RecruitPriorityOffset += 10;			// 5
		edu.loadUnit("Andalusian Archers").RecruitPriorityOffset += 15;		// 10
		edu.loadUnit("Dismounted Fari Archers").RecruitPriorityOffset += 10;	// 5

		// ## Archers recruit turns >= 2 for westerns foots : -> +1 turn -> archers become 'Elite'.
		String christianOwnership = FactionsDefs.christianFactionsCsv();
		List<UnitDef> archersWestern = archers.stream().filter(u -> FactionsDefs.isAnyFirstCsvFactorsInSecondCsv(u.Ownership, christianOwnership) && u.isCategoryInfantry() && u.StatCost.RecruitTurns >= 2).collect(Collectors.toList());

		for (UnitDef unit : archersWestern) {
			unit.StatCost.RecruitTurns++;
			edb.updateUnitReplenishRates(unit.Name, 0 , replenishTurnsAddition);
		}
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		parIds.add(new ParamIdInteger("ReplenishTurnsAddition","Replenish Turns Addition",
					feature -> ((ArchersCrossbomanRecruitmentBalancing)feature).getReplenishTurnsAddition(),
					(feature, value) ->  ((ArchersCrossbomanRecruitmentBalancing)feature).setReplenishTurnsAddition(value)  ));

		return parIds;
	}

	@Getter @Setter private int replenishTurnsAddition;

	protected ExportDescrUnitTyped edu;
	protected ExportDescrBuilding edb;

	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.fromString("dbb8a719-22cc-4257-b4be-46dbbab6803b");

	public ArchersCrossbomanRecruitmentBalancing() {
		super("Archers & Crossbowmen availability");

		addCategory("Campaign");
		addCategory("Units");
		addCategory("Missile");

		setDescriptionShort("Archers & Crossbowmen recruitment availability tweaks");
		setDescriptionUrl("http://tmsship.wikidot.com/archers-crossbowmen-availability");
	}
}
