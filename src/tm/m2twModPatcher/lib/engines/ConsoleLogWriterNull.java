package tm.m2twModPatcher.lib.engines;

/**
 * Writes enttries into /dev/null - meaning nowhere
 */
public class ConsoleLogWriterNull implements ConsoleLogWriter {
	@Override
	public void write(StringBuilder strB) {

	}

	@Override
	public void write(String str) {

	}

	@Override
	public void scrollToEnd() {

	}
}
