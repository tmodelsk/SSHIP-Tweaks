package tm.mtwModPatcher.lib.engines;

import lombok.val;
import org.apache.commons.io.FileUtils;
import tm.common.Ctm;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.NotUniqueEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.OverrideTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**  */
public class OverrideEngine {

	public void overridePaths(List<OverrideTask> overrideTasks) throws IOException, PatcherLibBaseEx {
		validateTasks(overrideTasks);
		for (OverrideTask overrideTask : overrideTasks) {

			if (overrideTask instanceof OverrideCopyTask) {
				OverrideCopyTask copyTask = (OverrideCopyTask) overrideTask;
				runOverrideCopyTask(copyTask);
			} else if (overrideTask instanceof OverrideDeleteFilesTask) {
				OverrideDeleteFilesTask deleteTask = (OverrideDeleteFilesTask) overrideTask;
				runOverrideDeleteTask(deleteTask);
			} else throw new PatcherLibBaseEx("OverrideTask Type is not supported!");
		}
	}

	private void validateTasks(List<OverrideTask> overrideTasks) throws IOException {

		val copyPathsGlobal = new ArrayUniqueList<String>();

		for (OverrideTask overrideTask : overrideTasks) {

			if (overrideTask instanceof OverrideCopyTask) {
				val copyTask = (OverrideCopyTask) overrideTask;
				val paths = copyTask.getAffectedFilesRelativePaths();

				for (val path : paths)
					try {
						copyPathsGlobal.add(path);    // throw when not unique
					} catch (NotUniqueEx notUniqueEx) {
						throw new PatcherLibBaseEx(Ctm.msgFormat("Override copy path {0} already exists, override dir {1} !", path, copyTask.DirectoryName), notUniqueEx);
					}
			} else if (overrideTask instanceof OverrideDeleteFilesTask) {
			} else throw new PatcherLibBaseEx("OverrideTask Type is not supported!");
		}
	}

	private void runOverrideDeleteTask(OverrideDeleteFilesTask deleteTask) throws IOException {
		List<String> relativePaths = deleteTask.getAffectedFilesRelativePaths();

		for (String relativePath : relativePaths) {
			String destPath = DestinationRootPath + "\\" + relativePath;

			File fileDest = new File(destPath);
			consoleLogger.writeLine("OverrideEngine: Delete task, deleting [" + fileDest + "]");
			FileUtils.deleteQuietly(fileDest);
		}
	}

	private void runOverrideCopyTask(OverrideCopyTask copyTask) throws IOException {
		List<String> relativePaths = copyTask.getAffectedFilesRelativePaths();

		for (String relativePath : relativePaths) {
			String srcPath = OverrideRootPath + "\\" + copyTask.DirectoryName + "\\" + relativePath;
			String destPath = DestinationRootPath + "\\" + relativePath;

			File fileSource = new File(srcPath);
			File fileDest = new File(destPath);

			consoleLogger.writeLine("OverrideEngine: Copy task, copying [" + fileSource + "] into [" + fileDest + "]");
			FileUtils.copyFile(fileSource, fileDest);
		}
	}

	public String DestinationRootPath;
	public String OverrideRootPath;

	private ConsoleLogger consoleLogger;

	public OverrideEngine(ConsoleLogger consoleLogger) {
		this.consoleLogger = consoleLogger;
	}
}
