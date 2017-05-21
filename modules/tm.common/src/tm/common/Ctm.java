package tm.common;

import lombok.val;

import java.text.MessageFormat;
import java.util.Collection;

/** Common TM - most used commons by TM  */
public class Ctm {

	// C# as
	public static <T> T as(Class<T> t, Object o) {
		return t.isInstance(o) ? t.cast(o) : null;
	}

	public static String getWorkingDirectory() {
		String dirStr = System.getProperty("user.dir");

		return dirStr;
	}

	/** Message Format like 'Some String {0} / {1}' with arguments */
	public static String msgFormat(String s, Object... args) {

		val strReplaces = s.replace("'" , "''");

		return new MessageFormat(strReplaces).format(args);
	}
	public static String msgFormat(String s) {
		return s;
	}


	public static String toCsv(Collection coll) {
		val strB = new StringBuilder();

		boolean isFirst = true;
		for (val item : coll) {

			if(!isFirst) strB.append(',');

			if(item != null) strB.append(item.toString());
			else strB.append("null");

			isFirst=false;
		}

		return strB.toString();
	}
}
