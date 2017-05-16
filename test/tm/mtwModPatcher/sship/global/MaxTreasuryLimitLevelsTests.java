package tm.mtwModPatcher.sship.global;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MaxTreasuryLimitLevelsTests {

	@Test
	public void getLevels_ShouldReturn() {
		val levelsManager = new MaxTreasuryLimitLevels();
		val levels = levelsManager.getLevels(150000, 5);

		assertThat(levels).isNotNull().isNotEmpty();
	}

}
