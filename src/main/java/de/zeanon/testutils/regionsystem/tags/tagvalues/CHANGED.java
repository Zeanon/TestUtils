package de.zeanon.testutils.regionsystem.tags.tagvalues;

import de.zeanon.testutils.regionsystem.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;


@Getter
@AllArgsConstructor
public enum CHANGED implements Tag.Value<CHANGED> {

	TRUE(ChatColor.GREEN + "true", ChatColor.RED + "The given region has been changed since the last Backup and thus will be saved at the next hourly Backup."),
	FALSE(ChatColor.RED + "false", ChatColor.RED + "The given region has not been changed since the last Backup and thus will not be saved at the next hourly Backup.");


	private static final @NotNull CHANGED[] values = CHANGED.values();
	private final @NotNull String chatValue;
	private final @NotNull String description;


	@Override
	public @NotNull CHANGED[] getValues() {
		return CHANGED.values;
	}

	@Override
	public @NotNull CHANGED getValue() {
		return this;
	}

	@Override
	public @NotNull CHANGED getValueOf(final @NotNull String name) {
		try {
			return CHANGED.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException e) {
			return CHANGED.FALSE;
		}
	}
}