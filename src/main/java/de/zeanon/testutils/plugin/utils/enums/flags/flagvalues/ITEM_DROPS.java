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
public enum ITEM_DROPS implements Flag.Value<ITEM_DROPS> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	@Getter
	private static final @NotNull Set<ITEM_DROPS> values;

	static {
		values = new HashSet<>(Arrays.asList(ITEM_DROPS.values()));
	}

	private final @NotNull String chatValue;

	@Override
	public ITEM_DROPS getValue() {
		return this;
	}

	@Override
	public ITEM_DROPS getValueOf(final @NotNull String name) {
		try {
			return ITEM_DROPS.valueOf(name);
		} catch (IllegalArgumentException e) {
			return ITEM_DROPS.ALLOW;
		}
	}
}