package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.data.common.Format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parsed Typed Unit Recruitment Information from ExportDescrBuildings */
public class UnitRecuitmentInfo {

	public String Name;

	public double InitialReplenishCounter;

	public double ReplenishRate;

	public double MaxStack;

	public int ExperienceBonus;

	public String RequirementStr;

	public UnitRequire getUnitRequireSimple() {
		val factionsSplit = RequirementStr.split("factions");
		if(factionsSplit.length > 2) return null;

		UnitRequire res = null;

		Matcher matcher = factionRegex.matcher(RequirementStr);
		if (matcher.find()) {
			res = new UnitRequire();
			res.Factions = FactionsDefs.resolveFactions(matcher.group(1));
			res.RestConditions = matcher.group(2);
		}

		return res;
	}

	public void setRequirementStr(UnitRequire unitRequire) {
		val sb = new StringBuilder();

		sb.append(" factions { " );

		for(val factionSymbol : unitRequire.Factions)
			sb.append(factionSymbol + ", ");
		sb.append("} ");

		sb.append(unitRequire.RestConditions);

		RequirementStr = sb.toString();
	}

	private static final Pattern factionRegex = Pattern.compile("factions\\s+\\{(.+)}(.+)");


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
