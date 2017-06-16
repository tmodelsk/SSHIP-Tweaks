package tm.mtwModPatcher.sship.global;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.lib.managers.UnitsManager;

/**
 * Created by tomek on 07.06.2017.
 */
public class FightForSurvivalTests extends FeatureBaseTest {

	@Test
	public void createExecuteUpdates_ShouldExecute() throws Exception {
		val ft = new FightForSurvival(new UnitsManager());

		initializeFeature(ft);

		ft.executeUpdates();
	}
}
