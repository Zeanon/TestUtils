package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum MOB_SPAWN implements Flag.Value<MOB_SPAWN> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull MOB_SPAWN[] values = MOB_SPAWN.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull MOB_SPAWN[] getValues() {
		return MOB_SPAWN.values;
	}

	@Override
	public @NotNull MOB_SPAWN getValue() {
		return this;
	}

	@Override
	public @NotNull MOB_SPAWN getValueOf(final @NotNull String name) {
		try {
			return MOB_SPAWN.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return MOB_SPAWN.DENY;
			}

			return MOB_SPAWN.ALLOW;
		}
	}
}