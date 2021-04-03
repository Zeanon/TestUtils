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

	ALLOW(ChatColor.GREEN + "allow", null),
	DENY(ChatColor.RED + "deny", null);


	private final @NotNull String chatValue;
	private @Nullable TNT[] values;


	@Override
	public TNT[] getValues() {
		if (this.values == null) {
			this.values = TNT.values();
		}
		return this.values;
	}

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