package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum FALL_DAMAGE implements Flag.Value<FALL_DAMAGE> {

	ALLOW(ChatColor.GREEN + "allow", null),
	DENY(ChatColor.RED + "deny", null);


	private final @NotNull String chatValue;
	private @Nullable FALL_DAMAGE[] values;


	@Override
	public FALL_DAMAGE[] getValues() {
		if (this.values == null) {
			this.values = FALL_DAMAGE.values();
		}
		return this.values;
	}

	@Override
	public FALL_DAMAGE getValue() {
		return this;
	}

	@Override
	public FALL_DAMAGE getValueOf(final @NotNull String name) {
		try {
			return FALL_DAMAGE.valueOf(name);
		} catch (IllegalArgumentException e) {
			return FALL_DAMAGE.ALLOW;
		}
	}
}
