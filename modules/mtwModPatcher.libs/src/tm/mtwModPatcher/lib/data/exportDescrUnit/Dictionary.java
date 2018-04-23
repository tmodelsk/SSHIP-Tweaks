package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.util.regex.Pattern;

public class Dictionary {

	public String value;
	public String comment;

	public static Dictionary parse(String eduStr) {
		val dict = new Dictionary();

		val m = parseRegex.matcher(eduStr);
		if(m.matches()) {
			dict.value = m.group(1);
			val comment = m.group(2);
			dict.comment = comment != null && !comment.isEmpty() ? comment : null;
		}
		else throw new PatcherLibBaseEx("Unable to parse: "+eduStr);

		return dict;
	}

	private static Pattern parseRegex = Pattern.compile("(\\s*\\w+)(.*)");

	public String toEduString() {
		String eduStr = value;

		if(comment != null)
			eduStr += comment;

		return eduStr;
	}

	public Dictionary(String value, String comment) {
		this.value = value;
		this.comment = comment;
	}

	public Dictionary() {
	}
}
