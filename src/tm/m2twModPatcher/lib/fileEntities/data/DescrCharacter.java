package tm.m2twModPatcher.lib.fileEntities.data;

import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.Arrays;

/**
 * Created by Tomek on 2016-04-22.
 */
public class DescrCharacter extends LinesProcessorFileEntity {

	public void setCharacterWage(String characterType, int wage) throws PatcherLibBaseEx {
		// ## Admiral ##
		LinesProcessor charactersLines = getLines();

		try {
			charactersLines.replaceFirstLineByLinePath(Arrays.asList("^\\s*type\\s+"+characterType, "^\\s*wage_base\\s+\\d"), "wage_base\t\t"+wage);
		}
		catch (Exception ex) {
			throw new PatcherLibBaseEx("Error setting wage for  characterType '"+characterType+"' in DescrCharacter");
		}
	}

	public DescrCharacter() {
		super("data\\descr_character.txt");
	}
}
