package tm.mtwModPatcher.lib.common.core.features.fileEntities;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;

/**
 * Created by Tomek on 2016-04-21.
 */
public class LinesProcessorFileEntity extends FileEntity {

	protected LinesProcessor _Lines;

	public LinesProcessor getLines() {
		return _Lines;
	}
	public void setLines(LinesProcessor lines) {
		_Lines = lines;
	}

	public LinesProcessorFileEntity(String filePath) {
		super(filePath);
	}
	public LinesProcessorFileEntity(String filePath, String fileEncoding) {
		super(filePath, fileEncoding);
	}

	@Override
	public void load() throws ParserConfigurationException, IOException, SAXException {

		_Lines = new LinesProcessor(filePath);
		_Lines.setLines(loadAsTextLines());
	}

	@Override
	public void saveChanges() throws TransformerException, IOException {
		File fout = new File(getFullPath());
		FileOutputStream fos = new FileOutputStream(fout);

		Writer writer;

		if(FileEncoding != null) {
			writer = new OutputStreamWriter(fos, FileEncoding);
		}
		else writer = new OutputStreamWriter(fos);

		BufferedWriter bw = new BufferedWriter(writer);

		_Lines.saveChanges(bw);

		bw.close();
		fos.close();
	}
}
