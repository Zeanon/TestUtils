package de.zeanon.testutils.plugin.utils.enums;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DeepSearch {

	CONFIRM(true),
	DENY(false);

	@Getter
	@Accessors(fluent = true)
	private final boolean confirm;

	public static DeepSearch map(final @NotNull String arg) {
		switch (arg.toLowerCase()) {
			case "-d":
			case "-deep":
				return DeepSearch.CONFIRM;
			case "":
				return DeepSearch.DENY;
			default:
				return null;
		}
	}
}