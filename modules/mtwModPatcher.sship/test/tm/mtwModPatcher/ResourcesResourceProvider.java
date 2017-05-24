package tm.mtwModPatcher;

import lombok.Getter;
import tm.mtwModPatcher.lib.common.core.features.ResourcesProvider;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;

import java.io.InputStream;

/** Resources provider from testing embedded resources */
public class ResourcesResourceProvider implements ResourcesProvider {
	@Override
	public String getOverridesPath() {
		return "test-resources/overrides";
	}

	@Override
	public InputStream getInputStream(String fullPath) {
		return inputStreamProvider.getInputStream(fullPath);
	}

	@Getter
	private InputStreamProvider inputStreamProvider;

	public ResourcesResourceProvider(InputStreamProvider inputStreamProvider) {
		this.inputStreamProvider = inputStreamProvider;
	}
}
