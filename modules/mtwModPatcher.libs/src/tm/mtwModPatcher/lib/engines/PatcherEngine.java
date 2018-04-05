package tm.mtwModPatcher.lib.engines;

import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileEntity;
import tm.mtwModPatcher.lib.common.core.features.OverrideTask;
import tm.mtwModPatcher.lib.engines.userSettings.SettingsEngine;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**  */
public class PatcherEngine {

	/** Main-Root patching method */
	public void patch(FeatureList featureFullList, String appVersion) throws Exception {
		consoleLogger.writeLine("PatcherEngine: Patching process has started ... ");
		fileEntityFactory.reset();

		restoreCleanBackup();

		// ### Determine Enabled Features to apply ###
		val featureList = featureFullList.getFeaturesEnabledList();
		consoleLogger.writeLine("PatcherEngine: Found " + featureList.size() + " features to apply");

		val userSettings = settingsEngine.loadSettings("mySettings");
		Set<UUID> previousMapRemovalIds = userSettings != null ? userSettings.featureIdsSetByMapRemoval() : null;
		val actualMapRemovalIds = featureFullList.getIdsSetByEnabledAndMapRemoval();

		List<OverrideTask> additionalOverrideTasks = new ArrayList<>();
		if( actualMapRemovalIds != null && !actualMapRemovalIds.equals(previousMapRemovalIds))
			additionalOverrideTasks.add(OverrideDeleteFilesTask.DELETE_MAP_RWM);

		// ## Save user settings ##
		settingsEngine.saveSettings("mySettings", appVersion, featureFullList);

		try {
			// ## Execute Overrides ##
			val overrideTasks = executeOverrides(featureList, additionalOverrideTasks);
			// ## Execute Features ##
			executeFeatures(featureList, overrideTasks);

			consoleLogger.writeLine("PatcherEngine: Patching process finished. Selected Features applied.");
		} catch (Exception ex) {
			consoleLogger.writeLine("PatcherEngine: Error in overrides or features encountered!");
			consoleLogger.writeLine("PatcherEngine: Trying to clean up after error - reverting any changes");
			restoreCleanBackup();
			throw ex;
		}
	}

	private void restoreCleanBackup() throws IOException {
		consoleLogger.writeLine("PatcherEngine: Restore & clean Backup started ...");
		backupEngine.restoreBackup();
		backupEngine.cleanBackup();
		consoleLogger.writeLine("PatcherEngine: Restore & clean Backup done");
	}

	private Set<OverrideTask> executeOverrides(List<Feature> featureList, List<OverrideTask> additionalTasks) throws IOException {
		// ## Do Overrides Tasks ##
		consoleLogger.writeLine("PatcherEngine: Override tasks execution started ...");
		List<String> overrideRelativePaths = new ArrayList<>();
		val overrideTasks = new HashSet<OverrideTask>();

		// # Determine all override tasks #
		for (Feature baseFeature : featureList) {
			if (baseFeature.getOverrideTasks() != null)
				overrideTasks.addAll(baseFeature.getOverrideTasks());
		}
		if(additionalTasks != null)
			overrideTasks.addAll(additionalTasks);

		// ## Initialize them ...
		for (OverrideTask overrideTask : overrideTasks) {
			overrideTask.RootPath = OverrideRootPath;
			overrideTasks.add(overrideTask);
			overrideRelativePaths.addAll(overrideTask.getAffectedFilesRelativePaths());
		}

		consoleLogger.writeLine("PatcherEngine: Found " + overrideTasks.size() + " override tasks");

		// ## Backup & Execute Overrides ##
		if (overrideTasks.size() > 0) {
			consoleLogger.writeLine("PatcherEngine: Backup files to override started ... ");

			backupEngine.BackupPaths(overrideRelativePaths);
			consoleLogger.writeLine("PatcherEngine: Backup files to override done");

			consoleLogger.writeLine("PatcherEngine: Override files started ... ");
			OverrideEngine overrideEngine = new OverrideEngine(consoleLogger);
			overrideEngine.DestinationRootPath = DestinationRootPath;
			overrideEngine.OverrideRootPath = OverrideRootPath;
			overrideEngine.overridePaths( new ArrayList<>(overrideTasks));
			consoleLogger.writeLine("PatcherEngine: Override files done");
		}
		consoleLogger.writeLine("PatcherEngine: Override tasks execution done");

		return overrideTasks;
	}

	private void executeFeatures(List<Feature> featureList, Set<OverrideTask> overrideTasks) throws IOException, TransformerException {
		// ## Execute Features ##
		consoleLogger.writeLine("PatcherEngine: Apply Features started ... ");
		Set<FileEntity> filesToUpdate = new HashSet<>();
		for (Feature feature : featureList) {

			consoleLogger.writeLine("PatcherEngine: Feature [" + feature.name + "] execution started ...");

			try {
				feature.preExecuteUpdates();
				feature.executeUpdates();
			} catch (Exception ex) {
				val ftName = feature.getName();
				throw new FeatureEx(ex.getMessage(), ex, ftName);
			}

			val filesUpdated = feature.getFilesUpdated();
			filesToUpdate.addAll(filesUpdated);
			consoleLogger.writeLine("PatcherEngine: Feature [" + feature.name + "] execution done, updated (" + filesUpdated.size() + ") files");
		}
		consoleLogger.writeLine("PatcherEngine: Apply Features done");

		// ## Backup only changed but NOT Overrided Files !! ##
		consoleLogger.writeLine("PatcherEngine: Backup updated files starting ...");
		val filesToBackup = new ArrayList<FileEntity>();
		for (val fileToUpd : filesToUpdate) {

			if (!overrideTasks.contains(fileToUpd.filePath))
				filesToBackup.add(fileToUpd);
		}

		// ## Backup files to be updated ##
		backupEngine.BackupPaths(filesToUpdate);
		consoleLogger.writeLine("PatcherEngine: Backup updated files done");

		// ## Save Features changes to disk ##
		consoleLogger.writeLine("PatcherEngine: Save updated files starting ...");
		for (FileEntity file : filesToUpdate) {
			file.saveChanges();
			consoleLogger.writeLine("PatcherEngine: Saved updated file [" + file.getFullPath() + "]");
		}
		consoleLogger.writeLine("PatcherEngine: Save updated files done");
	}

	public void initialize(FeatureList featureList) throws Exception {
		consoleLogger.writeLine("PatcherEngine initialization started ...");

		fileEntityFactory.rootPath = DestinationRootPath;

		backupEngine = new BackupEngine(consoleLogger);
		backupEngine.BackupRootPath = BackupRootPath;
		backupEngine.DestinationRootPath = DestinationRootPath;

		settingsEngine.loadSettings("mySettings", featureList);

		consoleLogger.writeLine("PatcherEngine initialization done");
	}

	private BackupEngine backupEngine;
	private ConsoleLogger consoleLogger;
	private FileEntityFactory fileEntityFactory;
	private SettingsEngine settingsEngine;

	public String OverrideRootPath;
	public String BackupRootPath;
	public String DestinationRootPath;

	public PatcherEngine(ConsoleLogger consoleLogger, FileEntityFactory fileEntityFactory, SettingsEngine settingsEngine) {
		this.consoleLogger = consoleLogger;
		this.fileEntityFactory = fileEntityFactory;
		this.settingsEngine = settingsEngine;
	}
}
