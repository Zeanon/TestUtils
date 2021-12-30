package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum SNOW_FORM implements Flag.Value<SNOW_FORM> {
	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull SNOW_FORM[] values = SNOW_FORM.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull SNOW_FORM[] getValues() {
		return SNOW_FORM.values;
	}

	@Override
	public @NotNull SNOW_FORM getValue() {
		return this;
	}

	@Override
	public @NotNull SNOW_FORM getValueOf(final @NotNull String name) {
		try {
			return SNOW_FORM.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return SNOW_FORM.DENY;
			}

			return SNOW_FORM.ALLOW;
		}
	}
}