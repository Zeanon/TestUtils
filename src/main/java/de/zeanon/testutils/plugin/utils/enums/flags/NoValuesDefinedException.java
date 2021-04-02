package de.zeanon.testutils.plugin.utils.enums.flags;

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