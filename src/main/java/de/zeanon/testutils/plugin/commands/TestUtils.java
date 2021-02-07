package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class TestUtils {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("registerblock")) {
						if (args.length == 1) {
							TestBlock.registerBlock(p, null);
						} else if (args.length == 2) {
							TestBlock.registerBlock(p, args[1]);
						} else {
							p.sendMessage(ChatColor.RED + "Too many arguments.");
						}
					} else if (args.length == 2 && args[0].equalsIgnoreCase("deleteblock")) {
						final @NotNull File tempFile = new File(de.zeanon.testutils.TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString(), args[1] + ".schem");
						if (tempFile.exists() && tempFile.isFile()) {
							try {
								Files.delete(tempFile.toPath());
								p.sendMessage(ChatColor.RED + "'" + BaseFileUtils.removeExtension(tempFile.getName()) + "' has been deleted.");
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							p.sendMessage(ChatColor.RED + "You have no TestBlock with the given name.");
						}
					} else if (args[0].equalsIgnoreCase("registertg")) {
						final @NotNull String name = args.length > 1 ? args[1] : p.getName();
						TestAreaUtils.generate(new BukkitWorld(p.getWorld()),
											   p.getLocation().getBlockX(),
											   p.getLocation().getBlockY(),
											   p.getLocation().getBlockZ(),
											   name);
						p.sendMessage(ChatColor.GOLD + "Created TG with name 'testarea_" + name + "'");
					} else if (args[0].equalsIgnoreCase("update")) {
						if (Bukkit.getVersion().contains("git-Paper")) {
							Update.updatePlugin(p, de.zeanon.testutils.TestUtils.getInstance());
						} else {
							new BukkitRunnable() {
								@Override
								public void run() {
									Update.updatePlugin(p, de.zeanon.testutils.TestUtils.getInstance());
								}
							}.runTask(de.zeanon.testutils.TestUtils.getInstance());
						}
					}
				} else {
					p.sendMessage(ChatColor.RED + "Missing argument.");
				}
			}
		}.runTaskAsynchronously(de.zeanon.testutils.TestUtils.getInstance());
	}
}