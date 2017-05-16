package tm.mtwModPatcher.lib.common.core.features;

/**
 * Base Exception for all core libraries.
 */
public class PatcherLibBaseEx extends RuntimeException {

	public PatcherLibBaseEx() {
	}

	public PatcherLibBaseEx(String message) {
		super(message);
	}

	public PatcherLibBaseEx(String message, Throwable cause) {
		super(message, cause);
	}

	public PatcherLibBaseEx(Throwable cause) {
		super(cause);
	}

	public PatcherLibBaseEx(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
