package tm.mtwModPatcher.sship.features.agentsCharacters.merchantsNerfed;

import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;

/**
 * Created by tomek on 15.10.2017.
 */
public class BuildingLimit {

	public String DisplayName;

	public BuildingType BuildingType = null;

	public String Building = null;

	public String Level = null;

	public SettlType SettlType = null;

	public String Requires = null;

	public int Limit = 0;

	public BuildingLimit() {
	}

	public BuildingLimit(String displayName, BuildingType buildingType, int limit) {
		DisplayName = displayName;
		BuildingType = buildingType;
		Limit = limit;
	}
	public BuildingLimit(String displayName, BuildingType buildingType, String requires, int limit) {
		DisplayName = displayName;
		BuildingType = buildingType;
		Requires = requires;
		Limit = limit;
	}

	public BuildingLimit(String displayName, String building, String level, int limit) {
		DisplayName = displayName;
		Building = building;
		Level = level;
		Limit = limit;
	}
	public BuildingLimit(String displayName, String building, String level, SettlType settlType, int limit) {
		DisplayName = displayName;
		Building = building;
		Level = level;
		SettlType = settlType;
		Limit = limit;
	}
	public BuildingLimit(String displayName, String building, String level, SettlType settlType, String requires, int limit) {
		DisplayName = displayName;
		Building = building;
		Level = level;
		SettlType = settlType;
		Requires = requires;
		Limit = limit;
	}
}
