package tm.m2twModPatcher.sship.armyUnits;

import lombok.val;
import org.junit.Test;
import tm.m2twModPatcher.FeatureBaseTest;

/**
 * Created by tomek on 01.05.2017.
 */
public class PeasantsRecruitmentRemovedTest extends FeatureBaseTest {

	@Test
	public void createExecuteUpdates_ShouldWork() throws Exception {
		val ft = new PeasantsRecruitmentRemoved();
		initializeFeature(ft);

		ft.executeUpdates();
	}

}
