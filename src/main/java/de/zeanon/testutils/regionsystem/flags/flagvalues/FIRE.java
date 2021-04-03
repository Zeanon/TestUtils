package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum FIRE implements Flag.Value<FIRE> {

	ALLOW(ChatColor.GREEN + "allow", null),
	DENY(ChatColor.RED + "deny", null);


	private final @NotNull String chatValue;
	private @Nullable FIRE[] values;


	@Override
	public FIRE[] getValues() {
		if (this.values == null) {
			this.values = FIRE.values();
		}
		return this.values;
	}

	@Override
	public FIRE getValue() {
		return this;
	}

	@Override
	public FIRE getValueOf(final @NotNull String name) {
		try {
			return FIRE.valueOf(name);
		} catch (IllegalArgumentException e) {
			return FIRE.ALLOW;
		}
	}
}