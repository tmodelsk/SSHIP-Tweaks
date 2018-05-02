package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

/** Building with Level  */
public class BuildingSimple {
	public final String name;
	public final String levelName;
	public final SettlType settlType;

	public BuildingSimple(String name, String levelName, SettlType settlType) {
		this.name = name;
		this.levelName = levelName;
		this.settlType = settlType;
	}
}
