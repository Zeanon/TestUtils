package de.zeanon.testutils.plugin.commands.region;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Reload {

	public void execute(final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "Reloading all region files...");
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					RegionManager.initialize();
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Reloaded all region files.");
				} catch (IOException e) {
					e.printStackTrace();
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "There has been an error reloading the region files, for more information please see [console]");
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}