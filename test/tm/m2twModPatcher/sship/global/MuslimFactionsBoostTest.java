package tm.m2twModPatcher.sship.global;

import lombok.val;
import org.junit.Test;
import tm.m2twModPatcher.FeatureBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class MuslimFactionsBoostTest extends FeatureBaseTest {

	@Test
	public void executeUpdates_ShouldWork() throws Exception {
		val feature = new MuslimFactionsBoost();
		initializeFeature(feature);

		feature.executeUpdates();

		val updatedFiles = feature.getFilesUpdated();
		assertThat(updatedFiles).isNotEmpty();
	}
}
