package de.zeanon.testutils.plugin.utils.enums.flags.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.TNTMode;
import de.zeanon.testutils.plugin.utils.enums.flags.Flag;
import java.util.EnumSet;
import java.util.Set;
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


	@Getter
	private static final @NotNull Set<TNT> values;

	static {
		values = EnumSet.allOf(TNT.class);
	}

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
	public TNT getValueOf(final @NotNull String name) {
		try {
			return TNT.valueOf(name);
		} catch (IllegalArgumentException e) {
			return TNT.ALLOW;
		}
	}
}