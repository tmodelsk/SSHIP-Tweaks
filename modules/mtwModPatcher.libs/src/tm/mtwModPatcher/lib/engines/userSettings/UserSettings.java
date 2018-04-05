package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.Getter;
import lombok.Setter;
import tm.mtwModPatcher.lib.common.core.features.Feature;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tomek on 17.10.2017.
 */
public class UserSettings {

	@Getter @Setter
	private String name;

	@Getter @Setter
	private String version;

	@Getter @Setter
	private String createdDate;

	@Getter
	private List<FeatureSettings> features = null;


	public void add(FeatureSettings featureSettings) {
		if(features == null) features = new ArrayList<>();

		features.add(featureSettings);
	}

	public List<FeatureSettings> featuresByMapRemoval() {
		return features.stream().filter( f -> f.isEnabled() && f.isMapRemovalRequirement() ).collect(Collectors.toList());
	}
	public Set<UUID> featureIdsSetByMapRemoval() {
		return featuresByMapRemoval().stream().map( f -> f.getId()).collect(Collectors.toSet());
	}


	public UserSettings() {
	}
}
