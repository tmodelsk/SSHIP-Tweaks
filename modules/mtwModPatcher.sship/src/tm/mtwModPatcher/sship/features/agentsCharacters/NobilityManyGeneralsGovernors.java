package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.common.entities.UnitReplenishRate;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings;
import tm.mtwModPatcher.sship.features.global.PopulationResourcesLimited;

import java.util.UUID;

/** Adds very limited recruitment of bodyguard (generals) to every settlement (walls)*/
public class NobilityManyGeneralsGovernors extends Feature {

	@Override
	public void setParamsCustomValues() {
		cityReplenishTurnsStarting = 52; //44;
		cityReplenishTurnsBonus = 2;
		castleReplenishTurnsStarting = 48; //40;
		castleReplenishTurnsBonus = 2;
		aiReplenishTurnsMulti = 1.25;
	}

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);

		insertUnit("NE Bodyguard", "factions { england, scotland, france, hre, denmark, poland, hungary, slave, jerusalem,  } and not event_counter GOTHIC_ARMOR 1");
		insertUnit("NE Late Bodyguard", "factions { england, scotland, france, hre, denmark, poland, hungary, slave, jerusalem,  } and event_counter GOTHIC_ARMOR 1");

		insertUnit("NE Bodyguard", "factions { norway, slave, } and not hidden_resource norway and not hidden_resource denmark and not event_counter GOTHIC_ARMOR 1");
		insertUnit("NE Late Bodyguard", "factions { norway, slave, } and not hidden_resource norway and not hidden_resource denmark and event_counter GOTHIC_ARMOR 1");
		insertUnit("Royal Hirdsmen", "factions { norway, slave, } and hidden_resource norway or hidden_resource denmark");

		insertUnit("SE Bodyguard", "factions { spain, aragon, portugal, pisa, venice, papal_states, sicily, slave, } and not event_counter GOTHIC_ARMOR 1");
		insertUnit("SE Late Bodyguard", "factions { spain, aragon, portugal, pisa, venice, papal_states, sicily, } and event_counter GOTHIC_ARMOR 1");

		insertUnit("EE Bodyguard", "factions { russia, kievan_rus, teutonic_order, slave, } and not event_counter TRANSITIONAL_ARMOR 1");
		insertUnit("EE Late Bodyguard", "factions { russia, kievan_rus, teutonic_order, slave, } and event_counter TRANSITIONAL_ARMOR 1");

		insertUnit("Lith Bodyguard", "factions { lithuania, }");

		insertUnit("ME Bodyguard", "factions { moors, egypt, milan, turks, rum, kwarezm, } and not event_counter TRANSITIONAL_ARMOR 1");
		insertUnit("ME Late Bodyguard", "factions { moors, egypt, milan, turks, rum, kwarezm, } and event_counter TRANSITIONAL_ARMOR 1");

		insertUnit("bodyguard georgia", "factions { timurids, }");

		insertUnit("Greek Bodyguard", "factions { byzantium, }");

		insertUnit("Cuman Bodyguard", "factions { cumans, }");

//		insertUnit("", "");
//		insertUnit("", "");
	}

	private void insertUnit(String unitName, String requiresStr) {

		insertUnitCore(unitName, requiresStr + " and event_counter is_the_player 1", 1.0);
		insertUnitCore(unitName, requiresStr + " and not event_counter is_the_player 1", aiReplenishTurnsMulti);

	}

	private void insertUnitCore(String unitName, String requiresStr, double replenishTurnsMultiPar) {

		// ### CITY ###
		String buildingName = Buildings.WallsCity.Name;
		String type = Buildings.CityType;
		val wallsCity = Buildings.WallsCity;

		int starting = 1, max = 1, bonus = 0, replenishTurns, replBonus;

		double replenishTmpMulti = 1.0;
		if (featuresContainer.isFeatureEnabled(PopulationResourcesLimited.Id)) {
			val populationResourcesLimited = (PopulationResourcesLimited) featuresContainer.getEnabled(PopulationResourcesLimited.Id);

			replenishTmpMulti = 1.0 / populationResourcesLimited.getReplenishRateMult();
			starting = 0;
		}
		replenishTmpMulti *= replenishTurnsMultiPar;

		replenishTurns = cityReplenishTurnsStarting;
		replBonus = cityReplenishTurnsBonus;

		edb.addRecuitment(buildingName, wallsCity.L1_WoodenPalisade, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= replBonus;
		edb.addRecuitment(buildingName, wallsCity.L2_WoodenWall, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= replBonus;
		max++;
		edb.addRecuitment(buildingName, wallsCity.L3_StoneWall, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= replBonus;
		max++;
		//starting++;
		edb.addRecuitment(buildingName, wallsCity.L4_LargeStoneWall, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= (2 * replBonus);
		max++;
		starting++;
		edb.addRecuitment(buildingName, wallsCity.L5_HugeStoneWall, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		// ##### CASTLE #####
		buildingName = Buildings.WallsCastle.Name;
		type = Buildings.CastleType;
		val wallsCastle = Buildings.WallsCastle;

		if (featuresContainer.isFeatureEnabled(PopulationResourcesLimited.Id)) starting = 0;
		else starting = 1;
		max = 1;
		bonus = 0;
		replenishTurns = castleReplenishTurnsStarting;
		replBonus = castleReplenishTurnsBonus;

		edb.addRecuitment(buildingName, wallsCastle.L1_MotteAndBailey, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= replBonus;
		edb.addRecuitment(buildingName, wallsCastle.L2_WoodenCastle, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= replBonus;
		max++;
		edb.addRecuitment(buildingName, wallsCastle.L3_Castle, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= replBonus;
		max++;
		starting++;
		edb.addRecuitment(buildingName, wallsCastle.L4_Fortress, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);

		replenishTurns -= (2 * replBonus);
		max++;
		//starting++;
		edb.addRecuitment(buildingName, wallsCastle.L5_Citadel, type,
				unitName, starting, UnitReplenishRate.getRate(replenishTurns) * replenishTmpMulti, max, bonus, requiresStr);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val params = new ArrayUniqueList<ParamId>();

		params.add(new ParamIdInteger("CityReplenishTurns", "City Replenish Turns",
				feature -> ((NobilityManyGeneralsGovernors) feature).getCityReplenishTurnsStarting(),
				(feature, value) -> ((NobilityManyGeneralsGovernors) feature).setCityReplenishTurnsStarting(value)));

		params.add(new ParamIdInteger("CityReplenishBonus", "City Replenish Bonus",
				feature -> ((NobilityManyGeneralsGovernors) feature).getCityReplenishTurnsBonus(),
				(feature, value) -> ((NobilityManyGeneralsGovernors) feature).setCityReplenishTurnsBonus(value)));

		params.add(new ParamIdInteger("CastleReplenishTurns", "Castle Replenish Turns",
				feature -> ((NobilityManyGeneralsGovernors) feature).getCastleReplenishTurnsStarting(),
				(feature, value) -> ((NobilityManyGeneralsGovernors) feature).setCastleReplenishTurnsStarting(value)));

		params.add(new ParamIdInteger("CastleReplenishBonus", "Castle Replenish Bonus",
				feature -> ((NobilityManyGeneralsGovernors) feature).getCastleReplenishTurnsBonus(),
				(feature, value) -> ((NobilityManyGeneralsGovernors) feature).setCastleReplenishTurnsBonus(value)));

		params.add(new ParamIdDouble("AiReplenishTurnsMulti", "AI Replenish Turns Multiplier",
				feature -> ((NobilityManyGeneralsGovernors) feature).getAiReplenishTurnsMulti(),
				(feature, value) -> ((NobilityManyGeneralsGovernors) feature).setAiReplenishTurnsMulti(value)));

		return params;
	}

	@Getter @Setter private int cityReplenishTurnsStarting;
	@Getter @Setter private int cityReplenishTurnsBonus;
	@Getter @Setter private int castleReplenishTurnsStarting;
	@Getter @Setter private int castleReplenishTurnsBonus;
	@Getter @Setter private double aiReplenishTurnsMulti;

	private ExportDescrBuilding edb;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("c6207dbd-5b38-4bad-8211-d69583fbeee8");

	public NobilityManyGeneralsGovernors() {

		setName("Nobility Many Generals & Governors");
		setDescriptionShort("Nobility Recruiment is much easier - AI & Player can have many Generals & Governors");
		setDescriptionUrl("http://tmsship.wikidot.com/nobility-many-generals-governors");
	}
}
