package de.zeanon.testutils.commandframework;

@SuppressWarnings("unused")
public class CommandParseException extends Exception {

	public CommandParseException() {
		super();
	}

	public CommandParseException(final String message) {
		super(message);
	}

	public CommandParseException(final Throwable cause) {
		super(cause);
	}

	public CommandParseException(final String message, final Throwable cause) {
		super(message, cause);
	}
}