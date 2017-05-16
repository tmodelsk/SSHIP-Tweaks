package tm.mtwModPatcher;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.managers.UnitsManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonMetaManager;
import tm.mtwModPatcher.sship.SsHipFeatures;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 28.04.2017.
 */
public class AllFeaturesTest extends FeatureBaseTest {

	@Test
	public void allFeature_Configure_ShouldReturnList() throws Exception {
		val fileEntityFactory = createFileEntityFactory();
		val consoleLogger = createConsoleLogger();

		val garrisonMetaManager = new GarrisonMetaManager(fileEntityFactory);
		val garrisonManager = new GarrisonManager(garrisonMetaManager);
		val unitsManager = new UnitsManager();

		val sshipFeature = new SsHipFeatures(garrisonManager, unitsManager, createInputStreamProvider(), fileEntityFactory, consoleLogger);
		val features = sshipFeature.configureFeatures();

		assertThat(features).isNotNull();
		val ftList = features.getFeaturesList();

		assertThat(ftList).isNotEmpty();
	}

	@Test
	public void allFeature_Configure_Execute_ShouldWork() throws Exception {

		val fileEntityFactory = createFileEntityFactory();
		val consoleLogger = createConsoleLogger();

		val garrisonMetaManager = new GarrisonMetaManager(fileEntityFactory);
		val garrisonManager = new GarrisonManager(garrisonMetaManager);
		val unitsManager = new UnitsManager();

		//ConfigurationSettings.OverrideRootPath = 	"test-resources/overrides";
												//   test-resources\overrides\SelectHeirScript\
		ConfigurationSettings.setDevOverrideRootPath("test-resources/overrides");

		val sshipFeature = new SsHipFeatures(garrisonManager, unitsManager, createInputStreamProvider(), fileEntityFactory, consoleLogger);
		val features = sshipFeature.configureFeatures();

		for (val ft : features.getFeaturesList()) {
			ft.executeUpdates();
		}
	}

}
