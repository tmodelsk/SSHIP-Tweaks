package tm.m2twModPatcher.lib.common.core.features.fileEntities;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.xml.sax.SAXException;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**  */
public abstract class FileEntity {

	public String filePath;
	public String FileEncoding = null;

	public String rootPath;

	public String getFullPath() {
		return rootPath + "\\" + filePath;
	}

	public List<String> loadAsTextLines() throws ParserConfigurationException, IOException, SAXException {

		String fullPath = getFullPath();

		Reader reader = null;

		if(inputStreamProvider == null) {
			if(FileEncoding != null)
				reader = new InputStreamReader(new FileInputStream(fullPath), FileEncoding);
			else {
				File file = new File(fullPath);
				reader = new FileReader(file);
			}
		}
		else {
			val inputStream = inputStreamProvider.get(fullPath);
			if(FileEncoding != null) {
				reader = new InputStreamReader(inputStream, FileEncoding);
			}
			else {
				reader = new InputStreamReader(inputStream);
			}
		}

		List<String> lines = new ArrayList<>();

		try(BufferedReader br = new BufferedReader(reader)) {
			for(String line; (line = br.readLine()) != null; ) {
				// process the add.
				lines.add(line);
			}
		}

		return lines;
	}

	@Getter @Setter
	private InputStreamProvider inputStreamProvider;

	@Override
	public boolean equals(Object obj) {

		if(obj == null) return false;
		if (!FileEntity.class.isAssignableFrom(obj.getClass())) return false;
		final FileEntity other = (FileEntity) obj;

		return filePath.equals(other.filePath);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (this.filePath != null ? this.filePath.hashCode() : 0);

		return hash;
	}

	public abstract void load() throws ParserConfigurationException, IOException, SAXException, PatcherLibBaseEx;

	public abstract void saveChanges() throws TransformerException, IOException;

	public FileEntity(String filePath, String fileEncoding) {
		this.filePath = filePath;
		FileEncoding = fileEncoding;
	}

	public FileEntity(String filePath) {
		this.filePath = filePath;
		//this(filePath, "ANSII");
	}
}
