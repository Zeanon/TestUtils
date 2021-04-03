package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum ITEM_DROPS implements Flag.Value<ITEM_DROPS> {

	ALLOW(ChatColor.GREEN + "allow", null),
	DENY(ChatColor.RED + "deny", null);


	private final @NotNull String chatValue;
	private @Nullable ITEM_DROPS[] values;


	@Override
	public ITEM_DROPS[] getValues() {
		if (this.values == null) {
			this.values = ITEM_DROPS.values();
		}
		return this.values;
	}

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