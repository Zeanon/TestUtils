package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.plugin.commands.TestUtils;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.GlobalRequestUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


public class SleepModeCommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("testutils")) {
			if (args[0].equalsIgnoreCase("update")) {
				Player p = (Player) sender;
				if (args.length == 1) {
					if (!Update.checkForUpdate()) {
						p.sendMessage(ChatColor.RED + "You are already running the latest Version.");
					}
					GlobalMessageUtils.sendBooleanMessage(ChatColor.RED + "Do you really want to update?"
							, "/tu update confirm"
							, "/tu update deny"
							, p);
					GlobalRequestUtils.addUpdateRequest(p.getUniqueId().toString());
				} else if (args.length == 2
						   && (args[1].equalsIgnoreCase("confirm")
							   || args[1].equalsIgnoreCase("deny"))
						   && GlobalRequestUtils.checkUpdateRequest(p.getUniqueId().toString())) {
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
						p.sendMessage(ChatColor.DARK_PURPLE + de.zeanon.testutils.TestUtils.getInstance().getName()
									  + ChatColor.RED + " will not be updated.");
					}
				} else {
					p.sendMessage(ChatColor.RED + "Too many arguments.");
					TestUtils.sendUpdateUsage(p);
				}
			} else {
				sender.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
			}
		}
		return true;
	}
}
