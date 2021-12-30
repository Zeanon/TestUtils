package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum SNOW_MELT implements Flag.Value<SNOW_MELT> {
	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull SNOW_MELT[] values = SNOW_MELT.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull SNOW_MELT[] getValues() {
		return SNOW_MELT.values;
	}

	@Override
	public @NotNull SNOW_MELT getValue() {
		return this;
	}

	@Override
	public @NotNull SNOW_MELT getValueOf(final @NotNull String name) {
		try {
			return SNOW_MELT.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return SNOW_MELT.DENY;
			}

			return SNOW_MELT.ALLOW;
		}
	}
}