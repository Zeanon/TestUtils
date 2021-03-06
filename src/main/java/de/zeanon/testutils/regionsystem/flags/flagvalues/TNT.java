package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.TNTMode;
import de.zeanon.testutils.regionsystem.flags.Flag;
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


	private static final @NotNull TNT[] values = TNT.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull TNT[] getValues() {
		return TNT.values;
	}

	@Override
	public @NotNull TNT getValue() {
		return this;
	}

	public @NotNull TNT getValue(final @Nullable TNTMode tntMode) {
		if (tntMode == null) {
			return this == TNT.ALLOW ? TNT.DENY : TNT.ALLOW;
		} else {
			return tntMode.getValue();
		}
	}

	@Override
	public @NotNull TNT getValueOf(final @NotNull String name) {
		try {
			return TNT.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return TNT.DENY;
			}

			return TNT.ALLOW;
		}
	}
}