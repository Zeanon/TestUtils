package de.zeanon.testutils.plugin.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum StringModifiers {

	LINE_BREAK("\n");

	private final @NotNull String value;

	@Override
	public String toString() {
		return this.value;
	}
}