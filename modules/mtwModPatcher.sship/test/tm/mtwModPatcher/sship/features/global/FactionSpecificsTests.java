package tm.mtwModPatcher.sship.features.global;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;

public class FactionSpecificsTests extends FeatureBaseTest {

	@Test
	public void createExecuteUpdates_ShouldExecute() throws Exception {
		val ft = new FactionsSpecifics(true);

		initializeFeature(ft);

		ft.executeUpdates();
	}
}
