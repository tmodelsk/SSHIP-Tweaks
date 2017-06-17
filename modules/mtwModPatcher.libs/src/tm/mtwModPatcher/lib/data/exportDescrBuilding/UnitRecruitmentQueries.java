package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;
import tm.common.collections.ArrayUniqueList;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by tomek on 17.06.2017.
 */
public class UnitRecruitmentQueries {

	public List<UnitRecuitmentInfo> getByFactionsHiddenResourcesPositive(List<String> factions, List<String> hiddenResources) {
		String regex = "^\\s*recruit_pool\\s+\"([\\w\\s']+)\"\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+(\\d+)\\s+";
		regex += "requires\\s+";
		regex += "factions\\s+\\{.*(";

		boolean firstFaction = true;
		for(val factionSymbol : factions) {
			if(!firstFaction) regex += "|";
			regex += factionSymbol  +"\\s*,";
			firstFaction = false;
		}

		regex += ").*\\}";
		regex += ".*(and|or)\\s+hidden_resource\\s+(";

		boolean firstHr = true;
		for (val hr : hiddenResources) {
			if(!firstHr) regex += "|";
			regex += hr;
			firstHr = false;
		}

		regex += ")"; // |hidden_resource andalusia

		val pattern = Pattern.compile(regex);

		val unitRecrList = edb.findRecruitmentsByRegex(pattern);

		return unitRecrList;
	}

	public List<UnitRecuitmentInfo> getByFactionsNoHiddenResources(List<String> factions) {
		String regex = "^\\s*recruit_pool\\s+\"([\\w\\s']+)\"\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+(\\d+)\\s+";
		regex += "requires\\s+";
		regex += "factions\\s+\\{.*(";

		boolean firstFaction = true;
		for(val factionSymbol : factions) {
			if(!firstFaction) regex += "|";
			regex += factionSymbol  +"\\s*,";
			firstFaction = false;
		}

		regex += ").*\\}";
		//regex += ".*^(hidden_resource)";

		val pattern = Pattern.compile(regex);

		val unitRecrList = edb.findRecruitmentsByRegex(pattern);

		unitRecrList.removeIf( u -> u.RequirementStr.contains("hidden_resource")  );

		return unitRecrList;
	}

	public static List<String> toUnitNames(List<UnitRecuitmentInfo> recruitments) {
		val uniqueList = new ArrayUniqueList<String>();

		recruitments.forEach( r -> uniqueList.addUnique(r.Name) );

		return uniqueList;
	}

	private ExportDescrBuilding edb;

	public UnitRecruitmentQueries(ExportDescrBuilding edb) {
		this.edb = edb;
	}
}
