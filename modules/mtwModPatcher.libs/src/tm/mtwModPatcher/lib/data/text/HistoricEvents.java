package tm.mtwModPatcher.lib.data.text;

import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.List;

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
