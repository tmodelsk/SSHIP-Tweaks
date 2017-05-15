package tm.m2twModPatcher.lib.fileEntities.data;

import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-05-09.
 */
public class DescrSMFactions extends LinesProcessorFileEntity {

	public void updateFactionAtttribute(String factionName, String attribute, String value) throws PatcherLibBaseEx {

		LinesProcessor lines = _Lines;

		int factionIndexc = lines.findFirstRegexLine("^faction\\s+"+factionName);

		if(factionIndexc < 0) throw new PatcherLibBaseEx("Faction "+factionName+" not found");


		// prefers_naval_invasions				yes
		Pattern regex = Pattern.compile("^("+attribute+")(\\s+)(\\S.*)");

		int attribLine = lines.findFirstByRegexLine(regex, factionIndexc+1);

		if(attribLine < 0) throw new PatcherLibBaseEx("Attribute "+attribute+" not found, faction "+factionName+"!");

		String lineOrg = lines.getLine(attribLine);

		Matcher matcher = regex.matcher(lineOrg);

		String att, spaces;

		if(matcher.matches()) {
			att = matcher.group(1);
			spaces = matcher.group(2);
		}
		else throw new PatcherLibBaseEx("Unexpected");

		String lineNew = att+spaces+value;

		lines.replaceLine(attribLine, lineNew);
	}



	public DescrSMFactions() {
		super("data\\descr_sm_factions.txt");
	}
}
