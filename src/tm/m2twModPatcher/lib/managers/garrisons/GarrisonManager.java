package tm.m2twModPatcher.lib.managers.garrisons;

import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.entities.SettlementInfo;
import tm.m2twModPatcher.lib.common.entities.SettlementLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomek on 2016-11-13.
 */
public class GarrisonManager {

	public GarrisonManager(GarrisonMetaManager garrisonMetaManager) throws PatcherLibBaseEx {
		_GarrisonMetaManager = garrisonMetaManager;
	}

	public List<UnitGarrisonInfo> getUnits(SettlementInfo settlement, String factionName) throws Exception {
		List<UnitGarrisonInfo> units;

		List<UnitMetaDef> metaList = getMetaListBySettlementLevel(settlement.Level);

		units = _GarrisonMetaManager.getFactionImplementations(metaList, factionName, settlement);

		return units;
	}



	public List<UnitMetaDef> getMetaListBySettlementLevel(SettlementLevel level) throws PatcherLibBaseEx {
		List<UnitMetaDef> metaList = new ArrayList<>();

		switch (level) {

			case L1_Village:
				metaList.add(new UnitMetaDef(UnitType.LevyInfantry));
				break;
			case L2_Town:
				metaList.add(new UnitMetaDef(UnitType.LevyInfantry));
				metaList.add(new UnitMetaDef(UnitType.LevyMissle));
				break;
			case L3_LargeTown:
				metaList.add(new UnitMetaDef(UnitType.LevyInfantry));
				metaList.add(new UnitMetaDef(UnitType.Missle));
				metaList.add(new UnitMetaDef(UnitType.LevyInfantry));
				break;
			case L4_City:
				metaList.add(new UnitMetaDef(UnitType.LevyInfantry , 1));
				metaList.add(new UnitMetaDef(UnitType.Missle));
				metaList.add(new UnitMetaDef(UnitType.LevyInfantry , 1));
				metaList.add(new UnitMetaDef(UnitType.Spearmen));
				break;
			case L5_LargeCity:
				metaList.add(new UnitMetaDef(UnitType.Spearmen));
				metaList.add(new UnitMetaDef(UnitType.Spearmen));
				metaList.add(new UnitMetaDef(UnitType.Missle));
				metaList.add(new UnitMetaDef(UnitType.Missle));
				metaList.add(new UnitMetaDef(UnitType.HeavyInfantry));
				break;
			case L6_HugeCity:
				metaList.add(new UnitMetaDef(UnitType.Spearmen, 1));
				metaList.add(new UnitMetaDef(UnitType.Spearmen, 1));
				metaList.add(new UnitMetaDef(UnitType.Missle, 1));
				metaList.add(new UnitMetaDef(UnitType.Missle, 1));
				metaList.add(new UnitMetaDef(UnitType.Spearmen, 1));
				metaList.add(new UnitMetaDef(UnitType.HeavyInfantry));
				break;
			default:
				throw new PatcherLibBaseEx("Level "+level+" is not supported!");
		}

		return metaList;
	}

	private GarrisonMetaManager _GarrisonMetaManager;
}
