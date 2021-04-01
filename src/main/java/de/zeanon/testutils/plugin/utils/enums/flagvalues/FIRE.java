package de.zeanon.testutils.plugin.utils.enums.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum FIRE implements Flag.Value<FIRE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private final @NotNull String chatValue;

	@Override
	public FIRE getValue() {
		return this;
	}

	@Override
	public FIRE getValue(final @NotNull String name) {
		return FIRE.valueOf(name);
	}
}