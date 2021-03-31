package de.zeanon.testutils.plugin.utils.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum Flag {

	TNT(de.zeanon.testutils.plugin.utils.enums.flagvalues.TNT.class),
	ITEM_DROPS(de.zeanon.testutils.plugin.utils.enums.flagvalues.ITEM_DROPS.class),
	FIRE(de.zeanon.testutils.plugin.utils.enums.flagvalues.FIRE.class),
	LEAVES_DECAY(de.zeanon.testutils.plugin.utils.enums.flagvalues.LEAVES_DECAY.class);

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