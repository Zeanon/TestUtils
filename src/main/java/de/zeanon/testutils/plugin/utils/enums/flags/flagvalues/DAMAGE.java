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
public enum DAMAGE implements Flag.Value<DAMAGE> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	@Getter
	private static final @NotNull Set<DAMAGE> values;

	static {
		values = EnumSet.allOf(DAMAGE.class);
	}

	private final @NotNull String chatValue;

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