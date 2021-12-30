package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum ICE_FORM implements Flag.Value<ICE_FORM> {
	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static final @NotNull ICE_FORM[] values = ICE_FORM.values();
	private final @NotNull String chatValue;


	@Override
	public @NotNull ICE_FORM[] getValues() {
		return ICE_FORM.values;
	}

	@Override
	public @NotNull ICE_FORM getValue() {
		return this;
	}

	@Override
	public @NotNull ICE_FORM getValueOf(final @NotNull String name) {
		try {
			return ICE_FORM.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			if (name.equalsIgnoreCase("false")) {
				return ICE_FORM.DENY;
			}

			return ICE_FORM.ALLOW;
		}
	}
}