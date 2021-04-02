package de.zeanon.testutils.plugin.utils.enums.flags.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.flags.Flag;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum FIRE implements Flag.Value<FIRE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	@Getter
	private static final @NotNull Set<FIRE> values;

	static {
		values = new HashSet<>(Arrays.asList(FIRE.values()));
	}

	private final @NotNull String chatValue;

	@Override
	public FIRE getValue() {
		return this;
	}

	@Override
	public FIRE getValueOf(final @NotNull String name) {
		try {
			return FIRE.valueOf(name);
		} catch (IllegalArgumentException e) {
			return FIRE.ALLOW;
		}
	}
}