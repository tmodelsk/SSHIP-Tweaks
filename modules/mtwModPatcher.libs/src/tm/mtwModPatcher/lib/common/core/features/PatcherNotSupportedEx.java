package tm.mtwModPatcher.lib.common.core.features;

/** Operation not supported by Patcher system */
public class PatcherNotSupportedEx extends PatcherLibBaseEx {

	public PatcherNotSupportedEx() {
	}

	public PatcherNotSupportedEx(String message) {
		super(message);
	}

	public PatcherNotSupportedEx(String message, Throwable cause) {
		super(message, cause);
	}

	public PatcherNotSupportedEx(Throwable cause) {
		super(cause);
	}

	public PatcherNotSupportedEx(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
