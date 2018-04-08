package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.PatcherNotSupportedEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherUnexpectedEx;
import tm.mtwModPatcher.lib.engines.ConsoleLogger;

import java.time.LocalDateTime;

public class SettingsEngine {

	public void saveSettings(String profileName, String versionStr, FeatureList featureList) throws Exception {

		val us = new UserSettings();

		us.setName(profileName);
		us.setVersion(versionStr);
		us.setCreatedDate(LocalDateTime.now().toString());

		for (val feature : featureList.getFeaturesList()) {
			val fs = createFeatureSettings(feature);
			us.add(fs);
		}

		settingsRepository.setRootPath(Ctm.getWorkingDirectory());
		settingsRepository.saveSettings(us);
	}

	private FeatureSettings createFeatureSettings(Feature f) {
		val fs = new FeatureSettings();

		fs.setId(f.getId());
		fs.setName(f.getName());
		fs.setVersion(f.getVersion());
		fs.setEnabled(f.isEnabled());
		fs.setMapRemovalRequirement(f.isMapRemovalRequirement());

		val pars = f.getPars();
		for (val par : pars) {
			val fPar = new FeatureParam();
			fPar.setSymbol(par.getParamId().getSymbol());
			val type = par.getInnerType();

			if(type.equals(String.class)) {
				fPar.setValue(par.getValueAsString());
			}
			else if(type.equals(Integer.class)) {
				fPar.setValue(par.getValueAsInteger());
			}
			else if(type.equals(Double.class)) {
				fPar.setValue(par.getValueAsDouble());
			}
			else if(type.equals(Boolean.class)) {
				fPar.setValue(par.getValueAsBoolean());
			}
			else throw new PatcherNotSupportedEx(type.toString());

			fs.add(fPar);
		}

		return fs;
	}

	public UserSettings loadSettings(String profileName, FeatureList featureList) throws Exception {

		consoleLogger.writeLine("SettingsEngine: Load settings started ...");

		settingsRepository.setRootPath(Ctm.getWorkingDirectory());
		val userSettings = loadSettings(profileName);

		if(userSettings != null) {
			consoleLogger.writeLine("SettingsEngine: Loading stored settings from version " + userSettings.getVersion());
			if(userSettings.getFeatures() != null) {
				for (val fs : userSettings.getFeatures()) {
					val feature = featureList.get(fs.getId());

					if(feature != null) {
						feature.setEnabled(fs.isEnabled());

						if(fs.getParameters() != null) {
							for (val fp : fs.getParameters()) {

								val symbol = fp.getSymbol();
								Object value;
								if(fp.getValueBoolean() != null)	value = fp.getValueBoolean();
								else if(fp.getValueDouble() != null)	value = fp.getValueDouble();
								else if(fp.getValueInteger() != null)	value = fp.getValueInteger();
								else if(fp.getValueString() != null)	value = fp.getValueString();
								else throw new PatcherUnexpectedEx("Persisted param value type not found!");

								if( feature.getParam(symbol) != null)
									feature.setParValue(symbol, value);
								else {
									consoleLogger.writeLine(Ctm.format("WARNING: Loading settings: Feature {0} param {1} from stored settings not found", feature.getName(), symbol));
								}
							}
						}
					}
					else {
						consoleLogger.writeLine(Ctm.format("WARNING: Load settings: Feature.id='{0}' not found on feature list", fs.getId()));
					}
				}
			}
		}

		consoleLogger.writeLine("SettingsEngine: Load settings done.");
		return userSettings;
	}

	public UserSettings loadSettings(String profileName) throws Exception {
		return settingsRepository.loadSettings(profileName);
	}

	private SettingsRepository settingsRepository;
	private ConsoleLogger consoleLogger;


	public SettingsEngine(SettingsRepository settingsRepository, ConsoleLogger consoleLogger) {
		this.settingsRepository = settingsRepository;
		this.consoleLogger = consoleLogger;
	}
}
