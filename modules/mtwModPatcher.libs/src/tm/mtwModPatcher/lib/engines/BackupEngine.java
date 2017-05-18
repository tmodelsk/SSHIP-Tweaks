package tm.mtwModPatcher.lib.engines;

import org.apache.commons.io.FileUtils;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Tomek on 2016-04-13.
 */
public class BackupEngine {

	public void BackupPaths(List<String> relativePaths) throws IOException {

		for (String relativePath : relativePaths) {

			String srcPath = DestinationRootPath + "\\" + relativePath;
			String backupPath = BackupRootPath + "\\" + relativePath;

			File fileSource = new File(srcPath);
			File fileDest = new File(backupPath);

			if(!fileDest.exists() && fileSource.exists()) {
				FileUtils.copyFile(fileSource, fileDest);
				consoleLogger.writeLine("BackupEngine: Created backup of ["+fileSource+"] to ["+fileDest+"]");
			}
		}
	}

	public void BackupPaths(Set<FileEntity> files) throws IOException {
		List<String> paths = new ArrayList<>();

		for( FileEntity file : files) {
			paths.add(file.filePath);
		}

		BackupPaths(paths);
	}

	public void RestoreBackup() throws IOException {
		consoleLogger.writeLine("BackupEngine: Restoring all files from ["+BackupRootPath+"] into ["+DestinationRootPath+"]");
		FileUtils.copyDirectory(new File(BackupRootPath), new File(DestinationRootPath));
		consoleLogger.writeLine("BackupEngine: Backup is restored.");
	}

	public void CleanBackup() throws IOException {
		consoleLogger.writeLine("BackupEngine: Cleaning backup directory ["+BackupRootPath+"]");
		FileUtils.cleanDirectory(new File(BackupRootPath));
		consoleLogger.writeLine("BackupEngine: Backup is purged.");
	}

	public String BackupRootPath;
	public String DestinationRootPath;

	private ConsoleLogger consoleLogger;

	public BackupEngine(ConsoleLogger consoleLogger) {
		this.consoleLogger = consoleLogger;
	}
}
