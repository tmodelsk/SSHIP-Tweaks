package tm.mtwModPatcher.lib.data.exportDescrUnit;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-04-21.
 * OBSOLETE - USE ExportDescrUnitTyped !!!!!
 */
@Deprecated
public class ExportDescrUnit extends LinesProcessorFileEntity {

	public void updateCostAndUpkeepByMultipliers(String unitName, double costMultiplier, double upkeepMultiplier) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		int lineIndex = lines.findFirstLineByLinePath(Arrays.asList(
				"^type\\s+"+unitName+"\\s*$",
				"^stat_cost\\s+"));
		if(lineIndex < 0) throw new PatcherLibBaseEx("Unit "+unitName+" & stat_cost NOT FOUND !!");

		String line = lines.getLine(lineIndex);

		// stat_cost        2, 3096, 339, 85, 339, 339, 3, 150

		Pattern pattern = Pattern.compile("(^stat_cost\\s+\\d+\\s*,\\s*)(\\d+)(\\s*,\\s*)(\\d+)(\\s*,.+)");
		Matcher matcher = pattern.matcher(line);

		if(matcher.find()) {

			String prefix, costStr, middleStr, upkeepStr, suffix;

			prefix = matcher.group(1);
			costStr = matcher.group(2);
			middleStr = matcher.group(3);
			upkeepStr = matcher.group(4);
			suffix = matcher.group(5);

			int cost = Integer.parseInt(costStr);
			cost = (int)(cost * costMultiplier);

			int upkeep = Integer.parseInt(upkeepStr);
			upkeep = (int)(upkeep*upkeepMultiplier);

			line = prefix + cost + middleStr + upkeep + suffix;
			lines.replaceLine(lineIndex, line);

		} else throw new PatcherLibBaseEx("Unable to parse stat_cost add");
	}

	public void updateUpkeepByMultiplier(String unitName, double multiplier) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		int lineIndex = lines.findFirstLineByLinePath(Arrays.asList(
				"^type\\s+"+unitName+"\\s*$",
				"^stat_cost\\s+"));
		if(lineIndex < 0) throw new PatcherLibBaseEx("Unit "+unitName+" & stat_cost NOT FOUND !!");

		String line = lines.getLine(lineIndex);

		// stat_cost        2, 3096, 339, 85, 339, 339, 3, 150

		Pattern pattern = Pattern.compile("(^stat_cost\\s+\\d+,\\s*\\d+,\\s*)(\\d+)(,.+)");
		Matcher matcher = pattern.matcher(line);

		if(matcher.find()) {

			String prefix, upkeepStr, suffix;

			prefix = matcher.group(1);
			upkeepStr = matcher.group(2);
			suffix = matcher.group(3);

			int upkeep = Integer.parseInt(upkeepStr);
			upkeep = (int)(upkeep*multiplier);

			line = prefix+upkeep+suffix;
			lines.replaceLine(lineIndex, line);

		} else throw new PatcherLibBaseEx("Unable to parse stat_cost add");
	}

	public void setAttributeForAllUnits(String attribute) throws PatcherLibBaseEx {
		int index = 0;


		String tmpLine;

		while(index  >= 0) {

			// attributes       sea_faring, can_withdraw
			index = _Lines.findFirstRegexLine("^attributes\\s+", index + 1 );

			if(index >= 0) {
				tmpLine = _Lines.getLine(index);
				_Lines.replaceLine(index, tmpLine+", "+attribute);
			}
		}
	}

	public void AddOwnership(String unitName, String factionName) throws PatcherLibBaseEx {
		String ownershipTagName = "ownership";
		AddOwnership(unitName, factionName, ownershipTagName);
	}

	public void AddOwnership(String unitName, String factionName, int eraNumber) throws PatcherLibBaseEx {
		String ownershipTagName = "era "+Integer.toString(eraNumber);
		AddOwnership(unitName, factionName, ownershipTagName);
	}

	protected void AddOwnership(String unitName, String factionName, String ownershipTagName) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		int lineIndex = lines.findFirstLineByLinePath(Arrays.asList(
				"^type\\s+"+unitName+"\\s*$",
				"^"+ownershipTagName+"\\s+"));
		if(lineIndex < 0) throw new PatcherLibBaseEx("Unit "+unitName+" "+ownershipTagName+" NOT FOUND !!");

		lines.appendLineEnd(lineIndex, ", "+factionName);

	}

	public ExportDescrUnit() {
		super("data\\export_descr_unit.txt");
	}
}
