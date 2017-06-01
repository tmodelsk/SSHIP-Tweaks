package tm.mtwModPatcher.lib.common;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.lib.data.common.Format;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 22.04.2017.
 */
public class FormatTests {

	@Test
	public void round() {
		val res = Format.round(Math.PI, 2);
		assertThat(res).isEqualTo(3.14);
	}

	@Test
	public void toString_0_1_ShouldBeExact() {
		double d = 0.1;

		val str = Format.toString(d);

		assertThat(str).isEqualTo("0.1");
	}

	@Test
	public void toString_0_0001_ShouldBeExact() {
		double d = 0.0001;

		val str = Format.toString(d);

		assertThat(str).isEqualTo("0.0001");
	}

	@Test
	public void toString_1_ShouldBeExact() {
		double d = 1;

		val str = Format.toString(d);

		assertThat(str).isEqualTo("1");
	}
}
