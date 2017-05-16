package tm.mtwModPatcher.lib.fileEntities.data.common;

import lombok.val;
import org.junit.Test;

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
}
