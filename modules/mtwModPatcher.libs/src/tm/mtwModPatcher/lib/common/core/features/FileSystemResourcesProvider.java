package tm.mtwModPatcher.lib.common.core.features;

import lombok.Getter;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.io.InputStream;

/** Resources provider from FileSystem */
public class FileSystemResourcesProvider implements ResourcesProvider {
	@Override
	public String getOverridesPath() {
		return ConfigurationSettings.OverrideRootPath();
	}

	@Override
	public InputStream getInputStream(String fullPath) {
		return inputStreamProvider.getInputStream(fullPath);
	}

	@Getter
	private InputStreamProvider inputStreamProvider;

	public FileSystemResourcesProvider(InputStreamProvider inputStreamProvider) {
		this.inputStreamProvider = inputStreamProvider;
	}
}
