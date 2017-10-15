package tm.mtwModPatcher.lib.common.core.features;

/**
 * Created by tomek on 14.10.2017.
 */
public class PatcherUnexpectedEx extends PatcherLibBaseEx {

	public PatcherUnexpectedEx() {
	}

	public PatcherUnexpectedEx(String message) {
		super(message);
	}

	public PatcherUnexpectedEx(String message, Throwable cause) {
		super(message, cause);
	}

	public PatcherUnexpectedEx(Throwable cause) {
		super(cause);
	}

	public PatcherUnexpectedEx(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
