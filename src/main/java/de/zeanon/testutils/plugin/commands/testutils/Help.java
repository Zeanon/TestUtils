package de.zeanon.testutils.plugin.commands.testutils;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Help {

	@SuppressWarnings("unused")
	public void execute(final @NotNull Player p) {
		if (ConfigUtils.getBoolean("Space Lists")) {
			p.sendMessage("");
		}

		p.sendMessage(ChatColor.DARK_GRAY + "=== "
					  + ChatColor.DARK_RED + "TestUtils "
					  + ChatColor.DARK_GRAY + "|"
					  + ChatColor.DARK_RED + " Version "
					  + TestUtils.getInstance().getDescription().getVersion()
					  + ChatColor.DARK_GRAY + " ===");
	}
}