package de.zeanon.testutils.regionsystem.flags.flagvalues;


import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum STOPLAG implements Flag.Value<STOPLAG> {

	ACTIVE("activate", ChatColor.GREEN + "active"),
	INACTIVE("deactivate", ChatColor.RED + "inactive");


	private static final @NotNull STOPLAG[] values = STOPLAG.values();
	private final @NotNull String descriptor;
	private final @NotNull String chatValue;


	@Override
	public @NotNull STOPLAG[] getValues() {
		return STOPLAG.values;
	}

	@Override
	public @NotNull STOPLAG getValue() {
		return this;
	}

	@Override
	public @NotNull STOPLAG getValueOf(final @NotNull String name) {
		try {
			return STOPLAG.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("true")) {
				return STOPLAG.ACTIVE;
			}

			return STOPLAG.INACTIVE;
		}
	}

	@Override
	public String toString() {
		return this.descriptor;
	}
}