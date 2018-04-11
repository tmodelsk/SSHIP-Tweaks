package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;

public class UnitRecuitmentLineInfo extends UnitRecuitmentInfo {

	public int lineNumber;

	@Override
	public UnitRecuitmentLineInfo clone() {
		val base =  super.clone();
		return new UnitRecuitmentLineInfo(base, lineNumber);
	}

	public UnitRecuitmentLineInfo(UnitRecuitmentInfo unitRecr, int lineNumber) {
		this(unitRecr);
		this.lineNumber = lineNumber;
	}

	public UnitRecuitmentLineInfo(UnitRecuitmentInfo unitRecr) {
		this.Name = unitRecr.Name;
		this.InitialReplenishCounter = unitRecr.InitialReplenishCounter;
		this.ReplenishRate = unitRecr.ReplenishRate;
		this.MaxStack = unitRecr.MaxStack;
		this.ExperienceBonus = unitRecr.ExperienceBonus;
		this.RequirementStr = unitRecr.RequirementStr;
	}

	public UnitRecuitmentLineInfo() {
	}
}
