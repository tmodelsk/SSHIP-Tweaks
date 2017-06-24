package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;

/**
 * Created by tomek on 22.06.2017.
 */
public class VeryHugeUnitSizeTests extends FeatureBaseTest {

	@Test
	public void executeUpdates() throws Exception {
		val ft = new VeryHugeUnitSize();
		initializeFeature(ft);

		ft.executeUpdates();
	}
}
