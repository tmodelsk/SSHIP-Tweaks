package tm.mtwModPatcher.sship.ai;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 23.05.2017.
 */
public class SkynetCampaignAiTests extends FeatureBaseTest {

	@Test
	public void executeUpdates_Test() throws Exception {

		val testingFt = new SkynetCampaignAi(createResourcesProvider());

		initializeFeature(testingFt);
		testingFt.executeUpdates();

		assertThat(testingFt.factionAiLabelsMap.size()).isGreaterThan(10);

	}
}
