package tm.m2twModPatcher.sship.armyUnits;

import lombok.val;
import org.junit.Test;
import tm.m2twModPatcher.FeatureBaseTest;

public class SnowStormNerfedTests extends FeatureBaseTest {

	@Test
	public void execute_ShouldWork() throws Exception {
		val ft = new SnowStormNerfed();
		initializeFeature(ft);

		ft.executeUpdates();
	}
}
