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

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static @Nullable DAMAGE[] values;
	private final @NotNull String chatValue;

	@Override
	public DAMAGE[] getValues() {
		if (DAMAGE.values == null) {
			DAMAGE.values = DAMAGE.values(); //NOSONAR
		}
		return DAMAGE.values;
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