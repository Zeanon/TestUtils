package de.zeanon.testutils.regionsystem.commands;

import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.regionsystem.RegionManager;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Save {

	public void execute(final @NotNull Player p) {
		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "Saving all region files...");
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					RegionManager.saveRegions();
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED + "Saved all region files.");
				} catch (final @NotNull RuntimeIOException e) {
					e.printStackTrace();
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED + "There has been an error saving the region files, for more information please see [console]");
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}