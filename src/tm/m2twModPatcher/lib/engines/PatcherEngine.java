package tm.m2twModPatcher.lib.engines;

import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.FileEntity;
import tm.m2twModPatcher.lib.common.core.features.OverrideTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Tomek on 2016-04-11.
 */
public class PatcherEngine {

	public void Patch(List<Feature> featureFullList) throws Exception {
		consoleLogger.writeLine("PatcherEngine: Patching process has started ... ");
		InitializeFeatures(featureFullList);
		_FileEntityFactory.reset();

		consoleLogger.writeLine("PatcherEngine: Restore & clean Backup started ...");
		BackupEngine backupEngine = new BackupEngine(consoleLogger);
		backupEngine.BackupRootPath = BackupRootPath;
		backupEngine.DestinationRootPath = DestinationRootPath;
		backupEngine.RestoreBackup();
		backupEngine.CleanBackup();
		consoleLogger.writeLine("PatcherEngine: Restore & clean Backup done");

		List<Feature> featureList = new ArrayList<>();
		// ### Only Enabled Features ###
		featureList.addAll(featureFullList.stream().filter(feature -> feature.isEnabled()).collect(Collectors.toList()));

		consoleLogger.writeLine("PatcherEngine: Found "+featureList.size()+" features to apply");

		// ## Do Overrides Tasks ##
		consoleLogger.writeLine("PatcherEngine: Override tasks execution started ...");
		List<String> overrideRelativePaths = new ArrayList<>();
		val overrideTasks = new ArrayUniqueList<OverrideTask>();

		for (Feature baseFeature : featureList) {
			// # perform backup for override tasks
			if(baseFeature.getOverrideTasks() != null&& baseFeature.getOverrideTasks().size() > 0) {
				for (OverrideTask overrideTask : baseFeature.getOverrideTasks()) {
					overrideTask.RootPath = OverrideRootPath;
					overrideTasks.add(overrideTask);

					overrideRelativePaths.addAll(overrideTask.getAffectedFilesRelativePaths());
				}

			}
		}
		consoleLogger.writeLine("PatcherEngine: Found "+overrideTasks.size()+" override tasks");

		// ## Backup & Execute Overrides ##
		if(overrideTasks.size() > 0) {
			consoleLogger.writeLine("PatcherEngine: Backup files to override started ... ");

			backupEngine.BackupPaths(overrideRelativePaths);
			consoleLogger.writeLine("PatcherEngine: Backup files to override done");

			consoleLogger.writeLine("PatcherEngine: Override files started ... ");
			OverrideEngine overrideEngine = new OverrideEngine(consoleLogger);
			overrideEngine.DestinationRootPath = DestinationRootPath;
			overrideEngine.OverrideRootPath = OverrideRootPath;
			overrideEngine.overridePaths(overrideTasks);
			consoleLogger.writeLine("PatcherEngine: Override files done");
		}
		consoleLogger.writeLine("PatcherEngine: Override tasks execution done");

		// ## Execute Features ##
		consoleLogger.writeLine("PatcherEngine: Apply Features started ... ");
		Set<FileEntity> filesToUpdate = new HashSet<>();
		for (Feature feature : featureList) {

			consoleLogger.writeLine("PatcherEngine: Feature ["+feature.Name+"] execution started ...");
			feature.preExecuteUpdates();
			feature.executeUpdates();
			val filesUpdated = feature.getFilesUpdated();
			filesToUpdate.addAll(filesUpdated);
			consoleLogger.writeLine("PatcherEngine: Feature ["+feature.Name+"] execution done, updated ("+filesUpdated.size()+") files");
		}
		consoleLogger.writeLine("PatcherEngine: Apply Features done");

		// ## Backup only changed but NOT Overrided Files !! ##
		consoleLogger.writeLine("PatcherEngine: Backup updated files starting ...");
		val filesToBackup = new ArrayList<FileEntity>();
		for(val fileToUpd : filesToUpdate) {

			if( ! overrideTasks.contains(fileToUpd.filePath))
				filesToBackup.add(fileToUpd);
		}

		// ## Backup files to be updated ##
		backupEngine.BackupPaths( filesToUpdate );
		consoleLogger.writeLine("PatcherEngine: Backup updated files done");

		// ## Save Features changes to disk ##
		consoleLogger.writeLine("PatcherEngine: Save updated files starting ...");
		for (FileEntity file : filesToUpdate) {
			file.saveChanges();
			consoleLogger.writeLine("PatcherEngine: Saved updated file ["+file.getFullPath()+"]");
		}
		consoleLogger.writeLine("PatcherEngine: Save updated files done");

		consoleLogger.writeLine("PatcherEngine: Patching process finished. Selected Features applied.");
	}

	protected void InitializeFeatures(List<Feature> featureList) {

	}

	public void Initialize() {
		consoleLogger.writeLine("PatcherEngine initialization started ...");
		//fileEntityFactory = new FileEntityFactory();
		_FileEntityFactory.rootPath = DestinationRootPath;
		consoleLogger.writeLine("PatcherEngine initialization done");
	}

	protected ConsoleLogger consoleLogger;

	protected FileEntityFactory _FileEntityFactory;

	public String OverrideRootPath;
	public String BackupRootPath;
	public String DestinationRootPath;

	public PatcherEngine(ConsoleLogger consoleLogger, FileEntityFactory fileEntityFactory) {
		this.consoleLogger = consoleLogger;
		_FileEntityFactory = fileEntityFactory;
	}
}
