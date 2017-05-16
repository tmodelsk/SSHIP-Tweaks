package tm.mtwModPatcher.lib.managers;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import tm.m2twModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.DescrStratSectioned;

import static org.assertj.core.api.Assertions.assertThat;

public class SettlementManagerTests extends FeatureBaseTest {

	@Test
	public void getAllSettlements_ShouldReturn() throws Exception {
		val fileEntityFactory = createFileEntityFactory();

		val settlementManager = new SettlementManager(
				fileEntityFactory.getFile(DescrStratSectioned.class),
				fileEntityFactory.getFile(DescrRegions.class));

		val settlements = settlementManager.getAllSettlements();

		Assertions.assertThat(settlements).isNotEmpty();

		for(val settl : settlements) {

			try {
				Assertions.assertThat(settl.Position).isNotNull();
			}
			catch (AssertionError e) {
				throw new Exception(e.getMessage() + " in Settlement.Name " + settl.Name);
			}

		}
	}

}
