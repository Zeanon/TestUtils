package de.zeanon.testutils.plugin.utils.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


public enum BackUpMode {

	STARTUP("automatic/startup"),
	HOURLY("automatic/hourly"),
	DAILY("automatic/daily"),
	MANUAL("manual"),
	NONE("");

	private final @NotNull String path;

	BackUpMode(final @NotNull String path) {
		this.path = path;
	}

	public static BackUpMode parse(final @NotNull String cycle) {
		switch (cycle) {
			case "-manual":
				return BackUpMode.MANUAL;
			case "-startup":
				return BackUpMode.STARTUP;
			case "-hourly":
				return BackUpMode.HOURLY;
			case "-daily":
				return BackUpMode.DAILY;
			default:
				return BackUpMode.NONE;
		}
	}

	@Contract(pure = true)
	public @NotNull String getPath() {
		return this.path;
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return this.name().toLowerCase();
	}
}