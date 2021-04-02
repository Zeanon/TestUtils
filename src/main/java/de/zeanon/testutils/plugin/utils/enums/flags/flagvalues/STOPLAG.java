package de.zeanon.testutils.plugin.utils.enums.flags.flagvalues;


import de.zeanon.testutils.plugin.utils.enums.flags.Flag;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum STOPLAG implements Flag.Value<STOPLAG> {

	ACTIVE("activate", ChatColor.GREEN + "active"),
	INACTIVE("deactivate", ChatColor.RED + "inactive");


	@Getter
	private static final @NotNull Set<STOPLAG> values;

	static {
		values = new HashSet<>(Arrays.asList(STOPLAG.values()));
	}

	private final @NotNull String descriptor;
	private final @NotNull String chatValue;

	@Override
	public STOPLAG getValue() {
		return this;
	}

	@Override
	public STOPLAG getValueOf(final @NotNull String name) {
		try {
			return STOPLAG.valueOf(name);
		} catch (IllegalArgumentException e) {
			return STOPLAG.INACTIVE;
		}
	}

	@Override
	public String toString() {
		return this.descriptor;
	}
}