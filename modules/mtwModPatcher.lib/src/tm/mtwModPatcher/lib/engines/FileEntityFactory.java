package tm.mtwModPatcher.lib.engines;

import lombok.Getter;
import lombok.Setter;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileEntity;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Returns singleton FileEntities
 */
public class FileEntityFactory {

	@Getter @Setter public String rootPath;

	protected static Map<String, FileEntity> FileEntitiesLoaded = new HashMap<>();

	public <T extends FileEntity> T getFile(Class<T> clz) throws Exception {

		T file = clz.newInstance();

		if (inputStreamProvider != null) file.setInputStreamProvider(inputStreamProvider);

		if (FileEntitiesLoaded.containsKey(file.filePath))
			file = (T) FileEntitiesLoaded.get(file.filePath);
		else {
			file.rootPath = rootPath;
			file.load();

			FileEntitiesLoaded.put(file.filePath, file);
		}


		return file;
	}

	public void reset() {
		FileEntitiesLoaded.clear();
	}

	@Getter @Setter private InputStreamProvider inputStreamProvider;

	public FileEntityFactory() {
	}
}
