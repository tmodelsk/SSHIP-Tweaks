package tm.mtwModPatcher.lib.common.core.features.fileEntities;

import tm.mtwModPatcher.lib.common.core.features.fileEntities.sections.Section;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.List;

/**
 * Created by Tomek on 2016-04-18.
 */
public abstract class SectionsFileEntity extends FileEntity {

	protected List<Section> _Sections;

	@Override
	public void saveChanges() throws TransformerException, IOException {
		File fout = new File(getFullPath());
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		boolean isNewLineNeeded = false;
		for (Section section : _Sections) {

			if(isNewLineNeeded)
				bw.newLine();

			section.SaveChanges(bw);
			isNewLineNeeded = true;
		}

		bw.close();
		fos.close();
	}

	public SectionsFileEntity(String filePath) {
		super(filePath);
	}

}
