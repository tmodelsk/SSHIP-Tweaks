package tm.mtwModPatcher.lib.managers.garrisons;

/**
 * Created by Tomek on 2016-11-13.
 */
public class UnitMetaDef {

	public UnitType Type;

	public int Quantity;

	public int Experience;

	public int Armor;
	public int Weapon;

	public UnitMetaDef clone() {
		UnitMetaDef clone = new UnitMetaDef();

		clone.Type = Type;
		clone.Quantity = Quantity;
		clone.Experience = Experience;
		clone.Armor = Armor;
		clone.Weapon = Weapon;

		return clone;
	}

	public UnitMetaDef(UnitType type, int experience) {
		this();
		Type = type;
		Experience = experience;
	}

	public UnitMetaDef(UnitType type) {
		this();
		Type = type;
	}

	public UnitMetaDef() {
		Quantity = 1;
	}
}
