package tm.mtwModPatcher.lib.managers.garrisons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomek on 2016-11-13.
 */
public class UnitGarrisonInfo {

	public String Name;

	public UnitType Type;

	public int Quantity;

	public int Experience;

	public int Armor;
	public int Weapon;

	public List<String> RequiredResources = new ArrayList<>();

	public boolean equals(UnitGarrisonInfo unitOther) {
		if(unitOther == null) return  false;

		return Name.equals(unitOther.Name) && Type == unitOther.Type
				&& Experience == unitOther.Experience && Armor == unitOther.Armor && Weapon == unitOther.Weapon;
	}

	public UnitGarrisonInfo(String name, int experience) {
		Name = name;
		Experience = experience;
	}

	public UnitGarrisonInfo(String name, String requiredResource) {
		Name = name;
		RequiredResources.add(requiredResource);
	}
	public UnitGarrisonInfo(String name, int experience, String requiredResource) {
		Name = name;
		Experience = experience;
		RequiredResources.add(requiredResource);
	}
	public UnitGarrisonInfo(String name, List<String> requiredResources) {
		Name = name;
		RequiredResources = requiredResources;
	}
	public UnitGarrisonInfo(String name,  int experience, List<String> requiredResources) {
		Name = name;
		Experience = experience;
		RequiredResources = requiredResources;
	}

	public UnitGarrisonInfo(String name) {
		Name = name;
	}

	public UnitGarrisonInfo() {
	}
}
