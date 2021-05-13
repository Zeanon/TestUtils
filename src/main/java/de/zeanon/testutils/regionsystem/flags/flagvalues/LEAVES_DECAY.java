package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum LEAVES_DECAY implements Flag.Value<LEAVES_DECAY> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull LEAVES_DECAY[] values = LEAVES_DECAY.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull LEAVES_DECAY[] getValues() {
		return LEAVES_DECAY.values;
	}

	@Override
	public @NotNull LEAVES_DECAY getValue() {
		return this;
	}

	@Override
	public @NotNull LEAVES_DECAY getValueOf(final @NotNull String name) {
		try {
			return LEAVES_DECAY.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return LEAVES_DECAY.DENY;
			}

			return LEAVES_DECAY.ALLOW;
		}
	}
}