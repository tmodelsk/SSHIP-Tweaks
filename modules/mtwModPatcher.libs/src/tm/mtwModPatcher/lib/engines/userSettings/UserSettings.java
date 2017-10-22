package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.Getter;
import lombok.Setter;
import tm.mtwModPatcher.lib.common.core.features.Feature;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tomek on 17.10.2017.
 */
public class UserSettings {

	@Getter @Setter
	private String name;

	@Getter @Setter
	private String version;

	@Getter @Setter
	private LocalDateTime createdDate;

	@Getter
	private List<FeatureSettings> features = new ArrayList<>();


	public void add(FeatureSettings featureSettings) {
		features.add(featureSettings);
	}

}
