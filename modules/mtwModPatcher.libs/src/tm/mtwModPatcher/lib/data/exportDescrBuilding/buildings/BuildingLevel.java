package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Buidling Level 'coordinations' */
public class BuildingLevel {
	public final String Name;
	public final SettlType SettlType;

	public String LevelName;

	private List<String> levelNames;
	private int levelIndex = 0;

	public void nextLevel() {
		if(levelNames == null) throw new PatcherLibBaseEx("Levels not set!");

		levelIndex++;
		LevelName = levelNames.get(levelIndex);
	}
	public BuildingLevel createNextLevel() {
		if(levelNames == null) throw new PatcherLibBaseEx("Levels not set!");

		val next = new BuildingLevel(Name, levelNames, this.SettlType);
		next.setLevelIndex(levelIndex+1);

		return next;
	}

	public BuildingLevel level(int level) {
		if(levelNames == null) throw new PatcherLibBaseEx("Levels not set!");

		val next = new BuildingLevel(Name, levelNames, this.SettlType);
		next.setLevelIndex(level-1);

		return next;
	}

	public void setLevelIndex(int level) {
		levelIndex = level;
		LevelName = levelNames.get(levelIndex);
	}
	public void setLevelFirst() {
		setLevelIndex(0);
	}

	public List<BuildingLevel> levels() {
		if(levelNames == null) throw new PatcherLibBaseEx("Levels not set!");

		val res = new ArrayList<BuildingLevel>();

		for(int i = 1; i<= levelNames.size(); i++) {
			res.add(level(i));
		}

		return res;
	}

	public List<String> levelNames() {
		return levelNames;
	}


	public void addLevel(String levelName) {
		levelNames.add(levelName);
	}

	public BuildingLevel(String name, SettlType settlType, String levelsStr) {
		this(name, Arrays.asList(levelsStr.split(" ")), settlType);
	}
	public BuildingLevel(String name, List<String> levelNames, SettlType settlType) {
		Name = name;
		SettlType = settlType;
		this.levelNames = new ArrayList<>(levelNames);

		this.levelIndex = 0;
		this.LevelName = levelNames.get(this.levelIndex);
	}

	public BuildingLevel(String name, String levelName, SettlType settlType) {
		Name = name;
		LevelName = levelName;
		SettlType = settlType;
	}

	public BuildingLevel(String name, SettlType settlType) {
		Name = name;
		SettlType = settlType;
	}
}
