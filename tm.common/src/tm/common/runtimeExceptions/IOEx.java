package tm.common.runtimeExceptions;

/**
 * IOException Runtime wrapper
 */
public class IOEx extends RuntimeException {


	public IOEx() {
	}

	public IOEx(String message) {
		super(message);
	}

	public IOEx(String message, Throwable cause) {
		super(message, cause);
	}

	public IOEx(Throwable cause) {
		super(cause);
	}

	public IOEx(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
