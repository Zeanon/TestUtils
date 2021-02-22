package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import de.zeanon.testutils.plugin.commands.testblock.DeleteBlock;
import de.zeanon.testutils.plugin.commands.testblock.RegisterBlock;
import de.zeanon.testutils.plugin.commands.testblock.RenameBlock;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.GlobalRequestUtils;
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
							RegisterBlock.registerBlock(p, null);
						} else if (args.length == 2) {
							RegisterBlock.registerBlock(p, args[1]);
						} else {
							p.sendMessage(ChatColor.RED + "Too many arguments.");
						}
					} else if (args[0].equalsIgnoreCase("deleteblock")) {
						DeleteBlock.execute(args, p);
					} else if (args[0].equalsIgnoreCase("renameblock")) {
						RenameBlock.execute(args, p);
					} else if (args[0].equalsIgnoreCase("registertg")) {
						final @NotNull String name = args.length > 1 ? args[1] : p.getName();
						TestAreaUtils.generate(new BukkitWorld(p.getWorld()),
											   p.getLocation().getBlockX(),
											   p.getLocation().getBlockY(),
											   p.getLocation().getBlockZ(),
											   name);
						p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
									  ChatColor.RED + "You created a testarea with the name '" + ChatColor.DARK_RED + "testarea_" + name + "" + ChatColor.RED + "'");
					} else if (args[0].equalsIgnoreCase("update")) {
						if (args.length == 1) {
							if (!Update.checkForUpdate()) {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
											  + ChatColor.RED + "You are already running the latest Version.");
							}
							GlobalMessageUtils.sendBooleanMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
																  + ChatColor.RED + "Do you really want to update?"
									, "/tu update confirm"
									, "/tu update deny"
									, p);
							GlobalRequestUtils.addUpdateRequest(p.getUniqueId().toString());
						} else if (args.length == 2
								   && (args[1].equalsIgnoreCase("confirm")
									   || args[1].equalsIgnoreCase("deny"))) {
							if (GlobalRequestUtils.checkUpdateRequest(p.getUniqueId().toString())) {
								GlobalRequestUtils.removeUpdateRequest(p.getUniqueId().toString());
								if (args[1].equalsIgnoreCase("confirm")) {
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
									p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
												  + ChatColor.RED + "Plugin will not be updated.");
								}
							} else {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
											  + ChatColor.RED + "You don't have a  pending update request.");
							}
						} else {
							p.sendMessage(ChatColor.RED + "Too many arguments.");
							TestUtils.sendUpdateUsage(p);
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

	public static void sendUpdateUsage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(ChatColor.RED + "Usage: ",
											  ChatColor.GRAY + "/testutils"
											  + ChatColor.AQUA + " update",
											  ChatColor.DARK_GREEN + ""
											  + ChatColor.UNDERLINE + ""
											  + ChatColor.ITALIC + ""
											  + ChatColor.BOLD + "!!UPDATE BABY!!",
											  "/tu update", p);
	}
}