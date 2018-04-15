package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.util.List;

/** Buidling Level 'coordinations' */
public class BuildingLevel {
	public final String Name;
	public final SettlType SettlType;

	public String LevelName;

	private List<String> levels;
	private int levelIndex = 0;

	public void nextLevel() {
		if(levels == null) throw new PatcherLibBaseEx("Levels not set!");

		levelIndex++;
		LevelName = levels.get(levelIndex);
	}

	public BuildingLevel(String name, List<String> levels, tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType settlType) {
		Name = name;
		SettlType = settlType;
		this.levels = levels;

		this.levelIndex = 0;
		this.LevelName = levels.get(this.levelIndex);
	}

	public BuildingLevel(String name, String levelName, tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType settlType) {
		Name = name;
		LevelName = levelName;
		SettlType = settlType;
	}

	public BuildingLevel(String name, SettlType settlType) {
		Name = name;
		SettlType = settlType;
	}
}
