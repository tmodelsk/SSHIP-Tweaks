package tm.mtwModPatcher;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.ResourceInputStreamProvider;
import tm.mtwModPatcher.lib.engines.ConsoleLogWriterNull;
import tm.mtwModPatcher.lib.engines.ConsoleLogger;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;

/**
 * Unit Testing base class for Features testing
 */
public class FeatureBaseTest {

	protected void initializeFeature(Feature feature) {

		val featuresContainer = new FeatureList();
		feature.setFeaturesContainer(featuresContainer);

		val fileEntityFactory = createFileEntityFactory();
		val consoleLogger = createConsoleLogger();

		feature.initialize(fileEntityFactory, consoleLogger);
	}

	protected FileEntityFactory createFileEntityFactory() {
		val inputStreamProvider = createInputStreamProvider();
		val fileEntityFactory = new FileEntityFactory();
		fileEntityFactory.setInputStreamProvider(inputStreamProvider);
		fileEntityFactory.setRootPath("test-resources/modFiles");

		return fileEntityFactory;
	}

	protected ConsoleLogger createConsoleLogger() {
		val nullWriter = new ConsoleLogWriterNull();
		val consoleLogger = new ConsoleLogger(nullWriter);

		return consoleLogger;
	}

	protected InputStreamProvider createInputStreamProvider() {
		return new ResourceInputStreamProvider();
	}
}
