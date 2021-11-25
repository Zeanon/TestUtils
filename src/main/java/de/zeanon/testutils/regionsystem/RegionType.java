package de.zeanon.testutils.regionsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum RegionType {

	GLOBAL_REGION("GlobalRegion", "resources/global.json"),
	DEFINED_REGION("DefinedRegion", "resources/region.json"),
	TESTAREA("TestArea", "resources/testarea.json");


	private final @NotNull String name;
	private final @NotNull String resource;


	@Override
	public @NotNull String toString() {
		return this.name;
	}
}