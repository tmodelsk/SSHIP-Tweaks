package tm.common.collections;

/**
 * Created by tomek on 21.04.2017.
 */
public class NotUniqueEx extends RuntimeException {

	public NotUniqueEx() {
	}

	public NotUniqueEx(String message) {
		super(message);
	}

	public NotUniqueEx(String message, Throwable cause) {
		super(message, cause);
	}

	public NotUniqueEx(Throwable cause) {
		super(cause);
	}

	public NotUniqueEx(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
