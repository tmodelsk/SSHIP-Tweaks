package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;

/** Created by tomek on 29.10.2017. */
public class CavalryNerfedTests extends FeatureBaseTest {

	@Test
	public void createExecuteUpdates_ShouldWork() throws Exception {
		val cavalryNerfed = new CavalryNerfed();
		initializeFeature(cavalryNerfed);

		val unitBugFixes = new UnitsBugFixes();
		initializeFeature(unitBugFixes);

		unitBugFixes.executeUpdates();
		cavalryNerfed.executeUpdates();
	}
}
