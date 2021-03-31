package de.zeanon.testutils.plugin.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public class MappedFolder {

	final @NotNull String name;

	@Override
	public String toString() {
		return this.name;
	}
}
