package tm.m2twModPatcher.lib.common.core.features;

import lombok.val;
import tm.common.Ctm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomek on 2016-04-11.
 */
public class OverrideCopyTask extends OverrideTask {

	public List<String> getAffectedFilesRelativePaths() throws IOException {
		String featureSrcPath = RootPath + "\\" + DirectoryName;

		List<String> pathsFullList = new ArrayList<String>();

		Files.walk(Paths.get(featureSrcPath)).forEach(filePath -> {

			if (Files.isRegularFile(filePath)) {
				pathsFullList.add(filePath.toString());
			}
		});

		// clean up rootPath from paths
		List<String> pathsRelativeList = new ArrayList<String>();

		for (String path : pathsFullList) {
			path = path.replace(featureSrcPath + "\\", "");

			if (!path.isEmpty())
				pathsRelativeList.add(path);
		}

		return pathsRelativeList;
	}


	@Override
	public int hashCode() {
		return DirectoryName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;

		val objTyped = Ctm.as(OverrideCopyTask.class, obj);
		if (objTyped == null) return false;
		return DirectoryName.equals(objTyped.DirectoryName);
	}

	public OverrideCopyTask(String directoryName) {
		super();
		DirectoryName = directoryName;
	}


	public String DirectoryName;
}
