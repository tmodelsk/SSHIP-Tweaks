package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import java.util.List;

public class WallsCastleTree extends WallsTree {
	public WallsCastleTree(String levelsStr) {
		super(WallsCastle.INSTANCE.Name, SettlType.Castle, levelsStr);
	}

	public WallsCastleTree(List<String> levelNames) {
		super(WallsCastle.INSTANCE.Name, levelNames, SettlType.Castle);
	}
}
