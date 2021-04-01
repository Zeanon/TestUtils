package de.zeanon.testutils.plugin.utils.enums;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
public enum Flag {

	TNT(de.zeanon.testutils.plugin.utils.enums.flagvalues.TNT.class, de.zeanon.testutils.plugin.utils.enums.flagvalues.TNT.ALLOW),
	ITEM_DROPS(de.zeanon.testutils.plugin.utils.enums.flagvalues.ITEM_DROPS.class, de.zeanon.testutils.plugin.utils.enums.flagvalues.ITEM_DROPS.ALLOW),
	FIRE(de.zeanon.testutils.plugin.utils.enums.flagvalues.FIRE.class, de.zeanon.testutils.plugin.utils.enums.flagvalues.FIRE.ALLOW),
	LEAVES_DECAY(de.zeanon.testutils.plugin.utils.enums.flagvalues.LEAVES_DECAY.class, de.zeanon.testutils.plugin.utils.enums.flagvalues.LEAVES_DECAY.ALLOW),
	STOPLAG(de.zeanon.testutils.plugin.utils.enums.flagvalues.STOPLAG.class, de.zeanon.testutils.plugin.utils.enums.flagvalues.STOPLAG.INACTIVE);

	@Getter
	private static final @NotNull Set<Flag> flags;

	static {
		flags = new HashSet<>(Arrays.asList(Flag.values()));
	}


	private final @NotNull Class<? extends Value<?>> value;
	private final @NotNull Flag.Value<?> defaultValue;


	<T extends Enum<T> & Value<T>> Flag(final @NotNull Class<? extends Value<T>> value, final @NotNull Flag.Value<T> defaultValue) {
		this.value = value;
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}


	public interface Value<T extends Enum<T> & Value<T>> {

		T getValue();

		T getValue(final @NotNull String name);

		@NotNull String getChatValue();
	}
}