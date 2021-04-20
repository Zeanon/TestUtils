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