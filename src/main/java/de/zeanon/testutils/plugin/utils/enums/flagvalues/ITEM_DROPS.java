package de.zeanon.testutils.plugin.utils.enums.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum ITEM_DROPS implements Flag.Value<ITEM_DROPS> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private final @NotNull String chatValue;

	@Override
	public ITEM_DROPS getValue() {
		return this;
	}

	@Override
	public ITEM_DROPS getValue(final @NotNull String name) {
		return ITEM_DROPS.valueOf(name);
	}
}