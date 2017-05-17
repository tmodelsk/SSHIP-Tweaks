package tm.mtwModPatcher.lib.common.entities;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 30.04.2017.
 */
public class FactionsDefsTests {

	@Test
	public void getFactionInfos_ShouldReturnAllList() {
		val factions = FactionsDefs.getFactionInfos();

		Assertions.assertThat(factions).isNotEmpty();
	}
}
