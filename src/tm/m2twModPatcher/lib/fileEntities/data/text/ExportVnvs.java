package tm.m2twModPatcher.lib.fileEntities.data.text;

import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.List;

/**
 * Created by Tomek on 2016-05-06.
 */
public class ExportVnvs extends LinesProcessorFileEntity {

	public void insertAtStartOfFile(String linesToInsert) {
		LinesProcessor lines = getLines();

		int insertIndex = lines.findExpFirstRegexLine("Listens_Teachings");

		lines.insertAt(insertIndex, linesToInsert);
	}

	public void insertAtStartOfFile(List<String> linesToInsert) {
		LinesProcessor lines = getLines();

		int insertIndex = lines.findExpFirstRegexLine("Listens_Teachings");

		lines.insertAt(insertIndex, linesToInsert);
	}


	public ExportVnvs()  {
		super("data\\text\\export_vnvs.txt" , "UnicodeLittle");
	}
}
