package tm.mtwModPatcher.lib.common.core.features.fileEntities;

import tm.common.runtimeExceptions.IOEx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by tomek on 28.04.2017.
 */
public class FileInputStreamProvider implements InputStreamProvider {
	@Override
	public InputStream getInputStream(String fullPath) {

		try {
			return new FileInputStream(fullPath);
		} catch (FileNotFoundException e) {
			throw new IOEx(e.getMessage(), e);
		}
	}
}
