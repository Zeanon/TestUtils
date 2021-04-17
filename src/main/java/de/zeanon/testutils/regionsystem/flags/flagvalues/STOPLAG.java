package de.zeanon.testutils.regionsystem.flags.flagvalues;


import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum STOPLAG implements Flag.Value<STOPLAG> {

	ACTIVE("activate", ChatColor.GREEN + "active"),
	INACTIVE("deactivate", ChatColor.RED + "inactive");


	private static @Nullable STOPLAG[] values;
	private final @NotNull String descriptor;
	private final @NotNull String chatValue;


	@Override
	public @NotNull STOPLAG[] getValues() {
		if (STOPLAG.values == null) {
			STOPLAG.values = STOPLAG.values(); //NOSONAR
		}
		//noinspection NullableProblems
		return Objects.notNull(STOPLAG.values);
	}

	@Override
	public @NotNull STOPLAG getValue() {
		return this;
	}

	@Override
	public @NotNull STOPLAG getValueOf(final @NotNull String name) {
		try {
			return STOPLAG.valueOf(name);
		} catch (IllegalArgumentException e) {
			return STOPLAG.INACTIVE;
		}
	}

	@Override
	public String toString() {
		return this.descriptor;
	}
}