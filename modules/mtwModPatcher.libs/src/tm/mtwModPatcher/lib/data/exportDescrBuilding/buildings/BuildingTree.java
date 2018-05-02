package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Buidling Tree */
public class BuildingTree {
	public final String Name;
	public final SettlType SettlType;

	private List<Building> buildings = new ArrayList<>();

	public Building first() {
		return level(1);
	}
	public Building last() {
		return level(size());
	}
	public Building level(int level) {
		return buildings.get(level-1);
	}
	public List<Building> levels() {
		return buildings;
	}
	public int size() {
		return buildings.size();
	}

	public void addLevel(String levelName) {
		buildings.add(new Building(this, levelName, buildings.size()+2));
	}

	public BuildingTree(String name, SettlType settlType, String levelsStr) {
		this(name, Arrays.asList(levelsStr.split(" ")), settlType);
	}
	public BuildingTree(String name, List<String> levelNames, SettlType settlType) {
		Name = name;
		SettlType = settlType;

		for(int i=0; i<levelNames.size(); i++) {
			buildings.add(new Building(this, levelNames.get(i), i+1));
		}
	}

}
