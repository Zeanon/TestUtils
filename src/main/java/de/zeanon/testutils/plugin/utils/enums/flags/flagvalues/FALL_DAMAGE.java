package de.zeanon.testutils.plugin.utils.enums.flags.flagvalues;

import de.zeanon.testutils.plugin.utils.enums.flags.Flag;
import java.util.EnumSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum FALL_DAMAGE implements Flag.Value<FALL_DAMAGE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	@Getter
	private static final @NotNull Set<FALL_DAMAGE> values;

	static {
		values = EnumSet.allOf(FALL_DAMAGE.class);
	}

	private final @NotNull String chatValue;

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
