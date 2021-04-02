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
public enum LEAVES_DECAY implements Flag.Value<LEAVES_DECAY> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	@Getter
	private static final @NotNull Set<LEAVES_DECAY> values;

	static {
		values = new HashSet<>(Arrays.asList(LEAVES_DECAY.values()));
	}

	private final @NotNull String chatValue;

	@Override
	public LEAVES_DECAY getValue() {
		return this;
	}

	@Override
	public LEAVES_DECAY getValueOf(final @NotNull String name) {
		try {
			return LEAVES_DECAY.valueOf(name);
		} catch (IllegalArgumentException e) {
			return LEAVES_DECAY.ALLOW;
		}
	}
}