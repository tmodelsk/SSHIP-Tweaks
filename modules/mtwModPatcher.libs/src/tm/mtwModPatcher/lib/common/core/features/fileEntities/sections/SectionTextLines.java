package tm.mtwModPatcher.lib.common.core.features.fileEntities.sections;

import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Tomek on 2016-04-18.
 */
public class SectionTextLines extends Section {

	protected LinesProcessor _Lines;

	public LinesProcessor lines() {
		return _Lines;
	}
	public void setLines(LinesProcessor lines) {
		_Lines = lines;
	}

	@Override
	public void SaveChanges(BufferedWriter bw) throws IOException {

		boolean isNewLineNeeded = false;

		for (String lineStr : _Lines.getLines()) {

			if(isNewLineNeeded)
				bw.newLine();

			bw.write(lineStr);
			isNewLineNeeded = true;
		}
	}

	public SectionTextLines(String name) {
		super(name);
	}

	public SectionTextLines(String name, LinesProcessor lines) {

		super(name);
		_Lines = lines;
	}


	public SectionTextLines() {
	}
}
