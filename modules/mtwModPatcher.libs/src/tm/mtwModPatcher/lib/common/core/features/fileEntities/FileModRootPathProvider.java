package tm.mtwModPatcher.lib.common.core.features.fileEntities;

import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

/**
 * Created by tomek on 23.05.2017.
 */
public class FileModRootPathProvider implements ModRootPathProvider {
	@Override
	public String getPath() {
		return ConfigurationSettings.DestinationRootPath();
	}
}
