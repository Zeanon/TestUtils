package de.zeanon.testutils.plugin.utils;

import org.jetbrains.annotations.NotNull;


public enum PasteSide {

	ALL("all"),
	NORTH("the north"),
	SOUTH("the south"),
	HERE("your"),
	OTHER("the other"),
	NONE("no");

	private final @NotNull String name;

	PasteSide(final @NotNull String name) {
		this.name = name;
	}

	public static PasteSide parse(final @NotNull String side) {
		switch (side) {
			case "-north":
			case "-n":
				return PasteSide.NORTH;
			case "-south":
			case "-s":
				return PasteSide.SOUTH;
			case "-here":
				return PasteSide.HERE;
			case "-other":
				return PasteSide.OTHER;
			default:
				return PasteSide.NONE;
		}
	}

	@Override
	public String toString() {
		return this.name;
	}
}
