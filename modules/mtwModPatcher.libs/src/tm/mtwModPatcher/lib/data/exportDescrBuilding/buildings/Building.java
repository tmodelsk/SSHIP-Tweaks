package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.util.Objects;

/** Building with its BuildingTree */
public class Building extends BuildingSimple {

	public final int levelNumber;
	private BuildingTree buildingTree;
	public BuildingTree buildingTree() {
		return this.buildingTree;
	}
	public void buildingTree(BuildingTree buildingTree) {
		if(this.buildingTree == null) this.buildingTree = buildingTree;
		else throw new PatcherLibBaseEx("BuildingTree already assigned!");
	}


	public Building next() {
		if(buildingTree == null) throw new PatcherLibBaseEx("BuildingTree not set!");

		return buildingTree.level(levelNumber+1);
	}

	public boolean isNext() {
		return  levelNumber < buildingTree.size();
	}

	public Building(String name, SettlType settlType, String levelName, int levelNumber) {
		super(name, levelName, settlType);
		this.levelNumber = levelNumber;
	}

	public Building(BuildingTree buildingTree, String levelName, int levelNumber ) {
		super(buildingTree.name, levelName, buildingTree.settlType);
		this.buildingTree = buildingTree;
		this.levelNumber = levelNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Building)) return false;
		Building building = (Building) o;
		return Objects.equals(name, building.name) &&
				settlType == building.settlType &&
				Objects.equals(levelName, building.levelName) &&
				Objects.equals(buildingTree, building.buildingTree);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, settlType, levelName, buildingTree);
	}
}
