package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum DAMAGE implements Flag.Value<DAMAGE> {

	ALLOW(ChatColor.GREEN + "allow", null),
	DENY(ChatColor.RED + "deny", null);


	private final @NotNull String chatValue;
	private @Nullable DAMAGE[] values;


	@Override
	public DAMAGE[] getValues() {
		if (this.values == null) {
			this.values = DAMAGE.values();
		}
		return this.values;
	}

	@Override
	public DAMAGE getValue() {
		return this;
	}

	@Override
	public DAMAGE getValueOf(final @NotNull String name) {
		try {
			return DAMAGE.valueOf(name);
		} catch (IllegalArgumentException e) {
			return DAMAGE.ALLOW;
		}
	}
}