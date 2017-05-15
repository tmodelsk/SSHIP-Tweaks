package tm.m2twModPatcher.lib.fileEntities.data.unitModels;

import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-11-09.
 */
public class BattleModels extends LinesProcessorFileEntity {

	public void copyModelBlocksData(String modelName, String newFactionName, String sourceFactionName) throws PatcherLibBaseEx {
		copyModelBlockData(modelName, newFactionName, sourceFactionName, 1);
		copyModelBlockData(modelName, newFactionName, sourceFactionName, 2);
	}

	protected void copyModelBlockData(String modelName, String newFactionName, String sourceFactionName, int blockNumber) throws PatcherLibBaseEx {

		if(blockNumber != 1 && blockNumber != 2) throw new PatcherLibBaseEx("Block number "+blockNumber+" not supported !");

		LinesProcessor lines = getLines();

		int modelStartIndex = lines.findExpFirstRegexLine("^\\d+\\s+"+modelName+"\\s*");

		// ## Find First Section & its number
		String numberRegex = "^(\\d+)+\\s*$";
		int sectionStart = lines.findExpFirstRegexLine(numberRegex, modelStartIndex+1);

		if(blockNumber == 2) {
			sectionStart = lines.findExpFirstRegexLine(numberRegex, sectionStart+1);
		}
		int sectionCount=-1;

		String lineStr = lines.getLine(sectionStart);
		Pattern pattern = Pattern.compile(numberRegex);
		Matcher matcher = pattern.matcher(lineStr);

		if (matcher.find()) {
			sectionCount = Integer.parseInt( matcher.group(1) );
		}
		else throw new PatcherLibBaseEx("Unable to parse Section counter");

		// ## Find source lines
		int sourceFactionBlockStart = lines.findExpFirstRegexLine("^"+sourceFactionName.length()+" "+sourceFactionName+"\\s*", sectionStart);

		// ## Prepare Lines to copy ##
		List<String> linesToCopy = new ArrayList<>();
		int copyLinesCount;
		if(blockNumber == 1) copyLinesCount = 3;
		else if(blockNumber == 2) copyLinesCount = 2;
		else throw new PatcherLibBaseEx("Not Supported");

		for (int i = 1; i <= copyLinesCount; i++) {
			linesToCopy.add( lines.getLine(sourceFactionBlockStart+i) );
		}

		// ## Insert Copied Lines
		int lineIdndexToInsert = sectionStart+1;
		lines.insertAt(lineIdndexToInsert++, newFactionName.length()+" "+newFactionName+" ");	// 6 aragon
		for (int i = 0; i < copyLinesCount; i++) {
			lines.insertAt(lineIdndexToInsert, linesToCopy.get(i));
			lineIdndexToInsert++;
		}

		// ## Update Section Count marker ##
		sectionCount++;
		lines.replaceLine(sectionStart, sectionCount + " ");
	}

	public BattleModels() {
		super("data\\unit_models\\battle_models.modeldb");
	}
}
