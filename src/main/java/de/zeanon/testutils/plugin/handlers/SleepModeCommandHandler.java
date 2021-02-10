package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.plugin.update.Update;
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
				if (Bukkit.getVersion().contains("git-Paper")) {
					Update.updatePlugin((Player) sender, de.zeanon.testutils.TestUtils.getInstance());
				} else {
					new BukkitRunnable() {
						@Override
						public void run() {
							Update.updatePlugin((Player) sender, de.zeanon.testutils.TestUtils.getInstance());
						}
					}.runTask(de.zeanon.testutils.TestUtils.getInstance());
				}
			} else {
				sender.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
			}
		}
		return true;
	}
}
