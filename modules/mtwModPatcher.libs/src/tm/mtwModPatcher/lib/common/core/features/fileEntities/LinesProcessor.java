package tm.mtwModPatcher.lib.common.core.features.fileEntities;

import javafx.util.Pair;
import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lines Processor - multiple lines & processing
 */
public class LinesProcessor {

	public static LinesProcessor load(String fullPath, InputStreamProvider inputStreamProvider) throws IOException {
		Reader reader;
		//File file = new File(fullPath);
		//reader = new FileReader(file);

		val inputStream = inputStreamProvider.get(fullPath);
		reader = new InputStreamReader(inputStream);

		List<String> lines = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(reader)) {
			for (String line; (line = br.readLine()) != null; ) {
				// process the add.
				lines.add(line);
			}
			// add is not visible here.
		}

		val linesProcessor = new LinesProcessor();
		linesProcessor.setLines(lines);

		return linesProcessor;
	}

	public static LinesProcessor load(String fullPath) throws IOException {
		Reader reader;
		File file = new File(fullPath);
		reader = new FileReader(file);

		List<String> lines = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(reader)) {
			for (String line; (line = br.readLine()) != null; ) {
				// process the add.
				lines.add(line);
			}
			// add is not visible here.
		}

		val linesProcessor = new LinesProcessor();
		linesProcessor.setLines(lines);

		return linesProcessor;
	}

	// Returns subsetCopy COPY of source Lines
	public LinesProcessor subsetCopy(int start, int endInclusive) {
		LinesProcessor subLines = new LinesProcessor();

		List<String> srcLines = _Lines.subList(start, endInclusive + 1);
		List<String> destLines = new ArrayList<>();

		for (String line : srcLines)
			destLines.add(line);

		subLines.setLines(destLines);

		return subLines;
	}

	// Returns subsetCopy COPY of source Lines
	public LinesProcessor subsetCopy(int start) {
		LinesProcessor subLines = new LinesProcessor();

		List<String> lines = new ArrayList<>();

		lines.addAll(_Lines.subList(start, _Lines.size()));
		//lines.add(_Lines.getFirst(_Lines.size()-1));

		subLines.setLines(lines);

		return subLines;
	}


	public void appendLineEnd(int lineIndex, String appendString) throws PatcherLibBaseEx {
		String line = getLine(lineIndex);
		replaceLine(lineIndex, line + appendString);
	}

	public int findFirstLineByLinePath(List<String> linePath) throws PatcherLibBaseEx {
		return findFirstLineByLinePath(linePath, 0);
	}

	public int findFirstLineByLinePath(List<String> linePath, int startFromIndex) throws PatcherLibBaseEx {

		int index = startFromIndex;

		for (String lineRegex : linePath) {

			index = findFirstRegexLine(lineRegex, index);
			if (index < 0) break;    // not found, BREAK LOOP
			else index++;            // Found, lets continiue from next index
		}

		return index - 1;        // we have advanced to next index.
	}

	public int findFirstByRexexLines(String lineRegex1, String lineRegex2) throws PatcherLibBaseEx {

		Pattern pattern1 = Pattern.compile(lineRegex1);
		Pattern pattern2 = Pattern.compile(lineRegex2);

		int index = 0;

		while (index >= 0) {
			index = findFirstByRegexLine(pattern1, index);

			if (index >= 0) {
				// found add #1, check add #2
				Matcher matcher = pattern2.matcher(_Lines.get(index + 1));

				if (matcher.find()) {
					// ok, match found !!!
					return index;
				} else index++;
			}
		}

		return index;
	}

	public String getLine(int index) {
		return _Lines.get(index);
	}

	public String getLine(Pattern pattern) throws PatcherLibBaseEx {

		return getLine(findExpFirstByRegexLine(pattern));
	}

	public void updateAllRegexLines(String regex, String newLine) throws PatcherLibBaseEx {

		int index = updateFirstRegexLine(regex, newLine, 0);
		index++;

		// if not found, incemented index will be 0
		while (index > 0) {
			index = updateFirstRegexLine(regex, newLine, index) + 1;
		}
	}

	public int updateFirstRegexLine(String regex, String newLine, int startFromIndex) throws PatcherLibBaseEx {

		int index = findFirstRegexLine(regex, startFromIndex);

		if (index >= 0)
			replaceLine(index, newLine);

		return index;
	}

	public void removeAllRegexLines(String regex) {
		val patt = Pattern.compile(regex);
		removeAllRegexLines(patt);
	}
	public void removeAllRegexLines(Pattern regexPatt) {
		int index = 0;
		while (index >= 0) {
			index = findFirstRegexLine(regexPatt, index);
			if (index >= 0) _Lines.remove(index);
		}
	}

	public void removeAllRegexLinesInRange(String regex, int startIndex, int endIndex) throws PatcherLibBaseEx {

		int index = 0;

		while (index >= 0) {
			index = findFirstRegexLine(regex, startIndex, endIndex);

			if (index >= 0) _Lines.remove(index);
		}
	}

	public void commentLine(int lineIndex) throws PatcherLibBaseEx {

		String line = _Lines.get(lineIndex);
		replaceLine(lineIndex, ";" + line);
	}

	public void remove(int index) {
		_Lines.remove(index);
	}

	public void removeRange(int startIndex, int endIndex) throws PatcherLibBaseEx {
		if (endIndex < startIndex) throw new PatcherLibBaseEx("EndIndex is less then StartIndex");

		for (int i = endIndex; i >= startIndex; i--) {
			remove(i);
		}
	}

	public void replaceFirstLineByLinePath(List<String> linePath, String newLine) throws PatcherLibBaseEx {
		int index = findFirstLineByLinePath(linePath);

		if (index < 0) throw new PatcherLibBaseEx("Line Not found! by LinePath");

		replaceLine(index, newLine);
	}

	public void replaceLine(int lineIndex, String newLine) throws PatcherLibBaseEx {

		if (newLine == null) throw new PatcherLibBaseEx("New replaced add cannot be null !!");

		_Lines.remove(lineIndex);
		_Lines.add(lineIndex, newLine);
	}

	public void replaceInLine(int lineIndex, String toReplace, String replaceWith) {
		String line = _Lines.get(lineIndex);
		_Lines.remove(lineIndex);

		_Lines.add(lineIndex, line.replaceAll(toReplace, replaceWith));
	}

	public void replaceInAllLines(String search, String newText) {
		val patt = Pattern.compile(search);

		int index = 0;
		while (true) {
			index = findFirstByRegexLine(patt, index);
			if (index >= 0) {
				// found
				String line = getLine(index);
				line = line.replaceAll(search, newText);
				replaceLine(index, line);
				index++;
			} else break;
		}
	}

	public int findFirstRegexLine(String regex, int startFromIndex) throws PatcherLibBaseEx {

		Pattern pattern = Pattern.compile(regex);

		return findFirstByRegexLine(pattern, startFromIndex);
	}

	// Expected to find -> throws if not found !
	public int findExpFirstRegexLine(String regex, int startFromIndex) throws PatcherLibBaseEx {

		Pattern pattern = Pattern.compile(regex);

		return findExpFirstByRegexLine(pattern, startFromIndex);
	}

	public int findExpFirstRegexLine(String regex, int startFromIndex, int endIndexInclusive) throws PatcherLibBaseEx {

		int index = findFirstRegexLine(regex, startFromIndex, endIndexInclusive);

		if (index < 0)
			throw new PatcherLibBaseEx("Pattern '" + regex + "' not in lines range [" + startFromIndex + "," + endIndexInclusive + "] !");
		return index;
	}

	public String loadLineByFirstRegexLine(Pattern regexPattern, int startFromIndex) throws PatcherLibBaseEx {
		int index = findExpFirstByRegexLine(regexPattern, startFromIndex);
		return getLine(index);
	}

	public String loadLineByFirstRegexLine(String regex, int startFromIndex) throws PatcherLibBaseEx {
		int index = findExpFirstRegexLine(regex, startFromIndex);
		return getLine(index);
	}

	// Finds rexeg add BETWEEN startFromIndex & endIndex - all inclusive
	public int findFirstRegexLine(String regex, int startFromIndex, int endIndexInclusive) throws PatcherLibBaseEx {

		Pattern pattern = Pattern.compile(regex);

		int index = findFirstByRegexLine(pattern, startFromIndex);

		if (index > endIndexInclusive) return -1;

		return index;
	}

	public int findFirstRegexLine(Pattern pattern) throws PatcherLibBaseEx {
		return findFirstByRegexLine(pattern, 0);
	}

	public int findFirstRegexLine(Pattern pattern, int startFromIndex) throws PatcherLibBaseEx {
		return findFirstRegexLine(pattern, startFromIndex, _Lines.size());
	}

	// Finds rexeg add BETWEEN startFromIndex & endIndex - all inclusive
	public int findFirstRegexLine(Pattern pattern, int startFromIndex, int endIndexInclusive) throws PatcherLibBaseEx {

		int index = findFirstByRegexLine(pattern, startFromIndex);

		if (index > endIndexInclusive) return -1;

		return index;
	}

	public int findFirstByRegexLine(Pattern pattern, int startFromIndex) throws PatcherLibBaseEx {

		if (pattern == null) throw new PatcherLibBaseEx("Input parameter Pattern is null");

		int indexFound = -1;
		for (int i = startFromIndex; indexFound < 0 && i < _Lines.size(); i++) {

			String line = _Lines.get(i);

			if (pattern == null) throw new PatcherLibBaseEx("Internal pattern variable is null !");

			if (line == null) {

				String x = "BREAK NOW !!";

				throw new PatcherLibBaseEx("add parameter is null, i = " + i);
			}

			Matcher matcher = pattern.matcher(line);

			if (matcher.find()) {
				indexFound = i;
			}
		}

		return indexFound;
	}

	// finds expected -> throws if not found
	public int findExpFirstByRegexLine(Pattern pattern) throws PatcherLibBaseEx {
		return findFirstByRegexLine(pattern, 0);
	}

	// finds expected -> throws if not found
	public int findExpFirstByRegexLine(Pattern pattern, int startFromIndex) throws PatcherLibBaseEx {

		int indexFound = -1;
		for (int i = startFromIndex; indexFound < 0 && i < _Lines.size(); i++) {

			String line = _Lines.get(i);

			Matcher matcher = pattern.matcher(line);

			if (matcher.find()) {
				indexFound = i;
			}
		}

		if (indexFound < 0)
			throw new PatcherLibBaseEx("Pattern '" + pattern.pattern() + "' not found from add " + startFromIndex);
		return indexFound;
	}

	public int findFirstRegexLine(String regex) throws PatcherLibBaseEx {
		return findFirstRegexLine(regex, 0);
	}

	public int findFirstRegexLine(String regex, List<Pair<Integer, Integer>> exeptRanges) throws PatcherLibBaseEx {

		if (exeptRanges == null || exeptRanges.size() == 0)
			return findFirstRegexLine(regex, 0);

		int start = 0, index = 0;

		for (Pair<Integer, Integer> range : exeptRanges) {

			index = findFirstRegexLine(regex, start, range.getKey() - 1);

			if (index >= 0) return index;    // FOUND

			// Not Found :
			start = range.getValue() + 1;
		}
		// Still not found !
		return findFirstRegexLine(regex, start);    // search to the end
	}

	// expects to find, if not - throws
	public int findExpFirstRegexLine(String regex) throws PatcherLibBaseEx {

		int index = findFirstRegexLine(regex);

		if (index < 0) throw new PatcherLibBaseEx("Regex '" + regex + "' in not found !");

		return index;
	}


	public int findFirstExactLine(String lineToFind, int startFromIndex) {

		int indexFound = -1;
		for (int i = startFromIndex; indexFound < 0 && i < _Lines.size(); i++) {

			if (lineToFind.equals(_Lines.get(i))) {
				indexFound = i;
			}
		}

		return indexFound;
	}

	public int findFirstExactLine(String lineToFind) {
		return findFirstExactLine(lineToFind, 0);
	}

	public void insertAt(int indexToInsert, String line) {

		List<String> splittedLines = new ArrayList<>(Arrays.asList(line.split(nl)));

		_Lines.addAll(indexToInsert, splittedLines);
	}

	public void insertAt(int indexToInsert, List<String> lines) {
		List<String> linesAfterSplit = new ArrayList<>();

		for (String line : lines) {
			List<String> splittedLines = new ArrayList<>(Arrays.asList(line.split(nl)));
			linesAfterSplit.addAll(splittedLines);
		}

		_Lines.addAll(indexToInsert, linesAfterSplit);

	}

	public void insertAtEnd(List<String> lines) {
		insertAt(count(), lines);
	}

	public void saveChanges(BufferedWriter bw) throws IOException {

		boolean isNewLineNeeded = false;

		for (String lineStr : _Lines) {

			if (isNewLineNeeded)
				bw.newLine();

			bw.write(lineStr);
			isNewLineNeeded = true;
		}
	}

	public int count() {
		return _Lines.size();
	}

	public void setLines(List<String> lines) {
		_Lines = lines;
	}

	public List<String> getLines() {
		return _Lines;
	}

	protected List<String> _Lines = new ArrayList<>();

	protected String nl = System.lineSeparator();
}
