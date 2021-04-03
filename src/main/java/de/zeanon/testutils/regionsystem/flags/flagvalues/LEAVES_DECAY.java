package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum LEAVES_DECAY implements Flag.Value<LEAVES_DECAY> {

	ALLOW(ChatColor.GREEN + "allow", null),
	DENY(ChatColor.RED + "deny", null);


	private final @NotNull String chatValue;
	private @Nullable LEAVES_DECAY[] values;


	@Override
	public LEAVES_DECAY[] getValues() {
		if (this.values == null) {
			this.values = LEAVES_DECAY.values();
		}
		return this.values;
	}

	@Override
	public LEAVES_DECAY getValue() {
		return this;
	}

	@Override
	public LEAVES_DECAY getValueOf(final @NotNull String name) {
		try {
			return LEAVES_DECAY.valueOf(name);
		} catch (IllegalArgumentException e) {
			return LEAVES_DECAY.ALLOW;
		}
	}
}