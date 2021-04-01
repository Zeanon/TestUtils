package de.zeanon.testutils.plugin.utils.enums.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.Flag;
import de.zeanon.testutils.plugin.utils.enums.TNTMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum TNT implements Flag.Value<TNT> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private final @NotNull String chatValue;

	@Override
	public TNT getValue() {
		return this;
	}

	public TNT getValue(final @Nullable TNTMode tntMode) {
		if (tntMode == null) {
			return this == TNT.ALLOW ? TNT.DENY : TNT.ALLOW;
		} else {
			return tntMode.getValue();
		}
	}

	@Override
	public TNT getValue(final @NotNull String name) {
		return TNT.valueOf(name);
	}
}