package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum DAMAGE implements Flag.Value<DAMAGE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull DAMAGE[] values = DAMAGE.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull DAMAGE[] getValues() {
		return DAMAGE.values;
	}


	@Override
	public @NotNull DAMAGE getValue() {
		return this;
	}

	@Override
	public @NotNull DAMAGE getValueOf(final @NotNull String name) {
		try {
			return DAMAGE.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return DAMAGE.DENY;
			}

			return DAMAGE.ALLOW;
		}
	}
}