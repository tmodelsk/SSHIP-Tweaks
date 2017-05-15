package tm.m2twModPatcher.lib.managers.garrisons;

/**
 * Created by Tomek on 2016-11-13.
 */
public class UnitTypeFactionKey {

	public UnitType Type;

	public String FactionName;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UnitTypeFactionKey that = (UnitTypeFactionKey) o;

		if (Type != that.Type) return false;
		return FactionName.equals(that.FactionName);

	}

	@Override
	public int hashCode() {
		int result = Type.hashCode();
		result = 31 * result + FactionName.hashCode();
		return result;
	}

	public UnitTypeFactionKey(UnitType type, String factionName) {
		Type = type;
		FactionName = factionName;
	}

	public UnitTypeFactionKey() {
	}
}
