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

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static @Nullable FIRE[] values;
	private final @NotNull String chatValue;

	@Override
	public FIRE[] getValues() {
		if (FIRE.values == null) {
			FIRE.values = FIRE.values(); //NOSONAR
		}
		return FIRE.values;
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