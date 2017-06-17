package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;

public class SnowStormNerfedTests extends FeatureBaseTest {

	@Test
	public void execute_ShouldWork() throws Exception {
		val ft = new SnowStormNerfed();
		initializeFeature(ft);

		ft.executeUpdates();
	}
}
