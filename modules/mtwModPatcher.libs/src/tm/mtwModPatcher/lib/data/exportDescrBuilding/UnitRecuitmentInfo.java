package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.data.common.Format;
import tm.mtwModPatcher.lib.managers.FactionsDefs;

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

		Matcher matcher = FACTIONS_CONDITIONS_REGEX.matcher(RequirementStr);
		if (matcher.find()) {
			res = new UnitRequire();
			res.Factions = FactionsDefs.resolveFactions(matcher.group(1));
			res.RestConditions = matcher.group(2);
		}
		else if( (matcher = FACTIONS_REGEX.matcher(RequirementStr)).find() ) {
			res = new UnitRequire();
			res.Factions = FactionsDefs.resolveFactions(matcher.group(1));
		}

		return res;
	}
	public void setRequirementStr(UnitRequire unitRequire) {
		val sb = new StringBuilder();

		sb.append(" factions { " );

		for(val factionSymbol : unitRequire.Factions)
			sb.append(factionSymbol + ", ");
		sb.append("} ");

		if(unitRequire.RestConditions != null)
			sb.append(unitRequire.RestConditions);

		RequirementStr = sb.toString();
	}
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

	@Override
	public UnitRecuitmentInfo clone() {
		val u = new UnitRecuitmentInfo();

		u.Name = Name;
		u.InitialReplenishCounter = InitialReplenishCounter;
		u.ReplenishRate = ReplenishRate;
		u.MaxStack = MaxStack;
		u.ExperienceBonus = ExperienceBonus;
		u.RequirementStr = RequirementStr;

		return u;
	}
	@Override
	public String toString() {
		return Name;
	}

	public static UnitRecuitmentInfo parseUnitRecruitmentInfo(String recruitPoolStringLine) throws PatcherLibBaseEx {

		/*   recruit_pool  "Urban Spear Militia"  1   0.25   2  0  requires factions { pisa, venice, papal_states, sicily, }
			 ^\s*recruit_pool\s+"([\w\s]+)"\s+([\d\.]+)\s+([\d\.]+)\s+(\d+)\s+(\d+)\s+requires(.+) */

		Matcher matcher = UNIT_RECRUITMENT_LINE_REGEX.matcher(recruitPoolStringLine);

		UnitRecuitmentInfo result = new UnitRecuitmentInfo();

		if (matcher.find()) {

			try {
				result.Name = matcher.group(1);
				result.InitialReplenishCounter = Double.parseDouble(matcher.group(2));
				result.ReplenishRate = Double.parseDouble(matcher.group(3));
				result.MaxStack = Double.parseDouble(matcher.group(4));
				result.ExperienceBonus = Integer.parseInt(matcher.group(5));
				result.RequirementStr = matcher.group(6);
			}
			catch (Exception ex) {	throw new PatcherLibBaseEx("Error parsing unit recr info: " + recruitPoolStringLine, ex);	}
		}
		else
			throw new PatcherLibBaseEx("Unable to parse Building - Capabilities - Unit Recruitment Line - no match for regex !");

		return result;
	}

	public static final Pattern FACTIONS_CONDITIONS_REGEX = Pattern.compile("factions\\s+\\{(.+)}(.+)");
	public static final Pattern FACTIONS_REGEX = Pattern.compile("factions\\s+\\{(.+)}.*");
	public static final Pattern UNIT_RECRUITMENT_LINE_REGEX = Pattern.compile("^\\s*recruit_pool\\s+\"([\\w\\s']+)\"\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+(\\d+)\\s+requires(.+)");


	//         recruit_pool  "Spear Militia"  0   0.000001   0.1  0  requires factions { england, scotland, france, hre, denmark, spain, aragon, portugal, norway, teutonic_order, }
}
