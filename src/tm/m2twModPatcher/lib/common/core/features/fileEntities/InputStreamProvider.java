package tm.m2twModPatcher.lib.common.core.features.fileEntities;

import java.io.InputStream;

/**
 * Created by tomek on 28.04.2017.
 */
public interface InputStreamProvider {

	InputStream get(String fullPath);

}
