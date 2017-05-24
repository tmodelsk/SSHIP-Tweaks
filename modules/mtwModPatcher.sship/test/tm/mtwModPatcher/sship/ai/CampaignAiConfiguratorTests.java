package tm.mtwModPatcher.sship.ai;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;

/**
 * Created by tomek on 23.05.2017.
 */
public class CampaignAiConfiguratorTests extends FeatureBaseTest {

	@Test
	public void executeUpdates_Test() throws Exception {

		val testingFt = new TestingCampaignAiFt(createResourcesProvider());

		initializeFeature(testingFt);

		testingFt.executeUpdates();

	}

}
