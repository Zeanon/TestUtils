package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum ITEM_DROPS implements Flag.Value<ITEM_DROPS> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull ITEM_DROPS[] values = ITEM_DROPS.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull ITEM_DROPS[] getValues() {
		return ITEM_DROPS.values;
	}

	@Override
	public @NotNull ITEM_DROPS getValue() {
		return this;
	}

	@Override
	public @NotNull ITEM_DROPS getValueOf(final @NotNull String name) {
		try {
			return ITEM_DROPS.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return ITEM_DROPS.DENY;
			}

			return ITEM_DROPS.ALLOW;
		}
	}
}