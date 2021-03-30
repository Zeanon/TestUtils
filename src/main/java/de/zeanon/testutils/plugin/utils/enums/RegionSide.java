package de.zeanon.testutils.plugin.utils.enums;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;


public enum RegionSide {

	NORTH("the north", "the south"),
	SOUTH("the south", "the north"),
	HERE("your", "the other"),
	OTHER("the other", "your"),
	NONE("no", "no");

	@Getter
	private final @NotNull String name;
	@Getter
	private final @NotNull String otherName;

	RegionSide(final @NotNull String name, final @NotNull String otherName) {
		this.name = name;
		this.otherName = otherName;
	}

	public static RegionSide parse(final @NotNull String side) {
		switch (side) {
			case "-north":
			case "-n":
				return RegionSide.NORTH;
			case "-south":
			case "-s":
				return RegionSide.SOUTH;
			case "-here":
				return RegionSide.HERE;
			case "-other":
				return RegionSide.OTHER;
			default:
				return RegionSide.NONE;
		}
	}

	@Override
	public String toString() {
		return this.name;
	}
}