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
public enum DAMAGE implements Flag.Value<DAMAGE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static @Nullable DAMAGE[] values;
	private final @NotNull String chatValue;


	@Override
	public @NotNull DAMAGE[] getValues() {
		if (DAMAGE.values == null) {
			DAMAGE.values = DAMAGE.values(); //NOSONAR
		}
		//noinspection NullableProblems
		return Objects.notNull(DAMAGE.values);
	}

	@Override
	public @NotNull DAMAGE getValue() {
		return this;
	}

	@Override
	public @NotNull DAMAGE getValueOf(final @NotNull String name) {
		try {
			return DAMAGE.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return DAMAGE.DENY;
			}

			return DAMAGE.ALLOW;
		}
	}
}