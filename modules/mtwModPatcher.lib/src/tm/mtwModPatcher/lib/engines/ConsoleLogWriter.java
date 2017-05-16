package tm.mtwModPatcher.lib.engines;

/**
 * Created by tomek on 10.04.2017.
 */
public interface ConsoleLogWriter {

	void write(StringBuilder strB);

	void write(String str);

	void scrollToEnd();
}
