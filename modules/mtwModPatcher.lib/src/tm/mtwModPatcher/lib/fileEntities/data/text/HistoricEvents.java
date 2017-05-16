package tm.mtwModPatcher.lib.fileEntities.data.text;

import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.List;

/**
 * Created by tomek on 22.04.2017.
 */
public class HistoricEvents extends LinesProcessorFileEntity {

	public void insertAtStartOfFile(List<String> linesToInsert) {
		LinesProcessor lines = getLines();

		int insertIndex = lines.findExpFirstRegexLine("SACK_PLAYER_BODY");

		lines.insertAt(insertIndex, linesToInsert);
	}

	public HistoricEvents() {
		super("data\\text\\historic_events.txt", "UnicodeLittle");
	}
}
