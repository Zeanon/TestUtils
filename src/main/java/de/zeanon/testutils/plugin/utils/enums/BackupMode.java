package de.zeanon.testutils.plugin.utils.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public enum BackupMode {

	STARTUP("automatic/startup"),
	HOURLY("automatic/hourly"),
	DAILY("automatic/daily"),
	MANUAL("manual"),
	NONE("");

	private final @NotNull String path;

	BackupMode(final @NotNull String path) {
		this.path = path;
	}

	public static BackupMode parse(final @NotNull String cycle) {
		switch (cycle) {
			case "-manual":
				return BackupMode.MANUAL;
			case "-startup":
				return BackupMode.STARTUP;
			case "-hourly":
				return BackupMode.HOURLY;
			case "-daily":
				return BackupMode.DAILY;
			default:
				return BackupMode.NONE;
		}
	}

	@Contract(pure = true)
	public @NotNull String getPath(final @Nullable String uuid) {
		return this == BackupMode.MANUAL ? this.path + "/" + uuid : this.path;
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return this.name().toLowerCase();
	}
}