package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import java.util.List;

public class WallsCityTree extends WallsTree {
	public WallsCityTree(String levelsStr) {
		super(WallsCity.INSTANCE.Name, SettlType.City, levelsStr);
	}

	public WallsCityTree(List<String> levelNames) {
		super(WallsCity.INSTANCE.Name, levelNames, SettlType.City);
	}
}
