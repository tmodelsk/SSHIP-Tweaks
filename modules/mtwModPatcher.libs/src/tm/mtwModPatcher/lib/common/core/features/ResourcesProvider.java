package tm.mtwModPatcher.lib.common.core.features;

import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;

/** Provider methods for Resources for sources DI substitions (FileSystem , Testing Resources, etc)*/
public interface ResourcesProvider extends InputStreamProvider {

	InputStreamProvider getInputStreamProvider();

	String getOverridesPath();
}
