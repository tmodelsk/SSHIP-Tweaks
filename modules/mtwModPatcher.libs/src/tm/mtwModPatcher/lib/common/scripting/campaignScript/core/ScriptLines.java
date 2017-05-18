package tm.mtwModPatcher.lib.common.scripting.campaignScript.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomek on 2016-11-17.
 */
public class ScriptLines {

	public void line(String line) {
		_Lines.add(line);
	}

	public void add(ScriptLines linesToAdd, int indentSize) {

		String indents = "";
		for (int i = 1; i <= indentSize; i++)
			indents += _Indent;

		for (String line : linesToAdd.getLines()) {

			line(indents + line);
		}

	}

	public void newLine() {
		_Lines.add(nl);
	}

	public String getLine(int index) {
		return _Lines.get(index);
	}

	public String firstLine() {
		return getLine(0);
	}

	public List<String> getLines() {
		return _Lines;
	}

	private List<String> _Lines = new ArrayList<>();
	private String nl = System.lineSeparator();
	private String _Indent = "	";
}
