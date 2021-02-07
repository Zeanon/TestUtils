package de.zeanon.testutils.plugin.update;

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
import org.jetbrains.annotations.NotNull;


@UtilityClass
class DefaultUpdate {

	void updatePlugin(final @NotNull JavaPlugin instance) {
		System.out.println(instance.getName() + " is updating...");
		try {
			BaseFileUtils.writeToFile(new File(TestUtils.class.getProtectionDomain()
															  .getCodeSource()
															  .getLocation()
															  .toURI()
															  .getPath())
											  .getCanonicalFile(),
									  new BufferedInputStream(
											  new URL(Update.DOWNLOAD_URL)
													  .openStream()));
			System.out.println(instance.getName() + " was updated successfully.");
		} catch (@NotNull IOException | URISyntaxException e) {
			e.printStackTrace();
			System.out.println(instance.getName() + " could not be updated.");
		}
	}

	void updatePlugin(final @NotNull Player p, final @NotNull JavaPlugin instance) {
		p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + instance.getName() + ChatColor.DARK_GRAY + "] " +
					  ChatColor.RED + "updating plugin...");
		try {
			BaseFileUtils.writeToFile(new File(TestUtils.class.getProtectionDomain()
															  .getCodeSource()
															  .getLocation()
															  .toURI()
															  .getPath())
											  .getCanonicalFile(),
									  new BufferedInputStream(
											  new URL(Update.DOWNLOAD_URL)
													  .openStream()));
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + instance.getName() + ChatColor.DARK_GRAY + "] " +
						  ChatColor.RED + "update successful.");
		} catch (@NotNull IOException | URISyntaxException e) {
			e.printStackTrace();
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + instance.getName() + ChatColor.DARK_GRAY + "] " +
						  ChatColor.RED + "could not update.");
		}
	}
}