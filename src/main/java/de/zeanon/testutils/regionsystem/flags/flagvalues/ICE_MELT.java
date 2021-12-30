package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum ICE_MELT implements Flag.Value<ICE_MELT> {
	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull ICE_MELT[] values = ICE_MELT.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull ICE_MELT[] getValues() {
		return ICE_MELT.values;
	}

	@Override
	public @NotNull ICE_MELT getValue() {
		return this;
	}

	@Override
	public @NotNull ICE_MELT getValueOf(final @NotNull String name) {
		try {
			return ICE_MELT.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return ICE_MELT.DENY;
			}

			return ICE_MELT.ALLOW;
		}
	}
}