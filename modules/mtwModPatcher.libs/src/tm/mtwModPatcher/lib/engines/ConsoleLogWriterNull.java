package tm.mtwModPatcher.lib.engines;

/** Writes enttries into /dev/null - meaning nowhere. Upgrade - writes into System.out console  */
public class ConsoleLogWriterNull implements ConsoleLogWriter {
	@Override
	public void write(StringBuilder strB) {
		System.out.println(strB);
	}

	@Override
	public void write(String str) {
		System.out.println(str);
	}

	@Override
	public void scrollToEnd() {

	}
}
