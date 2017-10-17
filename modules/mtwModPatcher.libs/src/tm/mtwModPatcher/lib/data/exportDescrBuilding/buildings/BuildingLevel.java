package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

/** Buidling Level 'coordinations' */
public class BuildingLevel {
	public String Name;
	public String LevelName;
	public SettlType SettlType = null;

	public BuildingLevel(String name, String levelName, tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType settlType) {
		Name = name;
		LevelName = levelName;
		SettlType = settlType;
	}

	public BuildingLevel(String name, String levelName) {
		Name = name;
		LevelName = levelName;
		SettlType = null;
	}

	public BuildingLevel() {
	}
}
