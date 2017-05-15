package tm.m2twModPatcher.lib.fileEntities.data.exportDescrBuilding;

import tm.m2twModPatcher.lib.fileEntities.data.common.Format;

/** Parsed Typed Unit Recruitment Information from ExportDescrBuildings */
public class UnitRecuitmentInfo {

	public String Name;

	public double InitialReplenishCounter;

	public double ReplenishRate;

	public double MaxStack;

	public int ExperienceBonus;

	public String RequirementStr;


	public String toRecruitmentPoolLine() {
		// "        recruit_pool  \"Pisan and Geonese sailors\"  1   0.25   2  0  requires factions { jerusalem, } and not event_counter HEAVY_MAIL_ARMOR 1"
		String line;
		line = "        recruit_pool  \""+this.Name +"\"";
		line += "  " + Format.toString(this.InitialReplenishCounter);
		line += "   " + Format.toString(this.ReplenishRate);
		line += "   " + Format.toString(this.MaxStack);
		line += "  " + this.ExperienceBonus;
		line += "  requires" + this.RequirementStr;

		return line;
	}

}
