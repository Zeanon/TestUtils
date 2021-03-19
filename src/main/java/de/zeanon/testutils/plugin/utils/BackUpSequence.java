package de.zeanon.testutils.plugin.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


public enum BackUpSequence {

	STARTUP("automatic/startup"),
	HOURLY("automatic/hourly"),
	DAILY("automatic/daily"),
	MANUAL("manual"),
	NONE("");

	private final @NotNull String path;

	BackUpSequence(final @NotNull String path) {
		this.path = path;
	}

	public static BackUpSequence parse(final @NotNull String cycle) {
		switch (cycle) {
			case "-manual":
				return BackUpSequence.MANUAL;
			case "-startup":
				return BackUpSequence.STARTUP;
			case "-hourly":
				return BackUpSequence.HOURLY;
			case "-daily":
				return BackUpSequence.DAILY;
			default:
				return BackUpSequence.NONE;
		}
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return this.path;
	}
}