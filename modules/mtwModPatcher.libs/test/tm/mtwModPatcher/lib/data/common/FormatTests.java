package tm.mtwModPatcher.lib.data.common;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Created by tomek on 02.11.2017 */
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
}
