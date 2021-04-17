package de.zeanon.testutils.regionsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum RegionType {

	GLOBAL_REGION("GlobalRegion"),
	DEFINED_REGION("DefinedRegion"),
	TESTAREA("TestArea");


	private final @NotNull String name;


	@Override
	public @NotNull String toString() {
		return this.name;
	}
}