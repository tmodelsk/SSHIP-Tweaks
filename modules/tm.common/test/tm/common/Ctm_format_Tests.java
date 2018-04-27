package tm.common;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tm.common.Ctm.format;

public class Ctm_format_Tests {

	@Test
	public void justString() {
		assertThat(format("aaa")).isEqualTo("aaa");
	}

	@Test
	public void singleTag() {
		assertThat(format("{0}", "aaa")).isEqualTo("aaa");
	}

	@Test
	@Ignore("TODO: !!")
	public void doubleBrackets() {
		assertThat(format("{{0}}", "aaa")).isEqualTo("{aaa}");
	}

}
