package tm.mtwModPatcher.lib.fileEntities.data;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ExportDescrCharacterTraits extends LinesProcessorFileEntity {

	public void commentNegativeChivalryFromTraitEffect(String traitName, String levelName) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		int traitStart = lines.findFirstRegexLine("^\\s*Trait\\s+"+traitName);
		if(traitStart < 0) throw new PatcherLibBaseEx("Trait "+traitName+" not found!");

		int traitEnd = lines.findFirstRegexLine("^;------------", traitStart+1);
		if(traitEnd < 0) throw new PatcherLibBaseEx("Trait "+traitName+" problem - cannot determine end!");

		int levelStart = lines.findFirstRegexLine("^\\s*Level\\s+"+levelName, traitStart+1);
		if(levelStart < 0 || levelStart > traitEnd) throw new PatcherLibBaseEx("Trait "+traitName+" Level "+levelName+" not found!");

		int levelEnd = lines.findFirstRegexLine("^\\s*Level\\s+", levelStart+1);
		if(levelEnd < 0 || levelEnd > traitEnd) levelEnd = traitEnd;

		//    Effect Chivalry -1
		int index = lines.findFirstRegexLine("^\\s*Effect Chivalry\\s+-\\d+", levelStart+1, levelEnd);
		if(index > 0) lines.commentLine(index);
	}

	public void commentAffectTraitInTrigger(String triggerName, String traitName) {
		val index = loadIndexOfAffectTraitInTrigger(triggerName, traitName);

		_Lines.commentLine(index);
	}

	public void setAffectTraitChanceInTrigger(String triggerName, String traitName, int chance) {
		val index = loadIndexOfAffectTraitInTrigger(triggerName, traitName);

		val pair = parseAffectTraitLineInTrigger(_Lines.getLine(index));

		val newLine = pair.getKey()+" " + chance;
		_Lines.replaceLine(index, newLine);
	}

	public void multiplyAffectTraitChanceInTrigger(String triggerName, String traitName, double chanceMulti) {
		val index = loadIndexOfAffectTraitInTrigger(triggerName, traitName);

		val pair = parseAffectTraitLineInTrigger(_Lines.getLine(index));

		val newLine = pair.getKey()+" " + (int)(pair.getValue()*chanceMulti);
		_Lines.replaceLine(index, newLine);

	}

	public Map.Entry<String, Integer> parseAffectTraitLineInTrigger(String line) {
		Map.Entry<String, Integer> result = null;

		// Affects BattleDread 1 Chance 25

		val regex = "(^.+\\s+Chance\\s+)(\\d+)";
		Pattern p = Pattern.compile(regex);
		val matcher = p.matcher(line);

		if(matcher.find()) {
			result = new AbstractMap.SimpleEntry<String, Integer>(matcher.group(1), Integer.parseInt(matcher.group(2)));
		}

		return  result;
	}

	public int loadIndexOfAffectTraitInTrigger(String triggerName, String traitName) {
		LinesProcessor lines = _Lines;

		int traitStart = lines.findFirstRegexLine("^\\s*Trigger\\s+"+triggerName);
		if(traitStart < 0) throw new PatcherLibBaseEx("Trigger "+triggerName+" not found!");

		int traitEnd = lines.findFirstRegexLine("^;------------", traitStart+1);
		if(traitEnd < 0) throw new PatcherLibBaseEx("Trigger "+triggerName+" problem - cannot determine end!");

		int affectTraitIndex = lines.findFirstRegexLine("^\\s*Affects\\s+"+ traitName +"\\s+", traitStart+1, traitEnd);
		if(affectTraitIndex <= 0)
			throw new PatcherLibBaseEx("Trigger ["+triggerName+"] Affects "+ traitName +" not found!");

		return affectTraitIndex;
	}

	public void insertNewAttribute(String traitName, String levelName, String attrubuteStr) throws PatcherLibBaseEx {

		LinesProcessor lines = _Lines;

		int traitStart = lines.findFirstRegexLine("^\\s*Trait\\s+"+traitName);
		if(traitStart < 0) throw new PatcherLibBaseEx("Trait "+traitName+" not found!");

		int traitEnd = lines.findFirstRegexLine("^;------------", traitStart+1);
		if(traitEnd < 0) throw new PatcherLibBaseEx("Trait "+traitName+" problem - cannot determine end!");

		int levelStart = lines.findFirstRegexLine("^\\s*Level\\s+"+levelName, traitStart+1);
		if(levelStart < 0 || levelStart > traitEnd) throw new PatcherLibBaseEx("Trait "+traitName+" Level "+levelName+" not found!");

		int levelEnd = lines.findFirstRegexLine("^\\s*Level\\s+", levelStart+1);
		if(levelEnd < 0 || levelEnd > traitEnd) levelEnd = traitEnd;


		lines.insertAt(levelEnd-1, attrubuteStr);
	}

	public ExportDescrCharacterTraits() {
		super("data\\export_descr_character_traits.txt");
	}
}
