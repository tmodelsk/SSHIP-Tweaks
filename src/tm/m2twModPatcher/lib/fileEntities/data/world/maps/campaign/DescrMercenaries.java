package tm.m2twModPatcher.lib.fileEntities.data.world.maps.campaign;

import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-05-07.
 */
public class DescrMercenaries extends LinesProcessorFileEntity {

	public void updateUnitCostsByMultiplier(String unitName, double multiplier) throws PatcherLibBaseEx {
		// 	unit merc cog,				exp 1 cost 555 replenish 0.36 - 1.0 max 1 initial 1 religions { catholic } crusading

		String prefix, unitNameRaw, middle, costStr, suffix, unitNameExtracted;
		int costOrg, costUpdated;
		Pattern pattern = Pattern.compile("(^\\s*unit\\s+)(" +  unitName + ")(\\s+exp.+cost\\s+)(\\d+)(.+)");


		for(int i=0; i<_Lines.getLines().size(); i++) {
			String line = _Lines.getLine(i);

			Matcher matcher = pattern.matcher(line);

			if(matcher.find()) {
				prefix = matcher.group(1);

				unitNameRaw = matcher.group(2);
				unitNameExtracted = unitNameRaw.replace(",", "");
				unitNameExtracted = unitNameExtracted.trim();

				if(!unitName.equals(unitNameExtracted)) throw new PatcherLibBaseEx("Given unit name '"+unitName+"' is different then extracted '"+unitNameExtracted+"'");


				middle = matcher.group(3);
				costStr = matcher.group(4);
				suffix = matcher.group(5);

				costOrg = Integer.parseInt(costStr);
				costUpdated = (int)(costOrg * multiplier);

				line = prefix+unitNameRaw+middle+costUpdated+suffix;

				_Lines.replaceLine(i, line);
			}
		}
	}

	public Set<String> updateAllCostsByMultiplier(double multiplier, List<String> unitNamesExcluded) throws PatcherLibBaseEx {
		Set<String> unitNames = new HashSet<>();

		// 	unit merc cog,				exp 1 cost 555 replenish 0.36 - 1.0 max 1 initial 1 religions { catholic } crusading

		String prefix, unitNameRaw, unitName, middle, costStr, suffix;
		int costOrg, costUpdated;
		Pattern pattern = Pattern.compile("(^\\s*unit\\s+)(.+)(\\s+exp.+cost\\s+)(\\d+)(.+)");


		for(int i=0; i<_Lines.getLines().size(); i++) {
			String line = _Lines.getLine(i);

			Matcher matcher = pattern.matcher(line);

			if(matcher.find()) {
				prefix = matcher.group(1);

				unitNameRaw = matcher.group(2);
				unitName = unitNameRaw.replace(",", "");
				unitName = unitName.trim();

				String unitNameTmp = unitName;

				if( unitNamesExcluded.stream().filter( un -> un.equals(unitNameTmp) ).count() > 0 )
					continue;

				middle = matcher.group(3);
				costStr = matcher.group(4);
				suffix = matcher.group(5);

				costOrg = Integer.parseInt(costStr);
				costUpdated = (int)(costOrg * multiplier);

				line = prefix+unitNameRaw+middle+costUpdated+suffix;

				_Lines.replaceLine(i, line);

				if(!unitNames.contains(unitName)) unitNames.add(unitName);
			}
		}

		return unitNames;
	}

	public Set<String> getAllUnitNames() throws PatcherLibBaseEx {
		Set<String> unitNames = new HashSet<>();

		// 	unit merc cog,				exp 1 cost 555 replenish 0.36 - 1.0 max 1 initial 1 religions { catholic } crusading
		//  unit Mercenary Crossbowmen		exp 2 cost 2484 replenish 0.03 - 0.09 max 1 initial 0 end_year 1450

		String prefix, unitNameRaw, unitName;
		Pattern pattern = Pattern.compile("(^\\s*unit\\s+)(.+)(\\s+exp.+cost\\s+)(\\d+)(.+)");


		for(int i=0; i<_Lines.getLines().size(); i++) {
			String line = _Lines.getLine(i);

			Matcher matcher = pattern.matcher(line);

			if(matcher.find()) {
				unitNameRaw = matcher.group(2);
				unitName = unitNameRaw.replace(",", "");
				unitName = unitName.trim();

				if(!unitNames.contains(unitName)) unitNames.add(unitName);
			}
		}

		return unitNames;
	}

	public void addUnitRecruitmentLine(String poolName, List<String> unitsLinesStr) throws PatcherLibBaseEx {
		for (String unitLineStr : unitsLinesStr)
			addUnitRecruitmentLine(poolName, unitLineStr);
	}

	public void addUnitRecruitmentLine(String poolName, String unitLineStr) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		int index = lines.findExpFirstRegexLine("^pool\\s+"+poolName);

		// index 		: poll ...
		// index +1		: 	regions ... , ... , ...
		lines.insertAt(index + 2 , unitLineStr);

	}

	public DescrMercenaries() {
		super("data\\world\\maps\\campaign\\imperial_campaign\\descr_mercenaries.txt");
	}
}
