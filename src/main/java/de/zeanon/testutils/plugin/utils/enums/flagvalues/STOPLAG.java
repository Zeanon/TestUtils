package de.zeanon.testutils.plugin.utils.enums.flagvalues;


import de.zeanon.testutils.plugin.utils.enums.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum STOPLAG implements Flag.Value<STOPLAG> {

	ACTIVE("activate", ChatColor.GREEN + "active"),
	INACTIVE("deactivate", ChatColor.RED + "inactive");


	private final @NotNull String name;

	private final @NotNull String chatValue;

	@Override
	public STOPLAG getValue() {
		return this;
	}

	@Override
	public STOPLAG getValue(final @NotNull String name) {
		return STOPLAG.valueOf(name);
	}

	@Override
	public String toString() {
		return this.name;
	}
}