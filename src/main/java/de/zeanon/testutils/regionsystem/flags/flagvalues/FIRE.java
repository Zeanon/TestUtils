package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum FIRE implements Flag.Value<FIRE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull FIRE[] values = FIRE.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull FIRE[] getValues() {
		return FIRE.values;
	}

	@Override
	public @NotNull FIRE getValue() {
		return this;
	}

	@Override
	public @NotNull FIRE getValueOf(final @NotNull String name) {
		try {
			return FIRE.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return FIRE.DENY;
			}
			return FIRE.ALLOW;
		}
	}
}