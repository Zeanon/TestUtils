package de.zeanon.testutils.plugin.utils.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum Flag {

	TNT(de.zeanon.testutils.plugin.utils.enums.flagvalues.TNT.class),
	DROP_ITEMS(de.zeanon.testutils.plugin.utils.enums.flagvalues.DROP_ITEMS.class),
	FIRE(de.zeanon.testutils.plugin.utils.enums.flagvalues.FIRE.class),
	STOPLAG(de.zeanon.testutils.plugin.utils.enums.flagvalues.STOPLAG.class);

	private final @NotNull Class<?> value;


	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	@Getter
	@AllArgsConstructor
	public static class Value<T extends Enum<T>> {

		private final @NotNull T enumValue;
	}
}