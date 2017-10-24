package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.PatcherNotSupportedEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherUnexpectedEx;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by tomek on 17.10.2017.
 */
public class SettingsEngine {

	public void saveSettings(String profileName, String versionStr, List<Feature> featureList) throws Exception {

		val us = new UserSettings();

		us.setName(profileName);
		us.setVersion(versionStr);
		us.setCreatedDate(LocalDateTime.now().toString());

		for (val feature : featureList) {
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
		settingsRepository.setRootPath(Ctm.getWorkingDirectory());
		val userSettings =  settingsRepository.loadSettings(profileName);

		if(userSettings != null) {
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
								else throw new PatcherUnexpectedEx("Persisted param value not found!");

								feature.setParValue(symbol, value);
							}
						}
					}
				}
			}
		}


		return userSettings;
	}

	private SettingsRepository settingsRepository;


	public SettingsEngine(SettingsRepository settingsRepository) {
		this.settingsRepository = settingsRepository;
	}
}
