package tm.m2twModPatcher.lib.common.core.features.fileEntities;

import lombok.val;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LinesProcessorTests {

	@Test
	public void replaceInAllLines_ShouldReplace() {

		val lines = new LinesProcessor();
		lines.insertAtEnd(Arrays.asList("AA aa BB bb CC cc", "XX xx YY yy ZZ zz", "1234567890" , "abcdefghijk"));

		lines.replaceInAllLines("b" , "R");

		assertThat(lines.getLine(0)).isEqualTo("AA aa BB RR CC cc");
		assertThat(lines.getLine(1)).isEqualTo("XX xx YY yy ZZ zz");
		assertThat(lines.getLine(2)).isEqualTo("1234567890");
		assertThat(lines.getLine(3)).isEqualTo("aRcdefghijk");
	}
}
