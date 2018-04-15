package tm.mtwModPatcher.lib.data.exportDescrUnit;

/**
 * Created by Tomek on 2016-07-16.
 */
public class WeaponStat {

	// stat_pri         18, 0, no, 0, 0, melee, melee_simple, slashing, none, 0, 1

	public int Damage;
	public int ChargeBonus;

	public String Ammunition;
	public int MissleRange;
	public int AmmunitionCount;

	public String WeaponType;
	public String TechType;
	public String DamageType;
	public String SoundType;
	public String SoundFireType;

	public int AttacksDelay;
	public double WeaponPreference;

	public String Comments = null;

	public boolean IsParsed = false;
	public String SourceStr;

	// ##### Logical Derived Properties ######

	public boolean IsMissleUnit() {
		return Ammunition != null && !Ammunition.equals("none");
	}

	public boolean IsArcherUnit() {
		return Ammunition != null && Ammunition.contains("arrow");
	}

	public boolean IsCrossbowUnit() {
		return Ammunition != null && Ammunition.contains("bolt");
	}

	@Override
	public String toString() {
		return "" + Damage + ", " + ChargeBonus + ", ...";
	}

	public static final String SOUND_TYPE_SPEAR = "spear";
}
