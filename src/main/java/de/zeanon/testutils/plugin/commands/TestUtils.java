package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
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
						TestBlock.deleteBlock(p, args[1]);
					} else if (args[0].equalsIgnoreCase("registertg")) {
						final @NotNull String name = args.length > 1 ? args[1] : p.getName();
						TestAreaUtils.generate(new BukkitWorld(p.getWorld()),
											   p.getLocation().getBlockX(),
											   p.getLocation().getBlockY(),
											   p.getLocation().getBlockZ(),
											   name);
						p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
									  ChatColor.RED + "You created TG with name '" + ChatColor.DARK_RED + "testarea_" + name + "" + ChatColor.RED + "'");
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
					} else {
						p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
					}
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Missing argument.");
				}
			}
		}.runTaskAsynchronously(de.zeanon.testutils.TestUtils.getInstance());
	}
}