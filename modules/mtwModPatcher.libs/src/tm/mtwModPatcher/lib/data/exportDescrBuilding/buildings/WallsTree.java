package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import java.util.List;

public abstract class WallsTree extends BuildingTree {
	public WallsTree(String name, SettlType settlType, String levelsStr) {
		super(name, settlType, levelsStr);
	}

	public WallsTree(String name, List<String> levelNames, SettlType settlType) {
		super(name, levelNames, settlType);
	}
}
