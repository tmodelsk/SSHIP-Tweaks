package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;

/**
 * Created by tomek on 13.08.2017.
 */
public class MercenariesCostsTests extends FeatureBaseTest {

	@Test
	public void createExecuteUpdates_ShouldWork() throws Exception {
		val ft = new MercenariesCosts();
		initializeFeature(ft);

		ft.executeUpdates();
	}
}
