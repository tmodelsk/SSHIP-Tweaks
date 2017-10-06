package tm.mtwModPatcher.sship.features.map;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.sship.features.global.rimlandHeartland.RimlandHeartland;

/**
 * Created by tomek on 24.06.2017.
 */
public class RimlandHeartlandTests extends FeatureBaseTest {

	@Test
	public void executeUpd() throws Exception {
		val ft = new RimlandHeartland();
		initializeFeature(ft);

		ft.executeUpdates();
	}
}
