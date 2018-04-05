package tm.mtwModPatcher.app;

import lombok.Getter;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.engines.ConsoleLogger;
import tm.mtwModPatcher.lib.engines.PatcherEngine;

import javax.swing.*;
import java.util.List;

public class ApplyFeaturesWorker extends SwingWorker<Integer, String> {

	@Override
	protected Integer doInBackground() throws Exception {
		int result = 0;

		try {
			patcherEngine.patch(featureList, PatcherAppVersion.Version);
			result=1;
		}
		catch (Exception ex) {
			consoleLogger.writeLine(Ctm.msgFormat("Unhandled Error: {0}, stopped", ex.getMessage()));
			exception = ex;
		}

		return result;
	}

	@Getter
	private Exception exception;

	private PatcherEngine patcherEngine;
	private FeatureList featureList;

	private ConsoleLogger consoleLogger;

	public ApplyFeaturesWorker(PatcherEngine patcherEngine, FeatureList featureList, ConsoleLogger consoleLogger) {
		this.patcherEngine = patcherEngine;
		this.featureList = featureList;
		this.consoleLogger = consoleLogger;
	}
}
