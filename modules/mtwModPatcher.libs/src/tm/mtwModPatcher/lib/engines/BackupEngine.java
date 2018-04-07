package tm.mtwModPatcher.lib.engines;

import org.apache.commons.io.FileUtils;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Tomek on 2016-04-13.
 */
public class BackupEngine {

	public void backupPaths(Set<String> relativePaths) throws IOException {

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

	@SuppressWarnings("unused")
	public void backup(Set<FileEntity> files) throws IOException {
		backup(files, null);
	}

	public void backup(Set<FileEntity> files, Set<String> relativePathsToOmmit) throws IOException {
		Set<String> paths = new HashSet<>();

		Set<String> relativePathsToOmmitLowered = relativePathsToOmmit.stream().map( p -> p.toLowerCase()).collect(Collectors.toSet());

		for( FileEntity file : files) {

			if(relativePathsToOmmit == null || !relativePathsToOmmitLowered.contains(file.filePath.toLowerCase()))
				paths.add(file.filePath);
		}

		backupPaths(paths);
	}

	public void restoreBackup() throws IOException {
		consoleLogger.writeLine("BackupEngine: Restoring all files from ["+BackupRootPath+"] into ["+DestinationRootPath+"]");
		FileUtils.copyDirectory(new File(BackupRootPath), new File(DestinationRootPath));
		consoleLogger.writeLine("BackupEngine: Backup is restored.");
	}

	public void cleanBackup() throws IOException {
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
