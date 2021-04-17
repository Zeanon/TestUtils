package de.zeanon.testutils.regionsystem.flags;


import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
public enum Flag {

	TNT(de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.class, de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW),
	ITEM_DROPS(de.zeanon.testutils.regionsystem.flags.flagvalues.ITEM_DROPS.class, de.zeanon.testutils.regionsystem.flags.flagvalues.ITEM_DROPS.ALLOW),
	FIRE(de.zeanon.testutils.regionsystem.flags.flagvalues.FIRE.class, de.zeanon.testutils.regionsystem.flags.flagvalues.FIRE.ALLOW),
	LEAVES_DECAY(de.zeanon.testutils.regionsystem.flags.flagvalues.LEAVES_DECAY.class, de.zeanon.testutils.regionsystem.flags.flagvalues.LEAVES_DECAY.ALLOW),
	STOPLAG(de.zeanon.testutils.regionsystem.flags.flagvalues.STOPLAG.class, de.zeanon.testutils.regionsystem.flags.flagvalues.STOPLAG.INACTIVE),
	FALL_DAMAGE(de.zeanon.testutils.regionsystem.flags.flagvalues.FALL_DAMAGE.class, de.zeanon.testutils.regionsystem.flags.flagvalues.FALL_DAMAGE.ALLOW),
	DAMAGE(de.zeanon.testutils.regionsystem.flags.flagvalues.DAMAGE.class, de.zeanon.testutils.regionsystem.flags.flagvalues.DAMAGE.ALLOW);


	@Getter
	private static final @NotNull Set<Flag> flags;

	static {
		flags = EnumSet.allOf(Flag.class);
	}


	private final @NotNull Class<? extends Value<?>> valueType;
	private final @NotNull Flag.Value<?> defaultValue;
	private final Value<?>[] values;


	<T extends Enum<T> & Value<T>> Flag(final @NotNull Class<? extends Value<T>> valueType, final @NotNull Flag.Value<T> defaultValue) {
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.values = defaultValue.getValues();
	}


	public Value<?> getFlagValueOf(final @NotNull String name) {
		return this.defaultValue.getValueOf(name);
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}


	public interface Value<T extends Enum<T> & Value<T>> {

		T getValue();

		T getValueOf(final @NotNull String name);

		@NotNull T[] getValues();

		@NotNull String getChatValue();

		default @NotNull String getName() {
			return this.getValue().name().toLowerCase();
		}
	}
}