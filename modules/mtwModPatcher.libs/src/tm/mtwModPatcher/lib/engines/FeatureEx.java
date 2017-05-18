package tm.mtwModPatcher.lib.engines;

import lombok.Getter;
import lombok.Setter;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

/**
 * Created by tomek on 19.05.2017.
 */
public class FeatureEx extends PatcherLibBaseEx {

	@Getter @Setter
	private String featureName;

	public FeatureEx(String featureName) {
		this.featureName = featureName;
	}

	public FeatureEx(String message, String featureName) {
		super(message);
		this.featureName = featureName;
	}

	public FeatureEx(String message, Throwable cause, String featureName) {
		super(message, cause);
		this.featureName = featureName;
	}

	public FeatureEx(Throwable cause, String featureName) {
		super(cause);
		this.featureName = featureName;
	}

	public FeatureEx(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String featureName) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.featureName = featureName;
	}
}
