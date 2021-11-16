package de.zeanon.testutils.plugin.update;

import com.rylinaux.plugman.util.PluginUtil;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
class PlugManEnabledUpdate {

	void updatePlugin(final boolean autoReload, final @NotNull JavaPlugin instance) {
		System.out.println("[" + instance.getName() + "] >> Plugin is updating...");
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					final @NotNull BukkitRunnable reloadRunnable = new BukkitRunnable() {
						@Override
						public void run() {
							PluginUtil.reload(instance);
						}
					};

					BaseFileUtils.writeToFile(new File(TestUtils.class.getProtectionDomain()
																	  .getCodeSource()
																	  .getLocation()
																	  .toURI()
																	  .getPath())
													  .getCanonicalFile(), new BufferedInputStream(
							new URL(Update.DOWNLOAD_URL)
									.openStream()));

					System.out.println("[" + instance.getName() + "] >> Plugin was updated successfully.");

					if (autoReload) {
						System.out.println("[" + instance.getName() + "] >> Plugin is reloading.");
						reloadRunnable.runTask(instance);
					}
				} catch (@NotNull final IOException |
						URISyntaxException e) {
					System.out.println("[" + instance.getName() + "] >> Plugin could not be updated.");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	void updatePlugin(final @NotNull Player p, final boolean autoReload, final @NotNull JavaPlugin instance) {
		p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + instance.getName() + ChatColor.DARK_GRAY + "] " +
					  ChatColor.RED + "Updating plugin...");
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					final @NotNull BukkitRunnable reloadRunnable = new BukkitRunnable() {
						@Override
						public void run() {
							PluginUtil.reload(instance);
						}
					};

					BaseFileUtils.writeToFile(new File(TestUtils.class.getProtectionDomain()
																	  .getCodeSource()
																	  .getLocation()
																	  .toURI()
																	  .getPath())
													  .getCanonicalFile(), new BufferedInputStream(
							new URL(Update.DOWNLOAD_URL)
									.openStream()));
					p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + instance.getName() + ChatColor.DARK_GRAY + "] " +
								  ChatColor.RED + "Update successful.");

					if (autoReload) {
						p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + instance.getName() + ChatColor.DARK_GRAY + "] " +
									  ChatColor.RED + "Reloading plugin...");
						reloadRunnable.runTask(instance);
					}
				} catch (@NotNull final IOException | URISyntaxException e) {
					p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + instance.getName() + ChatColor.DARK_GRAY + "] " +
								  ChatColor.RED + "Could not update.");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}