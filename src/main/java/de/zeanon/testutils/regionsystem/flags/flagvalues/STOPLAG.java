package de.zeanon.testutils.regionsystem.flags.flagvalues;


import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum STOPLAG implements Flag.Value<STOPLAG> {

	ACTIVE("activate", ChatColor.GREEN + "active", null),
	INACTIVE("deactivate", ChatColor.RED + "inactive", null);


	private final @NotNull String descriptor;
	private final @NotNull String chatValue;
	private @Nullable STOPLAG[] values;


	@Override
	public STOPLAG[] getValues() {
		if (this.values == null) {
			this.values = STOPLAG.values();
		}
		return this.values;
	}

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