package de.zeanon.testutils.plugin.utils.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;


@AllArgsConstructor(access = AccessLevel.PRIVATE)

public enum CaseSensitive {

	CONFIRM(true),
	DENY(false);

	@Getter
	@Accessors(fluent = true)
	private final boolean confirm;

	public static CaseSensitive map(final @NotNull String arg) {
		switch (arg.toLowerCase()) {
			case "-c":
			case "-casesensitive":
				return CaseSensitive.CONFIRM;
			case "":
				return CaseSensitive.DENY;
			default:
				return null;
		}
	}
}