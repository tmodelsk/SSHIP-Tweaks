package tm.mtwModPatcher.lib.common.core.features.fileEntities;

import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.io.InputStream;

/**
 * Created by tomek on 28.04.2017.
 */
public class ResourceInputStreamProvider implements InputStreamProvider {
	@Override
	public InputStream getInputStream(String fullPath) {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();

		String resourcePath = fullPath.replace('\\', '/');
//		if(resourcePath.substring(0,1).equals("/"))
//			resourcePath = resourcePath.substring(1);

		InputStream is = classloader.getResourceAsStream(resourcePath);

		if(is == null) throw new PatcherLibBaseEx(Ctm.format("File {0} not found in resources!", resourcePath));

		return is;
	}
}
