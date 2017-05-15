package tm.m2twModPatcher.lib.common.core.features.fileEntities;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-04-16.
 */
public abstract class TextLinesFileEntity extends FileEntity {

	protected List<String> _Lines = new ArrayList<>();

	public String GetLine(int index) {
		return _Lines.get(index);
	}

	public void UpdateAllRegexLines(String regex, String newLine ){

		int index = UpdateFirstRegexLine(regex, newLine, 0);
		index++;

		// if not found, incemented index will be 0
		while(index > 0) {
			index = UpdateFirstRegexLine(regex, newLine, index)+1;
		}
	}

	public int UpdateFirstRegexLine(String regex, String newLine, int startFromIndex) {

		int index = FindFirstRegexLine(regex, startFromIndex);

		if(index >= 0)
			ReplaceLine(index, newLine);

		return index;
	}

	public void RemoveAllRegexLines(String regex) {

		int index = 0;

		while (index >= 0) {
			index = FindFirstRegexLine(regex, index);

			if(index >= 0)	_Lines.remove(index);
		}
	}

	public void CommentLine(int lineIndex) {

		String line = _Lines.get(lineIndex);
		ReplaceLine(lineIndex, ";"+line);
	}

	public void ReplaceLine(int lineIndex, String newLine) {
		_Lines.remove(lineIndex);
		_Lines.add(lineIndex, newLine);
	}

	public int FindFirstRegexLine(String regex, int startFromIndex) {

		Pattern pattern = Pattern.compile(regex);

		int indexFound = -1;
		for (int i = startFromIndex; indexFound < 0 &&  i <  _Lines.size(); i++) {

			String line = _Lines.get(i);

			Matcher matcher = pattern.matcher(line);

			if(matcher.find()) {
				indexFound = i;
			}
		}


		return indexFound;
	}

	public int FindFirstRegexLine(String regex) {
		return FindFirstRegexLine(regex, 0);
	}

	public int FindFirstExactLine(String lineToFind, int startFromIndex ) {

		int indexFound = -1;
		for (int i = startFromIndex; indexFound < 0 &&  i <  _Lines.size(); i++) {

			if( lineToFind.equals( _Lines.get(i) ) ) {
				indexFound = i;
			}
		}

		return indexFound;
	}

	public int FindFirstExactLine(String lineToFind) {
		return FindFirstExactLine(lineToFind, 0);
	}

	public void InsertAt(int indexToInsert, String line) {
		_Lines.add(indexToInsert, line);
	}

	@Override
	public void load() throws ParserConfigurationException, IOException, SAXException {
		_Lines = loadAsTextLines();
	}

	@Override
	public void saveChanges() throws IOException {

		String fullPath = rootPath + "\\" + filePath;

		File fout = new File(fullPath);
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for (String line : _Lines) {
			bw.write(line);
			bw.newLine();
		}

		bw.close();
		fos.close();
	}

	public void ClearContent() {
		_Lines.clear();
	}

	public TextLinesFileEntity(String filePath) {
		super(filePath);
	}
}
