package tm.mtwModPatcher.lib.common.core.features.fileEntities;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

/** Line has not been found ! */
public class LineNotFoundEx extends PatcherLibBaseEx {

	public LineNotFoundEx() {
	}

	public LineNotFoundEx(String message) {
		super(message);
	}

	public LineNotFoundEx(String message, Throwable cause) {
		super(message, cause);
	}

	public LineNotFoundEx(Throwable cause) {
		super(cause);
	}

	public LineNotFoundEx(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
