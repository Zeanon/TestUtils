package de.zeanon.testutils.regionsystem.region;

public class IllegalFlagException extends Exception {

	public IllegalFlagException() {
		super();
	}

	public IllegalFlagException(final String message) {
		super(message);
	}

	public IllegalFlagException(final Throwable cause) {
		super(cause);
	}

	public IllegalFlagException(final String message, final Throwable cause) {
		super(message, cause);
	}
}