package de.zeanon.testutils.plugin.utils.enums.flags;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
public enum Flag {

	TNT(de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.TNT.class, de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.TNT.ALLOW),
	ITEM_DROPS(de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.ITEM_DROPS.class, de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.ITEM_DROPS.ALLOW),
	FIRE(de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.FIRE.class, de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.FIRE.ALLOW),
	LEAVES_DECAY(de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.LEAVES_DECAY.class, de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.LEAVES_DECAY.ALLOW),
	STOPLAG(de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.STOPLAG.class, de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.STOPLAG.INACTIVE),
	FALL_DAMAGE(de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.FALL_DAMAGE.class, de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.FALL_DAMAGE.ALLOW),
	DAMAGE(de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.DAMAGE.class, de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.DAMAGE.ALLOW);

	@Getter
	private static final @NotNull Set<Flag> flags;

	static {
		flags = new HashSet<>(Arrays.asList(Flag.values()));
	}


	private final @NotNull Class<? extends Value<?>> valueType;
	private final @NotNull Flag.Value<?> defaultValue;
	private final @NotNull Set<Value<?>> values;

	<T extends Enum<T> & Value<T>> Flag(final @NotNull Class<? extends Value<T>> valueType, final @NotNull Flag.Value<T> defaultValue) {
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.values = Value.getValues();
	}

	public Value<?> getValueOf(final @NotNull String name) {
		return this.defaultValue.getValueOf(name);
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}


	public interface Value<T extends Enum<T> & Value<T>> {

		static @NotNull Set<Value<?>> getValues() {
			return Collections.emptySet();
		}

		T getValue();

		T getValueOf(final @NotNull String name);

		@NotNull String getChatValue();

		default @NotNull String getName() {
			return this.getValue().name().toLowerCase();
		}
	}
}