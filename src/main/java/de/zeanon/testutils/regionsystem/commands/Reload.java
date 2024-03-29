package de.zeanon.testutils.regionsystem.commands;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.regionsystem.RegionManager;
import java.io.IOException;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Reload {

	public void execute(final @NotNull Player p) {
		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "Reloading all region files...");
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					RegionManager.reloadRegions();
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED + "Reloaded all region files.");
				} catch (final IOException e) {
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED + "There has been an error reloading the region files, for more information please see [console]");
					TestUtils.getChatLogger().log(Level.SEVERE, "Error while reloading the region files", e);
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}