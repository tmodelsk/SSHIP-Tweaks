package tm.mtwModPatcher.lib.data.exportDescrUnit;

import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

/** Unit Definition dto - EDU record */
public class UnitDef {

	public String Name;		// type
	public String Dictionary;
	public String Category;
	public String Class;

	public String Accent;
	public String VoiceType;
	public String BannerFaction;
	public String BannerUnit;
	public String BannerHoly;

	public Soldier Soldier;
	public String Officer1;
	public String Officer2;
	public String Officer3;
	public String Mount;
	public MountsEffects MountEffect;

	public String Ship;

	public String Engine;
	public String Attributes;
	public Double MoveSpeedMod;

	public String Formation;

	public String StatHealth;
	public WeaponStat StatPri;
	public WeaponAttributes StatPriAttr;
	public WeaponStat StatSec;
	public String StatSecAttr;
	public String StatTer;
	public String StatTerAttr;

	public StatPriArmor StatPriArmour;
	public String StatSecArmour;

	public Integer StatHeat;
	public String StatGround;
	public tm.mtwModPatcher.lib.data.exportDescrUnit.StatMental StatMental;


	public String StatChargeDist;
	public String StatFireDelay;
	public String StatFood;
	public UnitStatCost StatCost;
	public String StatStl;

	public String ArmourUgLevels;
	public String ArmourUgModels;

	public String Ownership;
	public String OwnershipEra0;
	public String OwnershipEra1;
	public String OwnershipEra2;
	public String OwnershipEra3;

	public int RecruitPriorityOffset;
	public String LastComment;

	public void addAttribute(String attributeName) {
		if(Attributes.contains(attributeName)) return;

		Attributes += ", "+ attributeName;
	}

	public void removeAttribute(String attributeName) {
		if(! Attributes.contains(attributeName)) return;

		String tmp = Attributes;

		tmp = tmp.replaceAll(attributeName , "");
		tmp = tmp.replaceAll(",\\s*," , ",");
		tmp = tmp.replaceAll("^\\s*," , "");
		tmp = tmp.replaceAll("\\s*,\\s*$" , "");

		Attributes = tmp;
	}

	public boolean hasAttribute(String attributeName) {
		if(Attributes == null) return false;

		return Attributes.contains(attributeName);
	}

	public boolean hasFormation(String formationName) {
		if(Formation == null) return false;

		return Formation.contains(formationName);
	}

	public void addStatPriAttribute(String newAttribute) {
		StatPriAttr.add(newAttribute);
	}

	public void addOwnership(String factionName) throws PatcherLibBaseEx {

		Ownership += ", "+factionName;
	}

	public void addOwnership(String factionName, int eraNumber) throws PatcherLibBaseEx {

		switch (eraNumber) {
			case 0:
				OwnershipEra0 = resolveAddOwnership(OwnershipEra0, factionName);
				break;
			case 1:
				OwnershipEra1 = resolveAddOwnership(OwnershipEra1, factionName);
				break;
			case 2:
				OwnershipEra2 = resolveAddOwnership(OwnershipEra2, factionName);
				break;
			case 3:
				OwnershipEra3 = resolveAddOwnership(OwnershipEra3, factionName);
				break;
		}
	}
	private String resolveAddOwnership(String actualOwnership, String factionName) {
		if(actualOwnership == null || actualOwnership.isEmpty()) return factionName;
		else return ", "+factionName;
	}

	public void multiplyMoveSpeedMod(double value) {
		if(MoveSpeedMod == null) MoveSpeedMod = value;
		else MoveSpeedMod *= value;
	}

	public boolean isCategoryInfantry() {
		return Category.equals("infantry");
	}
	public boolean isCategoryCavalry() {
		return Category.equals("cavalry");
	}
	public boolean isCategorySiege() {
		return Category.equals("siege");
	}

	public boolean isClassHeavy() {
		return Class.equals("heavy");
	}
	public boolean isClassLight() {
		return Class.equals("light");
	}
	public boolean isClassSpearmen() {
		return Class.equals("spearmen");
	}
	public boolean isClassSkirmish() {
		return Class.equals("skirmish");
	}
	public boolean isClassMissile() {
		return Class.equals("missile");
	}

	public static final UnitClass CLASS_HEAVY = new UnitClass("heavy");
	public static final UnitClass CLASS_LIGHT = new UnitClass("light");

	public static final String ATTRIB_FREE_UPKEEP = "free_upkeep_unit";
	public static final String ATTRIB_IS_PEASANT = "is_peasant";
	public static final String FORMATION_PHALANX = "phalanx";

	@Override
	public String toString() {

		if(StatSec != null && StatSec.Damage != 0)
			return Ctm.format("{0} (PA:{1} SA:{2} D:{3})",Name , StatPri.Damage, StatSec.Damage, StatPriArmour.defence());
		else
			return Ctm.format("{0} (PA:{1} D:{2})",Name , StatPri.Damage, StatPriArmour.defence());
	}
}
