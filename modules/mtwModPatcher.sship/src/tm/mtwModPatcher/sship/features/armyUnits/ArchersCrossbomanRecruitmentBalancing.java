package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.entities.UnitReplenishRate;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**  */
public class ArchersCrossbomanRecruitmentBalancing extends Feature {

	@Getter @Setter
	private int replenishTurnsAddition = 4;

	@Override
	public void executeUpdates() throws Exception {
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		ArchersBalancing();

		CrossbowmenBalancing();
	}

	private void CrossbowmenBalancing() throws PatcherLibBaseEx {
		// ## Lower Recruitment Turns For Basic Crossbowmen ## : Crossbow Militia EE Crossbow Militia  ME Crossbowmen
		updateRecuitTurnsAndReplenishRates("Crossbow Militia", 2, 1, 2.0, -2.0);
		updateRecuitTurnsAndReplenishRates("EE Crossbow Militia", 2, 1, 2.0, -2.0);
		updateRecuitTurnsAndReplenishRates("ME Crossbowmen", 2, 1, 2.0, -2.0);

		// ## Add Crossbow Militia to City Barracks ## - from city_watch - as Urban Crossbow Militia is
		// town_watch town_guard city_watch militia_drill_square militia_barracks army_barracks royal_armoury
		String crossbowReguires = "factions { france, hre, denmark, spain, aragon, portugal, norway, jerusalem, } and not event_counter lateran_council3 1";
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "town_guard", "city", "Crossbow Militia", 1, UnitReplenishRate.R15, 1, 0, crossbowReguires);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "city_watch", "city", "Crossbow Militia", 1, UnitReplenishRate.R8, 1, 0, crossbowReguires);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "militia_drill_square", "city", "Crossbow Militia", 1, UnitReplenishRate.R7, 2, 0, crossbowReguires);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "militia_barracks", "city", "Crossbow Militia", 1, UnitReplenishRate.R6, 2, 0, crossbowReguires);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "army_barracks", "city", "Crossbow Militia", 1, UnitReplenishRate.R5, 2, 0, crossbowReguires);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "royal_armoury", "city", "Crossbow Militia", 1, UnitReplenishRate.R4, 3, 0, crossbowReguires);

		exportDescrBuilding.insertRecruitmentBuildingCapabilities("guild_merchants_guild", "merchants_guild", "city", "Crossbow Militia", 1, UnitReplenishRate.R15, 1, 0, crossbowReguires);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("guild_merchants_guild", "m_merchants_guild", "city", "Crossbow Militia", 1, UnitReplenishRate.R13, 1, 0, crossbowReguires);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("guild_merchants_guild", "gm_merchants_guild", "city", "Crossbow Militia", 1, UnitReplenishRate.R12, 1, 0, crossbowReguires);

		// ## Crossbow Militia & EE Crossbow Militia - bigger recruit_priority_offset
		exportDescrUnit.loadUnit("Crossbow Militia").RecruitPriorityOffset += 10;		// 5
		exportDescrUnit.loadUnit("EE Crossbow Militia").RecruitPriorityOffset += 10;	// 5

		// ## ME Crossbowmen to Castle missiles practice_range % City barracks militia_drill_square
		String requiresCrossbowIslamHomelands = "factions { egypt, milan, moors, kwarezm, } and hidden_resource moors or hidden_resource kwarezm or hidden_resource egypt or hidden_resource milan";
		String requiresCrossbowIslamNotHomelands = "factions { egypt, milan, moors, kwarezm, } and not hidden_resource moors and not hidden_resource kwarezm and not hidden_resource egypt and not hidden_resource milan";

		// ## ME Crossbowmen to Castle missiles practice_range
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("missiles", "practice_range", "castle", "ME Crossbowmen", 1, UnitReplenishRate.R6, 1, 0, requiresCrossbowIslamHomelands);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("missiles", "practice_range", "castle", "ME Crossbowmen", 1, UnitReplenishRate.R8, 1, 0, requiresCrossbowIslamNotHomelands);

		// ## ME Crossbowmen to City barracks militia_drill_square militia_barracks army_barracks royal_armoury
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "city_watch", "city", "ME Crossbowmen", 1, UnitReplenishRate.R15, 1, 0, requiresCrossbowIslamHomelands);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "militia_drill_square", "city", "ME Crossbowmen", 1, UnitReplenishRate.R13, 1, 0, requiresCrossbowIslamHomelands);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "militia_barracks", "city", "ME Crossbowmen", 1, UnitReplenishRate.R12, 1, 0, requiresCrossbowIslamHomelands);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "army_barracks", "city", "ME Crossbowmen", 1, UnitReplenishRate.R10, 1, 0, requiresCrossbowIslamHomelands);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("barracks", "royal_armoury", "city", "ME Crossbowmen", 1, UnitReplenishRate.R9, 1, 0, requiresCrossbowIslamHomelands);

		exportDescrBuilding.insertRecruitmentBuildingCapabilities("guild_merchants_guild", "merchants_guild", "city", "ME Crossbowmen", 1, UnitReplenishRate.R15, 1, 0, requiresCrossbowIslamHomelands);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("guild_merchants_guild", "m_merchants_guild", "city", "ME Crossbowmen", 1, UnitReplenishRate.R14, 1, 0, requiresCrossbowIslamHomelands);
		exportDescrBuilding.insertRecruitmentBuildingCapabilities("guild_merchants_guild", "gm_merchants_guild", "city", "ME Crossbowmen", 1, UnitReplenishRate.R13, 1, 0, requiresCrossbowIslamHomelands);

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
		unit = exportDescrUnit.loadUnit(unitName);
		if (unit.StatCost.RecruitTurns >= recruitTurnsMinEqual) unit.StatCost.RecruitTurns -= recruitTurnsBonus;

		exportDescrBuilding.updateUnitReplenishRates(unitName, replenishRateMin, replenishRateAddition);

	}

	protected void ArchersBalancing() throws PatcherLibBaseEx {

		val archers = exportDescrUnit.getUnits().stream().filter(u -> u.StatPri.IsParsed && u.StatPri.IsMissleUnit() && u.StatPri.IsArcherUnit()).collect(Collectors.toList());

		// ## Some Archers - bigger recruit_priority_offset
		exportDescrUnit.loadUnit("Prussian Archers").RecruitPriorityOffset += 15;			// 10
		exportDescrUnit.loadUnit("Berber Archers").RecruitPriorityOffset += 10;			// 5
		exportDescrUnit.loadUnit("Andalusian Archers").RecruitPriorityOffset += 15;		// 10
		exportDescrUnit.loadUnit("Dismounted Fari Archers").RecruitPriorityOffset += 10;	// 5

		// ## Archers recruit turns >= 2 for westerns foots : -> +1 turn -> archers become 'Elite'.
		String christianOwnership = FactionsDefs.christianFactionsCsv();
		List<UnitDef> archersWestern = archers.stream().filter(u -> FactionsDefs.isAnyFirstCsvFactorsInSecondCsv(u.Ownership, christianOwnership) && u.isCategoryInfantry() && u.StatCost.RecruitTurns >= 2).collect(Collectors.toList());

		for (UnitDef unit : archersWestern) {
			unit.StatCost.RecruitTurns++;
			exportDescrBuilding.updateUnitReplenishRates(unit.Name, 0 , replenishTurnsAddition);
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

	protected ExportDescrUnitTyped exportDescrUnit;
	protected ExportDescrBuilding exportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.randomUUID();

	public ArchersCrossbomanRecruitmentBalancing() {
		super("Archers & Crossbowmen availability");

		addCategory("Campaign");
		addCategory("Units");
		addCategory("Missile");

		setDescriptionShort("Archers & Crossbowmen recruitment availability tweaks");
		setDescriptionUrl("http://tmsship.wikidot.com/archers-crossbowmen-availability");
	}
}
