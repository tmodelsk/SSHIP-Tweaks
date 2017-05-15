package tm.m2twModPatcher.lib.common.core.features.fileEntities;

import tm.common.runtimeExceptions.IOEx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by tomek on 28.04.2017.
 */
public class FileInputStreamProvider implements InputStreamProvider {
	@Override
	public InputStream get(String fullPath) {

		try {
			return new FileInputStream(fullPath);
		} catch (FileNotFoundException e) {
			throw new IOEx(e.getMessage(), e);
		}
	}
}
