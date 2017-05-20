package tm.mtwModPatcher.lib.engines;

import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileEntity;
import tm.mtwModPatcher.lib.common.core.features.OverrideTask;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**  */
public class PatcherEngine {

	private BackupEngine backupEngine;

	private void restoreCleanBackup() throws IOException {
		consoleLogger.writeLine("PatcherEngine: Restore & clean Backup started ...");
		backupEngine.restoreBackup();
		backupEngine.cleanBackup();
		consoleLogger.writeLine("PatcherEngine: Restore & clean Backup done");
	}

	private ListUnique<OverrideTask> executeOverrides(List<Feature> featureList) throws IOException {
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

		return overrideTasks;
	}

	private void executeFeatures(List<Feature> featureList, ListUnique<OverrideTask> overrideTasks) throws IOException, TransformerException {
		// ## Execute Features ##
		consoleLogger.writeLine("PatcherEngine: Apply Features started ... ");
		Set<FileEntity> filesToUpdate = new HashSet<>();
		for (Feature feature : featureList) {

			consoleLogger.writeLine("PatcherEngine: Feature ["+feature.Name+"] execution started ...");

			try {
				feature.preExecuteUpdates();
				feature.executeUpdates();
			}
			catch (Exception ex) {
				val ftName = feature.getName();
				throw new FeatureEx(ex.getMessage(), ex, ftName);
			}

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
	}

	public void Patch(List<Feature> featureFullList) throws Exception {
		consoleLogger.writeLine("PatcherEngine: Patching process has started ... ");
		InitializeFeatures(featureFullList);
		fileEntityFactory.reset();

		restoreCleanBackup();

		// Determine Features to apply
		List<Feature> featureList = new ArrayList<>();
		// ### Only Enabled Features ###
		featureList.addAll(featureFullList.stream().filter(feature -> feature.isEnabled()).collect(Collectors.toList()));
		consoleLogger.writeLine("PatcherEngine: Found "+featureList.size()+" features to apply");

		try {
			// ## Execute Overrides ##
			val overrideTasks = executeOverrides(featureList);
			// ## Execute Features ##
			executeFeatures(featureList, overrideTasks);

			consoleLogger.writeLine("PatcherEngine: Patching process finished. Selected Features applied.");
		}
		catch (Exception ex) {
			consoleLogger.writeLine("PatcherEngine: Error in overrides or features encountered!");
			consoleLogger.writeLine("PatcherEngine: Trying to clean up after error - reverting any changes");
			restoreCleanBackup();
			throw ex;
		}
	}

	protected void InitializeFeatures(List<Feature> featureList) {

	}

	public void Initialize() {
		consoleLogger.writeLine("PatcherEngine initialization started ...");

		fileEntityFactory.rootPath = DestinationRootPath;

		backupEngine = new BackupEngine(consoleLogger);
		backupEngine.BackupRootPath = BackupRootPath;
		backupEngine.DestinationRootPath = DestinationRootPath;

		consoleLogger.writeLine("PatcherEngine initialization done");
	}

	protected ConsoleLogger consoleLogger;

	protected FileEntityFactory fileEntityFactory;

	public String OverrideRootPath;
	public String BackupRootPath;
	public String DestinationRootPath;

	public PatcherEngine(ConsoleLogger consoleLogger, FileEntityFactory fileEntityFactory) {
		this.consoleLogger = consoleLogger;
		this.fileEntityFactory = fileEntityFactory;
	}
}
