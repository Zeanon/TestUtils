package de.zeanon.testutils.regionsystem.flags;

public class NoValuesDefinedException extends RuntimeException {

	public NoValuesDefinedException() {
		super();
	}

	public NoValuesDefinedException(final String message) {
		super(message);
	}

	public NoValuesDefinedException(final Throwable cause) {
		super(cause);
	}

	public NoValuesDefinedException(final String message, final Throwable cause) {
		super(message, cause);
	}
}