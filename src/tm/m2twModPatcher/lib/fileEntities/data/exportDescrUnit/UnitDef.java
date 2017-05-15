package tm.m2twModPatcher.lib.fileEntities.data.exportDescrUnit;

import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;

/**
 * Created by Tomek on 2016-07-12.
 */
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

	public String Soldier;
	public String Officer1;
	public String Officer2;
	public String Officer3;
	public String Mount;
	public String MoutEffect;

	public String Ship;

	public String Engine;
	public String Attributes;
	public Double MoveSpeedMod;

	public String Formation;

	public String StatHealth;
	public UnitStatPri StatPri;
	public String StatPriAttr;
	public String StatSec;
	public String StatSecAttr;
	public String StatTer;
	public String StatTerAttr;

	public UnitStatPriArmor StatPriArmour;
	public String StatSecArmour;

	public Integer StatHeat;
	public String StatGround;
	public UnitStatMental StatMental; //public String StatMental;


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

	public void addStatPriAttribute(String newAttribute) {
		if( StatPriAttr != null ) {
			if(!StatPriAttr.isEmpty()) {
				StatPriAttr += ", " + newAttribute;
			} else StatPriAttr = newAttribute;
		}
		else StatPriAttr = newAttribute;
	}

	public void addOwnership(String factionName) throws PatcherLibBaseEx {

		Ownership += ", "+factionName;
	}

	public void addOwnership(String factionName, int eraNumber) throws PatcherLibBaseEx {

		String addStr = ", "+factionName;

		switch (eraNumber) {
			case 0:
				OwnershipEra0 += addStr;
				break;
			case 1:
				OwnershipEra1 += addStr;
				break;
			case 2:
				OwnershipEra2 += addStr;
				break;
			case 3:
				OwnershipEra3 += addStr;
				break;
		}
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

	public boolean isClassHeavy() {
		return Class.equals("heavy");
	}

	@Override
	public String toString() {

		return Name + " (" + StatPri.Damage + ")";

	}

	public static final String AttribFreeUpkeep = "free_upkeep_unit";
}
