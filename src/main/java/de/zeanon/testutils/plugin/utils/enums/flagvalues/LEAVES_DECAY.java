package de.zeanon.testutils.plugin.utils.enums.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum LEAVES_DECAY implements Flag.Value<LEAVES_DECAY> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private final @NotNull String chatValue;

	@Override
	public LEAVES_DECAY getValue() {
		return this;
	}

	@Override
	public LEAVES_DECAY getValue(final @NotNull String name) {
		return LEAVES_DECAY.valueOf(name);
	}
}