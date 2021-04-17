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
public enum FIRE implements Flag.Value<FIRE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static @Nullable FIRE[] values;
	private final @NotNull String chatValue;


	@Override
	public @NotNull FIRE[] getValues() {
		if (FIRE.values == null) {
			FIRE.values = FIRE.values(); //NOSONAR
		}
		//noinspection NullableProblems
		return Objects.notNull(FIRE.values);
	}

	@Override
	public @NotNull FIRE getValue() {
		return this;
	}

	@Override
	public @NotNull FIRE getValueOf(final @NotNull String name) {
		try {
			return FIRE.valueOf(name);
		} catch (IllegalArgumentException e) {
			return FIRE.ALLOW;
		}
	}
}