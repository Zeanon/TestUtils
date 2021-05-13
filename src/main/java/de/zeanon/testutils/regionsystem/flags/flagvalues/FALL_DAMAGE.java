package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum FALL_DAMAGE implements Flag.Value<FALL_DAMAGE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull FALL_DAMAGE[] values = FALL_DAMAGE.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull FALL_DAMAGE[] getValues() {
		return FALL_DAMAGE.values;
	}

	@Override
	public @NotNull FALL_DAMAGE getValue() {
		return this;
	}

	@Override
	public @NotNull FALL_DAMAGE getValueOf(final @NotNull String name) {
		try {
			return FALL_DAMAGE.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return FALL_DAMAGE.DENY;
			}

			return FALL_DAMAGE.ALLOW;
		}
	}
}
