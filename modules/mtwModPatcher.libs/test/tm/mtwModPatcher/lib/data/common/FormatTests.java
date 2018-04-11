package tm.mtwModPatcher.lib.data.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormatTests {

	@Test
	public void integer(){
		assertThat(Format.toString(1.0)).isEqualTo("1");
	}

	@Test
	public void double1(){
		assertThat(Format.toString(1.1)).isEqualTo("1.1");
	}

	@Test
	public void integerminimumPrecision3(){
		assertThat(Format.toString(1.0 , 3)).isEqualTo("1.000");
	}

	@Test
	public void double1MinimumPrecision1(){
		assertThat(Format.toString(1.1 , 1)).isEqualTo("1.1");
	}

	@Test
	public void double1MinimumPrecision3(){
		assertThat(Format.toString(1.1 , 3)).isEqualTo("1.100");
	}

	@Test
	public void doubleUltraMinimum_ShouldBeEqual() {
		assertThat(Format.toString(0.000001)).isEqualTo("0.000001");
	}

	@Test
	public void doubleUltraMinimum_ForcedPrecision_ShouldBeEqual() {
		assertThat(Format.toString(0.000001, 6)).isEqualTo("0.000001");
	}
}
