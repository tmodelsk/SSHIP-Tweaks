package tm.m2twModPatcher.lib.engines;

import lombok.Getter;

/**
 * Created by tomek on 10.04.2017.
 */
public class ConsoleLogger {

	public void writeLine(String line) {
		writer.write(line + System.lineSeparator());
	}

	@Getter
	private ConsoleLogWriter writer;

	public ConsoleLogger(ConsoleLogWriter writer) {
		this.writer = writer;
	}
}
