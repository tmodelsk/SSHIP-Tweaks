package tm.mtwModPatcher.sship.features.global;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.sship.features.global.catholicIberiaReworked.CatholicIberiaReworked;

public class CatholicIberiaReworked_Tests extends FeatureBaseTest {

	@Test
	public void createExecuteUpdates_ShouldExecute() throws Exception {
		val ft = new CatholicIberiaReworked();

		initializeFeature(ft);

		ft.executeUpdates();
	}
}
